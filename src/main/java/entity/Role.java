package entity;

import java.util.Arrays;
import java.util.Optional;

public enum Role {
    ADMIN_ROLE, USER_ROLE;

    public static Optional<Role> find(String role){
        return Arrays.stream(values())
                .filter(r -> r.name()
                .equals(role)).findFirst();
    }
}
