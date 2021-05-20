package login;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationTest {

    @Test
    public void authenticateTest () {
        User user1 = new User ("user1", "1");
        User user2 = new User ("user2", "2");

        /*
         * Inloggen met normale authenticatie wordt getest voor user1.
         */
        Authentication authentication = AuthenticationNormal.getInstance ();
        authentication.authenticate ("user1", "1");
        assertTrue (authentication.userIsAuthenticated ());
        assertEquals (user1, authentication.getAuthenticatedUser());
        assertEquals (user1, authentication.getActiveUser ());

        /*
         * Gelden dezelfde voorwaarden als gebruik wordt gemaakt van eenvoudige authenticatie
         * (met dezelfde gebruiker user1)?
         */
        authentication = AuthenticationSimple.getInstance ();
        assertTrue (authentication.userIsAuthenticated ());
        assertEquals (user1, authentication.getAuthenticatedUser());
        assertEquals (user1, authentication.getActiveUser ());

        /*
         * Nog steeds gebruik makend van eenvoudige authenticatie: werkt dit ook voor user2?
         */
        authentication.authenticate ("user2");
        assertTrue (authentication.userIsAuthenticated ());
        assertEquals (user2, authentication.getAuthenticatedUser ());
        assertEquals (user2, authentication.getActiveUser ());

        /*
         * En - als de gebruiker nog geen password heeft gegeven: wat gebeurt er als wordt gecontroleerd of user2
         * is ingelogd bij normale authenticatie?
         */
        authentication = AuthenticationNormal.getInstance ();
        assertNull (authentication.getAuthenticatedUser());
        assertEquals (user2, authentication.getActiveUser ());

        /*
         * En terug naar de startsituatie.
         */
        authentication.authenticate ("user1", "1");
        assertTrue (authentication.userIsAuthenticated());
        assertEquals (user1, authentication.getAuthenticatedUser());
        assertEquals (user1, authentication.getActiveUser ());
    }
}