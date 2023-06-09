package ru.yandex.praktikum.user;

public class UserCredentials {

    private String email;
    private String password;

    static UserGenerator userGenerator = new UserGenerator();

    public static String fakeEmail = userGenerator.fakeUserEmail();
    public static String fakePassword = userGenerator.fakeUserPassword();
    public static String fakeName = userGenerator.fakeUserName();
    public static User userWithoutEmail = new User(fakeName, "", fakePassword);
    public static User userWithoutPassword = new User(fakeName, fakeEmail, "");
    public static User userWithoutName = new User("", fakeEmail, fakePassword);
    public static User user = new User(fakeName, fakeEmail, fakePassword);
    public static String newFakePassword = userGenerator.fakeUserPassword();
    public static String newFakeEmail = userGenerator.fakeUserEmail();
    public static String newFakeName = userGenerator.fakeUserName();
    public static User newUser = new User(newFakeName, newFakeEmail, newFakePassword);

    public UserCredentials() {
    }

    public UserCredentials(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static UserCredentials from(User user){
        return new UserCredentials(user.getEmail(), user.getPassword());
    }

}
