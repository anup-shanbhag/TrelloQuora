package com.upgrad.quora.service.business;

import com.upgrad.quora.db.dao.UserDao;
import com.upgrad.quora.db.entity.UserAuthEntity;
import com.upgrad.quora.db.entity.UserEntity;
import com.upgrad.quora.service.constants.ErrorConditions;
import com.upgrad.quora.service.constants.UserRole;
import com.upgrad.quora.service.exception.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
     * Method takes userId and authorizationToken as paremeter and fetches user information of user with uuid = userId
     * @param userId User Id
     * @param token Authorization Token
     * @return desired User Information
     * @throws AuthorizationFailedException if the authorization token is invalid, expired or not found
     * @throws UserNotFoundException if no such user exists
     */
    public UserEntity getUserById(String token, String userId) throws AuthorizationFailedException, UserNotFoundException {
       try{
           UserEntity user = getCurrentUser(token);
           user = userDao.getUser(userId);
           if(user==null){
               throw new UserNotFoundException(ErrorConditions.USER_NOT_FOUND.getCode(), ErrorConditions.USER_NOT_FOUND.getMessage());
           }
           else{
               return user;
           }
       }
       catch(AuthorizationFailedException e){
           if(e.getCode().equals(ErrorConditions.USER_SIGNED_OUT.getCode())){
               throw new AuthorizationFailedException(ErrorConditions.USER_GET_AUTH_FAILURE.getCode(), ErrorConditions.USER_GET_AUTH_FAILURE.getMessage());
           }
           else{
               throw e;
           }
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
            throw new AuthorizationFailedException(ErrorConditions.USER_NOT_SIGNED_IN.getCode(), ErrorConditions.USER_NOT_SIGNED_IN.getMessage());
        } else if ((userAuthEntity.getLogoutAt() != null && userAuthEntity.getLogoutAt().isBefore(LocalDateTime.now()))
                || userAuthEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AuthorizationFailedException(ErrorConditions.USER_GET_AUTH_FAILURE.getCode(), ErrorConditions.USER_GET_AUTH_FAILURE.getMessage());
        } else {
            return userAuthEntity.getUser();
        }
    }

    /**
     * Method takes current user and userId of the user to delete, and removes the user from the application if the current user is an admin.
     * @param token Authorization token of the current logged in user
     * @param userId Id of the user to be deleted
     * @return deleted userId
     * @throws AuthorizationFailedException if current user is not an admin
     * @throws UserNotFoundException if no such user exists
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity deleteUser(String token, String userId) throws AuthorizationFailedException, UserNotFoundException {
        UserEntity currentUser = this.getCurrentUser(token);
        if(currentUser.getRole().equalsIgnoreCase(UserRole.ADMIN.getRole())){
            UserEntity removeUser = userDao.getUser(userId);
            if(removeUser!=null){
                return userDao.deleteUser(removeUser);
            }
            else{
                throw new UserNotFoundException(ErrorConditions.USER_DELETE_FAILURE.getCode(),ErrorConditions.USER_DELETE_FAILURE.getMessage());
            }
        }
        else{
            throw new AuthorizationFailedException(ErrorConditions.USER_DELETE_UNAUTHORIZED.getCode(),ErrorConditions.USER_DELETE_UNAUTHORIZED.getMessage());
        }
    }

    /**
     * Methods takes a new user as a parameter and add it to the application database
     * @param user New User
     * @return creates User
     * @throws SignUpRestrictedException if new user's username/email is already taken
     */
    public UserEntity createUser(UserEntity user) throws SignUpRestrictedException {
        List<String> encryptedTexts = Arrays.asList(cryptographyProvider.encrypt(user.getPassword()));
        user.setSalt(encryptedTexts.get(0));
        user.setPassword(encryptedTexts.get(1));
        try {
            user = userDao.createUser(user);
            return user;
        }
        catch(DataIntegrityViolationException e){
            if(e.getCause() instanceof ConstraintViolationException){
                String constraintName = ((ConstraintViolationException)e.getCause()).getConstraintName();
                if(StringUtils.containsIgnoreCase(constraintName,"userName")){
                    throw new SignUpRestrictedException (ErrorConditions.USERNAME_ALREADY_EXISTS.getCode(),ErrorConditions.USERNAME_ALREADY_EXISTS.getMessage());
                }
                else{
                    throw new SignUpRestrictedException (ErrorConditions.EMAIL_ALREADY_EXISTS.getCode(),ErrorConditions.EMAIL_ALREADY_EXISTS.getMessage());
                }
            }
            else{
                throw e;
            }
        }
    }

    /**
     * Method takes user's login info and logs the user into the application.
     * @param userName User's username
     * @param password User's password
     * @return return User Authentication Information
     * @throws AuthenticationFailedException if user's login information (username/password) are invalid
     */
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
                throw new AuthenticationFailedException(ErrorConditions.USER_WRONG_PASSWORD.getCode(),ErrorConditions.USER_WRONG_PASSWORD.getMessage());
            }
        }
        else{
            throw new AuthenticationFailedException(ErrorConditions.USERNAME_NOT_FOUND.getCode(),ErrorConditions.USERNAME_NOT_FOUND.getMessage());
        }
    }

    /**
     * Method takes the authorization token and logs a user out of the application
     * @param authorizationToken authorization token
     * @return logged out User
     * @throws SignOutRestrictedException if the user is not signed in
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity invalidateAuthorization(String authorizationToken) throws SignOutRestrictedException {
        UserAuthEntity userAuth = userDao.getUserAuthToken(authorizationToken);
        if(userAuth != null){
            userAuth.setExpiresAt(LocalDateTime.now());
            userAuth.setLogoutAt(LocalDateTime.now());
            return userDao.updateUserAuth(userAuth).getUser();
        }
        else{
            throw new SignOutRestrictedException(ErrorConditions.USER_SIGNOUT_ERROR.getCode(),ErrorConditions.USER_SIGNOUT_ERROR.getMessage());
        }
    }
}