package com.upgrad.quora.db.dao;

import com.upgrad.quora.db.entity.UserAuthEntity;
import com.upgrad.quora.db.entity.UserEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Method takes userId as the parameter and fetches user formation form the database
     * @param userId id of the user whose information is to be retrieved
     * @return desired User Entity
     */
    public UserEntity getUser(String userId) {
        try {
            return entityManager.createNamedQuery("Users.getById", UserEntity.class)
                    .setParameter("uuid", userId)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Method takes authorization token as a parameter and fetches user authentication information
     * @param authorizationToken authorization token
     * @return desired User Auth Entity
     */
    public UserAuthEntity getUserAuthToken(String authorizationToken) {
        try {
            return entityManager.createNamedQuery("UserAuths.getByAccessToken", UserAuthEntity.class)
                    .setParameter("accessToken", authorizationToken)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Method takes User Entity as a parameter and deletes it from the database
     * @param user User Entity to be deleted
     * @return delete User Entity
     */
    public UserEntity deleteUser(UserEntity user) {
        entityManager.remove(user);
        return user;
    }

    /**
     * This method takes an User Entity and stores it in the database
     * @param user User Entity that should be stored in the database
     * @return created User Entity
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity createUser(UserEntity user) {
        entityManager.persist(user);
        return user;
    }

    /**
     * This method takes userName or Email as the input, creates a named query and fetches user information from the database
     * @param emailOrUserName Email or Username
     * @return User Entity with matching email or username
     */
    public UserEntity getUserByEmailOrUserName(String emailOrUserName) {
        try{
            return entityManager.createNamedQuery("Users.userByEmailOrUserName", UserEntity.class).setParameter("emailOrUserName",emailOrUserName).getSingleResult();
        }
        catch(NoResultException e){
            return null;
        }
    }

    /**
     * Method take User Auth Entity and a parameter and stores it in the database
     * @param userAuth User Authentication information to be stored
     * @return created User Authentication information
     */
    public UserAuthEntity createUserAuth(UserAuthEntity userAuth) {
        entityManager.persist(userAuth);
        return userAuth;
    }

    /**
     * This method takes updated User Authentication Entity as a parameter and stored it in the database
     * @param userAuth User Authentication information to be updated
     * @return updated User Authentication information
     */
    public UserAuthEntity updateUserAuth(UserAuthEntity userAuth) {
        entityManager.merge(userAuth);
        return userAuth;
    }
}
