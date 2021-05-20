package login;

import tfa.Authentication2FA;

class User {

    private String userName;
    private String password;
    private boolean isActive;
    private boolean isAuthenticated;
    private boolean isAuthenticatedWith2FA;
    private String secretKey;

    protected User (String userName, String password, String... secretKey) {

        this.userName = userName;
        this.password = password;
        this.isActive = false;
        this.isAuthenticated = false;
        this.isAuthenticatedWith2FA = false;

        if (secretKey.length == 1) {
            this.secretKey = secretKey [0];
        }
    }

    protected String getUserName () {
        return userName;
    }

    protected String getPassword () {
        return password;
    }

    protected boolean isActive () {
        return isActive || isAuthenticated || isAuthenticatedWith2FA;
    }

    protected void setActive () {
        isActive = true;
    }

    protected boolean isAuthenticated () {
        return isAuthenticated || isAuthenticatedWith2FA;
    }

    protected boolean authenticate (String password) {

        if (this.password.equals (password)) {
            isAuthenticated = true;
            return true;
        }

        return false;
    }

    protected boolean activate2FA () {

        if (secretKey == null) {
            secretKey = Authentication2FA.activate (userName, "Invoicing");
        }

        return (secretKey != null);
    }

    protected boolean authenticateWith2FA (String TFACode) {

        if (Authentication2FA.checkAuthenticatorCode (secretKey, TFACode)) {
            isAuthenticatedWith2FA = true;
            return true;
        }

        return false;
    }

    protected boolean isAuthenticatedWith2FA () {
        return isAuthenticatedWith2FA;
    }

    public void logout () {
        isActive = false;
        isAuthenticated = false;
        isAuthenticatedWith2FA = false;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) {
            return true;
        }

        if ((o == null) || (getClass () != o.getClass())) {
            return false;
        }

        User user = (User) o;
        return (this.userName.equals (user.getUserName ())) && (password.equals (user.getPassword ()));
    }
}