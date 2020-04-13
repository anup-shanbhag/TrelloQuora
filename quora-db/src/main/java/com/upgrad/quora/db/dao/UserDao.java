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

    public UserEntity getUser(String userId) {
        try {
            return entityManager.createNamedQuery("Users.getById", UserEntity.class)
                    .setParameter("uuid", userId)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserAuthEntity getUserAuthToken(String authorizationToken) {
        try {
            return entityManager.createNamedQuery("UserAuths.getByAccessToken", UserAuthEntity.class)
                    .setParameter("accessToken", authorizationToken)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public void deleteUser(UserEntity user) {
        entityManager.remove(user);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity createUser(UserEntity user) {
        entityManager.persist(user);
        return user;
    }

    public UserEntity getUserByEmailOrUserName(String emailOrUserName) {
        try{
            return entityManager.createNamedQuery("Users.userByEmailOrUserName", UserEntity.class).setParameter("emailOrUserName",emailOrUserName).getSingleResult();
        }
        catch(NoResultException e){
            return null;
        }
    }

    public UserAuthEntity createUserAuth(UserAuthEntity userAuth) {
        entityManager.persist(userAuth);
        return userAuth;
    }

    public UserAuthEntity updateUserAuth(UserAuthEntity userAuth) {
        entityManager.merge(userAuth);
        return userAuth;
    }
}
