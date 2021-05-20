package login;

import jdk.jfr.StackTrace;
import logging.Logging;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Scanner;

public abstract class Authentication {

    private static final Scanner scanner = new Scanner (System.in);
    private static Authentication singleton;
    private ArrayList<User> users;

    /*
     * Omdat de constructor bereikbaar moet zijn voor de subclasses (in de constructor daarvan), moet deze veranderen
     * van private (wat gewenst is voor toepassing van het Singleton Pattern) naar protected.
     */
    protected Authentication () {
        users = new ArrayList<>();
        users.add (new User ("user1", "1", "2V3H5SSTZ6D7ENRWRVG5IFHWYU22H4D3"));
        users.add (new User ("user2", "2", "2V3H5SSTZ6D7ENRWRVG5IFHWYU22H4D3"));
        users.add (new User ("user3", "3", "2V3H5SSTZ6D7ENRWRVG5IFHWYU22H4D3"));
    }

    private ArrayList<User> getUsers () {
        return users;
    }

    private void setUsers (ArrayList<User> users) {
        this.users = users;
    }

    protected static Authentication getInstance (Class classDefinition) {

        try {

            // Er wordt een object aangemaakt dat verwijst naar de static methode 'getNewInstance' die dan wel
            // gedefinieerd moet zijn in de subclass van Authentication (bijv. AuthenticationSimple) die als parameter
            // van het type Class is meegegeven.
            Method method = classDefinition.getDeclaredMethod ("getNewInstance");
            method.setAccessible (true);

            if (singleton == null) {
                singleton = (Authentication) method.invoke(null);
            }

            // Als wordt gewisseld tussen eenvoudige en normale authenticatie (of andersom), moet de lijst met gebruikers
            // (inclusief de actieve of ingelogde gebruiker in die lijst) worden bewaard in het nieuwe
            // object.
            else if (!singleton.getClass().getName().equals(classDefinition.getName())) {
                ArrayList<User> activeUsers = singleton.getUsers();
                singleton = (Authentication) method.invoke (null);
                singleton.setUsers(activeUsers);
            }

            method.setAccessible (false);
        }
        catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
            Logging.getInstance ().printLog (e);
        }

        return singleton;
    }

    /*
     * In de lijst met users wordt gezocht naar een gebruiker met userName.
     */
    protected User getUser (String userName) {

        for (User user : users) {

            if (user.getUserName().equals (userName)) {
                return user;
            }
        }

        return null;
    }

    /*
     * In de lijst met user wordt gezocht naar een actieve gebruiker (die misschien zelfs is ingelogd).
     */
    protected User getActiveUser () {

        for (User user : users) {

            if (user.isActive ()) {
                return user;
            }
        }

        return null;
    }

    /*
     * De gebruikersnaam van de actieve gebruiker wordt opgezocht.
     */
    public String getUserNameOfActiveUser () {

        User activeUser = getActiveUser ();

        if (activeUser != null) {
            return activeUser.getUserName ();
        }
        else {
            return "unknown";
        }
    }

    /*
     * De naam van een gebruiker wordt ingelezen vanaf het toetsenbord.
     */
    protected String readUserName () {
        System.out.print ("Voer uw gebruikersnaam in: ");
        return scanner.nextLine ();
    }

    /*
     * Het password van een gebruiker wordt ingelezen vanaf het toetsenbord.
     */
    protected String readPassword () {
        System.out.print ("Voer uw password in: ");
        return scanner.nextLine ();
    }

    /*
     * Een melding (bijv. 'Mislukt') wordt in het volgende formaat getoond:
     *
     *      ========================================
     *      = Mislukt                              =
     *      ========================================
     */
    protected void printMessage (String message) {
        System.out.println ("=".repeat (40));
        int numberOfSpaces = message.length () > 36 ? 0 : 40 - message.length ();
        System.out.printf ("= %s%s =", message, " ".repeat (numberOfSpaces));
        System.out.println ("=".repeat (40));
    }

    /*
     * De actieve gebruiker wordt uitgelogd.
     */
    public void logout () {

        User user = getActiveUser ();

        if (user != null) {
            user.logout ();
        }
    }

    protected abstract User getAuthenticatedUser ();
    protected abstract boolean authenticate ();
    protected abstract boolean authenticate (User user, String... passwordAndOrPTOP);

    /*
     * Dit is nu een template method om de zelfde stappen bij lichte (simple) en normale authenticatie te doorlopen.
     */
    public boolean authenticate (String userName, String... password) {

        // Als de ingelogde gebruiker de gebruiker met userName is, is hij al ingelogd.
        User user = getAuthenticatedUser ();

        if ((user != null) && (user.getUserName ().equals (userName))) {
            return true;
        }

        // Als de gebruiker niet bestaat kan hij/zij ook niet inloggen.
        user = getUser (userName);

        if (user == null) {
            Logging.getInstance ().printLog (String.format ("Authentication:authenticat - unknow user with username '%s' tried to login.", userName));
            return false;
        }
        else {
            // Een eventuele gebruiker die al was ingelogd, wordt uitgelogd. Daarna wordt de nieuwe gebruiker ingelogd.
            logout ();
            return authenticate (user, password);
        }
    }

    /*
     * Dit is nu een template method om de zelfde stappen bij lichte (simple) en normale authenticatie te doorlopen.
     */
    public boolean userIsAuthenticated () {

        // Als er nog geen gebruiker is ingelogd, wordt die volgens de methode voor lichte of normale authenticatie
        // ingelogd.
        if (getAuthenticatedUser () != null) {
            return true;
        }
        else {
            return authenticate ();
        }
    }
}