package login;

import logging.Logging;

import java.util.Scanner;

public class Authentication2FAAdapter extends Authentication {

    private static Authentication2FAAdapter singleton;

    private Authentication2FAAdapter () {
        super ();
    }

    /*
     * Implementatie van getInstance die nodig is om het Singleton Pattern toe te passen.
     */
    protected static Authentication getNewInstance () {

        if (singleton == null) {
            singleton = new Authentication2FAAdapter ();
        }

        return singleton;
    }

    /*
     * Het aanmaken van een eventuele singleton verloopt via Authentication.
     */
    public static Authentication getInstance () {
        return getInstance (Authentication2FAAdapter.class);
    }

    @Override
    protected User getAuthenticatedUser() {

        User user = getActiveUser();

        if ((user != null) && user.isAuthenticatedWith2FA ()) {
            return user;
        }
        else {
            return null;
        }
    }

    @Override
    protected boolean authenticate() {

        Scanner scanner = new Scanner (System.in);

        // Als er al een user geauthenticeerd is met 2FA, dan wordt deze user als ingelogd beschouwd.
        if (getAuthenticatedUser () != null) {
            return true;
        }

        // Het inloggen met gebruikersnaam en password wordt afgehandeld in AuthenticationNormal.
        Authentication normal = AuthenticationNormal.getInstance ();

        boolean isAuthenticated = normal.authenticate ();

        if (!isAuthenticated) {
            return false;
        }

        // Het is nu zeker dat de gebruiker is ingelogd met gebruikersnaam en password.
        User user = normal.getAuthenticatedUser ();

        // Als de gebruiker nog geen 2FA heeft geactiveerd, wordt dat nu gedaan.

        if (! user.activate2FA ()) {
            return false;
        }

        // Het is nu zeker dat 2FA is geactiveerd voor een gebruiker.
        // De gebruiker krijgt nu drie kansen om met 2FA-code in te loggen.
        for (int i = 0; i < 3; i++) {

            System.out.print ("Voer de code uit uw 2FA-app in: ");

            if (user.authenticateWith2FA (scanner.nextLine ())) {
                return true;
            }

            System.out.printf ("=====> Code is niet correct. U kunt het nog %d keer proberen.%n", 2 - i);
            Logging.getInstance ().printLog ("Incorrect PTOP-code entered.");
        }

        return false;
    }

    @Override
    protected boolean authenticate (User user, String... password) {
        return false;
    }
}
