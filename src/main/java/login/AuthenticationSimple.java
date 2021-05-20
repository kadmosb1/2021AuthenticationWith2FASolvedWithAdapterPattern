package login;

import logging.Logging;
import tfa.Authentication2FA;

/*
 * Bij eenvoudige authenticatie volstaat een gebruikersnaam voor inloggen.
 */
public class AuthenticationSimple extends Authentication {

    private static AuthenticationSimple singleton;

    private AuthenticationSimple() {
        super();
    }

    /*
     * Implementatie van getInstance die nodig is om het Singleton Pattern toe te passen.
     */
    protected static Authentication getNewInstance() {

        if (singleton == null) {
            singleton = new AuthenticationSimple();
        }

        return singleton;
    }

    /*
     * Het aanmaken van een eventuele singleton verloopt via Authentication.
     */
    public static Authentication getInstance() {
        return getInstance(AuthenticationSimple.class);
    }

    /*
     * De ingelogde gebruiker wordt opgezocht (als die er is).
     */
    @Override
    protected User getAuthenticatedUser() {
        return getActiveUser();
    }

    /*
     * Als er nog geen gebruiker is ingelogd, kan een nieuwe gebruiker inloggen (bij eenvoudige authenticatie is het
     * voldoende dat een gebruiker daarvoor zijn gebruikersnaam verstrekt.
     */
    @Override
    protected boolean authenticate() {

        for (int i = 0; i < 3; i++) {

            String userName = readUserName ();
            User user = getUser (userName);

            if (user != null) {
                user.setActive ();
                return true;
            }
            else {
                printMessage ("Unknown user. " + (2 - i) + " attempts left.");
                Logging.getInstance ().printLog (String.format ("Unknown user for user '%s'", userName));
            }
        }

        return false;
    }

    /*
     * Een gebruiker kan direct (zonder toetsenbord) worden ingelegd met gebruikersnaam en password.
     */
    @Override
    protected boolean authenticate (User user, String... passwordAndOrPTOP) {

        if (user != null) {

            user.setActive();

            // Hoewel een password bij eenvoudige authenticatie overbodig is, wordt een eventueel verstrekt password wel
            // gebruikt om een gebruiker in te loggen (er wordt alleen niet gecontroleerd of dat is gelukt.
            if (passwordAndOrPTOP.length >= 1) {
                user.authenticate(passwordAndOrPTOP[0]);
            }

            // Hoewel een Authenticatorcode bij eenvoudige authenticatie overbodig is, wordt een eventuele opgegeven
            // POTP wel geregistreerd.
            if (passwordAndOrPTOP.length >= 2) {
                user.authenticateWith2FA (passwordAndOrPTOP[1]);
            }
        }

        return true;
    }
}