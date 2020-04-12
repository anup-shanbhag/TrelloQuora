package com.upgrad.quora.service.business;

import com.upgrad.quora.db.dao.UserDao;
import com.upgrad.quora.db.entity.UserAuthEntity;
import com.upgrad.quora.db.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

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
}