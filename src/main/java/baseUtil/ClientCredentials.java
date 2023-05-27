package baseUtil;
import lombok.Data;
import user.User;

@Data
public class ClientCredentials {
    private String email;
    private String password;
    private String name;

    public ClientCredentials(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public static ClientCredentials from(User user) {
        return new ClientCredentials(user.getEmail(), user.getPassword(), user.getName());
    }

    public static ClientCredentials fromOnlyEmailAndPassword(User user) {
        return new ClientCredentials(user.getEmail(), user.getPassword(), "");
    }

    public static ClientCredentials fromOnlyEmail(User user) {
        return new ClientCredentials(user.getEmail(), "", "");
    }

    public static ClientCredentials fromOnlyPassword(User user) {
        return new ClientCredentials("", user.getPassword(), "");
    }
}
