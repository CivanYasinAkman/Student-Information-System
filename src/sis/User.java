package sis;

public class User {

    private String username;
    private String password;
    private String role; // ADMIN, INSTRUCTOR, STUDENT
    private String fullName;
    private String referenceId;

    public User(String username, String password, String role, String fullName, String referenceId) {
        this.username    = username;
        this.password    = password;
        this.role        = role;
        this.fullName    = fullName;
        this.referenceId = referenceId;
    }

    public String getUsername()    { return username; }
    public String getPassword()    { return password; }
    public String getRole()        { return role; }
    public String getFullName()    { return fullName; }
    public String getReferenceId() { return referenceId; }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // save to file as pipe separated line
    public String toFileLine() {
        return username + "|" + password + "|" + role + "|" + fullName + "|" + referenceId;
    }

    // read from file line
    public static User fromFileLine(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 5) return null;
        return new User(parts[0], parts[1], parts[2], parts[3], parts[4]);
    }

    public String toString() {
        return fullName + " (" + role + ")";
    }
}
