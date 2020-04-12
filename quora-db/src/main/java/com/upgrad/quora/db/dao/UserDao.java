package com.upgrad.quora.db.dao;

import com.upgrad.quora.db.entity.UserAuthEntity;
import com.upgrad.quora.db.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity getUser(String userUuid) {
        try {
            return entityManager.createNamedQuery("Users.getById", UserEntity.class)
                    .setParameter("uuid", userUuid)
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
}
