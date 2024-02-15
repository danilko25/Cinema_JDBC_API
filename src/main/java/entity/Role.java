package entity;

import java.util.Arrays;
import java.util.Optional;

public enum Role {
    USER_ROLE("User"), ADMIN_ROLE("Admin");

    Role(String title) {
        this.title = title;
    }

    private String title;

    public String getTitle() {
        return title;
    }

    public static Optional<Role> find(String role) {
        return Arrays.stream(values())
                .filter(r -> r.name()
                        .equals(role)).findFirst();
    }
}
