package lol.nebula.account;

/**
 * @author aesthetical
 * @since 05/06/23
 */
public class Account {
    private final String email, password;

    public Account(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
