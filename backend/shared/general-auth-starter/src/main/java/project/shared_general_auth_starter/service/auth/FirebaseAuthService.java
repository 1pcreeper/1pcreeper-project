package project.shared_general_auth_starter.service.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.shared_general_auth_starter.constant.FirebaseClaimKeysConstant;
import project.shared_general_auth_starter.model.bo.request.FirebaseCreateUserRequestBO;

import java.util.List;
import java.util.Map;

@Service
public class FirebaseAuthService {
    private final FirebaseAuth firebaseAuth;

    @Autowired
    public FirebaseAuthService(
        FirebaseAuth firebaseAuth
    ) {
        this.firebaseAuth = firebaseAuth;
    }

    public String createUser(FirebaseCreateUserRequestBO reqBo) throws FirebaseAuthException {
        UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
            .setUid(reqBo.getUid())
            .setDisplayName(reqBo.getDisplayName())
            .setEmail(reqBo.getEmail())
            .setPassword(reqBo.getPassword())
            .setDisabled(reqBo.isDisable())
            .setEmailVerified(reqBo.isEmailVerified());
//                .setPhoneNumber(reqBo.getPhoneNumber());
//                .setPhotoUrl(reqBo.getPhotoUrl());
        UserRecord userRecord = firebaseAuth.createUser(createRequest);
        return userRecord.getUid();
    }
    
    public void setClaims(String uid, List<String> roles) throws FirebaseAuthException{
        Map<String, Object> claims = Map.of(
            FirebaseClaimKeysConstant.ROLE_KEY, roles
        );
        firebaseAuth.revokeRefreshTokens(uid);
        firebaseAuth.setCustomUserClaims(uid, claims);
    }
    
    public UserRecord getUser(String uid) throws FirebaseAuthException{
        return firebaseAuth.getUser(uid);
    }
    
    public UserRecord getUserByEmail(String email) throws FirebaseAuthException{
        return firebaseAuth.getUserByEmail(email);
    }
    
    public boolean isUserEmailVerified(String uid) throws FirebaseAuthException{
        UserRecord userRecord = getUser(uid);
        return userRecord.isEmailVerified();
    }
    
    public String getUid(String idToken) throws FirebaseAuthException{
        FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
        return decodedToken.getUid();
    }
    
    public FirebaseToken verifyIdToken(String idToken) throws FirebaseAuthException {
        return firebaseAuth.verifyIdToken(idToken);
    }
    
    public UserRecord updateUser(UserRecord.UpdateRequest updateRequest) throws FirebaseAuthException{
        return firebaseAuth.updateUser(updateRequest);
    }
}
