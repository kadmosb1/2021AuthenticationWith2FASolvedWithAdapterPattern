package login;

import logging.Logging;

/*
 * Bij normale authenticatie wordt bij inloggen gevraagd om gebruikersnaam en password.
 */
public class AuthenticationNormal extends Authentication {

    private static AuthenticationNormal singleton;

    private AuthenticationNormal () {
        super ();
    }

    /*
     * Implementatie van getInstance die nodig is om het Singleton Pattern toe te passen.
     */
    protected static Authentication getNewInstance () {

        if (singleton == null) {
            singleton = new AuthenticationNormal();
        }

        return singleton;
    }

    /*
     * Het aanmaken van een eventuele singleton verloopt via Authentication.
     */
    public static Authentication getInstance () {
        return getInstance (AuthenticationNormal.class);
    }

    /*
     * De ingelogde gebruiker wordt opgezocht (als die er is).
     */
    @Override
    protected User getAuthenticatedUser () {

        User user = getActiveUser();

        if ((user != null) && user.isAuthenticated ()) {
            return user;
        }
        else {
            return null;
        }
    }

    /*
     * Als er nog geen gebruiker is ingelogd, kan een nieuwe gebruiker inloggen.
     */
    @Override
    protected boolean authenticate () {

        // Als er al een gebruiker actief is (die nog niet is ingelogd), hoeft deze alleen nog een password in te
        // voeren.
        User activeUser = getActiveUser ();

        if ((activeUser != null) && activeUser.isAuthenticated ()) {
            return true;
        }
        else {

            for (int i = 0; i < 3; i++) {

                String userName = "";
                User user = activeUser;

                if (user == null) {
                    userName = readUserName();
                    user = getUser (userName);
                }

                if (user != null) {
                    String password = readPassword ();
                    return user.authenticate (password);
                }
                else {
                    printMessage ("Unknown user or incorrect password. " + (2 - i) + " attempts left.");
                    Logging.getInstance ().printLog (String.format ("Unknown user/incorrect password for user '%s'", userName));
                }
            }
        }

        return false;
    }

    /*
     * Een gebruiker kan direct (zonder toetsenbord) worden ingelegd met gebruikersnaam en password.
     */
    protected boolean authenticate (User user, String... passwordAndOrPTOP) {

        // Als het password van de nieuwe gebruiker klopt, wordt hij ingelogd. Anders kan hij met een ander (het
        // correcte) password inloggen.
        if ((passwordAndOrPTOP.length >= 1) && user.authenticate (passwordAndOrPTOP [0])) {

            // Hoewel een Authenticatorcode bij normale authenticatie overbodig is, wordt een eventuele opgegeven
            // POTP wel geregistreerd.
            if (passwordAndOrPTOP.length == 2) {
                user.authenticateWith2FA (passwordAndOrPTOP [1]);
            }

            return true;
        }
        else {
            user.setActive ();
            return authenticate ();
        }
    }
}