package wtf.nebula.client.impl.account;

public class Account {
    private final String email, password;
    private final AccountType accountType;

    public Account(String email, String password, AccountType accountType) {
        this.email = email;
        this.password = password;
        this.accountType = accountType;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public AccountType getAltType() {
        return accountType;
    }
}
