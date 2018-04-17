package windshift.windhound.objects;

public class UserDTO {

    private String username;
    private String password;
    private String name;
    private String email;

    //not used, only present for spring
    private String[] roles;

    public UserDTO() {

    }

    public UserDTO(String username, String password, String name, String email, String... roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;

        this.name = name;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

