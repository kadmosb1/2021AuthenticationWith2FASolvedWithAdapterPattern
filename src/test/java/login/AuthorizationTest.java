package login;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthorizationTest {

    private static Authorization authorization;
    private static Authentication authentication;

    @BeforeAll
    public static void init () {
        authentication = AuthenticationNormal.getInstance ();
        authorization = Authorization.getInstance ();
    }

    @Test
    public void authorizeTest () {

        /*
         * Heeft user1 inderdaad toegang tot gegevens over klanten en facturen?
         */
        authentication.authenticate ("user1", "1");
        assertFalse (authorization.isAuthorized("product"));
        assertTrue (authorization.isAuthorized("customer"));
        assertTrue (authorization.isAuthorized("invoice"));

        /*
         * Heeft user2 inderdaad toegang tot gegevens over producten en facturen?
         */
        authentication.authenticate ("user2", "2");
        assertTrue (authorization.isAuthorized("product"));
        assertFalse (authorization.isAuthorized("customer"));
        assertTrue (authorization.isAuthorized("invoice"));

        /*
         * Heeft user3 inderdaad toegang tot alle beschikbare gegevens?
         */
        authentication.authenticate ("user3", "3");
        assertTrue (authorization.isAuthorized("product"));
        assertTrue (authorization.isAuthorized("customer"));
        assertTrue (authorization.isAuthorized("invoice"));

        /*
         * En na uitloggen? is de applicatie dan beschermd voor toegang tot de gegevens?
         */
        authentication.logout ();
        assertFalse (authorization.isAuthorized("invoice"));
        assertFalse (authorization.isAuthorized("customer"));
        assertFalse (authorization.isAuthorized("product"));
    }
}