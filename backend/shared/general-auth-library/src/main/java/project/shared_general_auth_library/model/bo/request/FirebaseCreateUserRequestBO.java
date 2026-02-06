package project.shared_general_auth_library.model.bo.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FirebaseCreateUserRequestBO {
    private String uid;
    private String displayName;
    private String email;
    private String password;
    private boolean disable;
    private boolean emailVerified;
    private String phoneNumber;
    private String photoUrl;
}
