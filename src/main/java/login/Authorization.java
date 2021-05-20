package login;

import java.util.ArrayList;
import java.util.Scanner;

public class Authorization {

    private static final Scanner scanner = new Scanner (System.in);
    private static Authorization singleton;
    private ArrayList<Role> roles;
    private Authentication authentication;

    private Authorization () {

        authentication = AuthenticationSimple.getInstance ();
        roles = new ArrayList<> ();

        Role role1 = new Role("customer");
        role1.addUser (authentication.getUser ("user1"));
        role1.addUser (authentication.getUser ("user3"));
        roles.add (role1);

        Role role2 = new Role("product");
        role2.addUser (authentication.getUser ("user2"));
        role2.addUser (authentication.getUser ("user3"));
        roles.add (role2);

        Role role3 = new Role("invoice");
        role3.addUser (authentication.getUser ("user1"));
        role3.addUser (authentication.getUser ("user2"));
        role3.addUser (authentication.getUser ("user3"));
        roles.add (role3);
    }

    public static Authorization getInstance () {

        if (singleton == null) {
            singleton = new Authorization ();
        }

        return singleton;
    }

    private Role getRole (String roleName) {

        for (Role role : roles) {

            if (role.getName ().equals (roleName)) {
                return role;
            }
        }

        return null;
    }

    public boolean isAuthorized (String roleName) {
        User user = authentication.getActiveUser ();
        Role role = getRole (roleName);
        return (role != null) && (role.userIsInRole (user));
    }
}