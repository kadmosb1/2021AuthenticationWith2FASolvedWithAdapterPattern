package invoice;

public class Login {

    /*
     * Als een gebruiker bekend is, maar zich niet met password hoeft the autoriseren, wordt dat hier gecontroleerd.
     */
    public static boolean isActive () {
        return login.AuthenticationSimple.getInstance ().userIsAuthenticated ();
    }

    /*
     * Als een gebruiker ingelogd moet zijn met password, wordt dat hier gecontroleerd.
     */
    public static boolean isAuthenticated () {
        return login.AuthenticationNormal.getInstance ().userIsAuthenticated ();
    }

    /*
     * Als een gebruiker ingelogd moet zijn met 2FA (gebruikersnaam, password en code), wordt dat hier gecontroleerd.
     */
    public static boolean isAuthenticatedWith2FA () {
        return login.Authentication2FAAdapter.getInstance ().userIsAuthenticated ();
    }

    /*
     * Hier wordt gecontroleerd of de ingelogde gebruiker een specifieke rol (roleName) heeft.
     */
    public static boolean isAuthorized (String roleName) {
        return login.Authorization.getInstance ().isAuthorized (roleName);
    }
}
