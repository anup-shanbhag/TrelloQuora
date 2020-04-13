package com.upgrad.quora.service.business;

import com.upgrad.quora.db.dao.UserDao;
import com.upgrad.quora.db.entity.UserAuthEntity;
import com.upgrad.quora.db.entity.UserEntity;
import com.upgrad.quora.service.constants.UserRole;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    PasswordCryptographyProvider cryptographyProvider;

    /**
     *
     * @param userId
     * @param authorizationToken
     * @return
     * @throws AuthorizationFailedException
     * @throws UserNotFoundException
     */
    public UserEntity getUserById(final String userId, final String authorizationToken) throws AuthorizationFailedException, UserNotFoundException {
        UserEntity user = this.getCurrentUser(authorizationToken);
        user = userDao.getUser(userId);
        if(user==null){
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }
        else{
            return user;
        }
    }

    /**
     * Method takes authorization token as input and return the current logged in user.
     * @param authorizationToken User's authorization token
     * @return Returns current logged in user
     * @throws AuthorizationFailedException if the authorization token is invalid, expired or not found.
     */
    public UserEntity getCurrentUser(String authorizationToken) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userDao.getUserAuthToken(authorizationToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if ((userAuthEntity.getLogoutAt() != null && userAuthEntity.getLogoutAt().isBefore(LocalDateTime.now()))
                || userAuthEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
        } else {
            return userAuthEntity.getUser();
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteUser(UserEntity user, String userId) throws AuthorizationFailedException, UserNotFoundException {
        if(user.getRole().equalsIgnoreCase(UserRole.ADMIN.getRole())){
            UserEntity removeUser = userDao.getUser(userId);
            if(removeUser!=null){
                userDao.deleteUser(removeUser);
                return removeUser.getUuid();
            }
            else{
                throw new UserNotFoundException("USR-001","User with entered uuid to be deleted does not exist");
            }
        }
        else{
            throw new AuthorizationFailedException("ATHR-003","Unauthorized Access, Entered user is not an admin");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity createUser(UserEntity user) {
        List<String> encryptedTexts = Arrays.asList(cryptographyProvider.encrypt(user.getPassword()));
        user.setSalt(encryptedTexts.get(0));
        user.setPassword(encryptedTexts.get(1));
        return userDao.createUser(user);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity authenticateUser(String userName, String password) throws AuthenticationFailedException {
        UserEntity user = userDao.getUserByEmailOrUserName(userName);
        if(user!=null){
            if(user.getPassword().equals(cryptographyProvider.encrypt(password,user.getSalt()))){
                JwtTokenProvider jwtProvider = new JwtTokenProvider(user.getPassword());
                UserAuthEntity userAuth = new UserAuthEntity();
                userAuth.setUser(user);
                userAuth.setUuid(user.getUuid());
                userAuth.setLoginAt(LocalDateTime.now());
                userAuth.setExpiresAt(LocalDateTime.now().plusHours(8));
                String accessToken = jwtProvider.generateToken(userAuth.getUuid(), ZonedDateTime.of(userAuth.getLoginAt(), ZoneId.of("IST ")), ZonedDateTime.of(userAuth.getExpiresAt(), ZoneId.of("IST ")));
                userAuth.setAccessToken(accessToken);
                return userDao.createUserAuth(userAuth);
            }
            else{
                throw new AuthenticationFailedException("ATH-002","Password failed");
            }
        }
        else{
            throw new AuthenticationFailedException("ATH-001","This username does not exist");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity invalidateAuthorization(String authorizationToken) throws SignOutRestrictedException {
        UserAuthEntity userAuth = userDao.getUserAuthToken(authorizationToken);
        if(userAuth != null){
            userAuth.setExpiresAt(LocalDateTime.now());
            userAuth.setLogoutAt(LocalDateTime.now());
            return userDao.updateUserAuth(userAuth).getUser();
        }
        else{
            throw new SignOutRestrictedException("SGR-001","User is not Signed in");
        }
    }
}