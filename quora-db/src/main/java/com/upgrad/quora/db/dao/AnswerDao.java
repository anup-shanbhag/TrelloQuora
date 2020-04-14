package com.upgrad.quora.db.dao;

import com.upgrad.quora.db.entity.AnswerEntity;
import com.upgrad.quora.db.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {
    @PersistenceContext
    EntityManager entityManager;

    /**
     * This method takes an Answer Entity and stores it in the database
     * @param answer Answer that should be stored in the database
     * @return created Answer Entity
     */
    public AnswerEntity createAnswer(AnswerEntity answer) {
        entityManager.persist(answer);
        return answer;
    }

    /**
     * Methods takes an updated Answer Entity and stores it in the database
     * @param answer Answer to the updated
     * @return updated Answer Entity
     */
    public AnswerEntity updateAnswer(AnswerEntity answer){
        entityManager.merge(answer);
        return answer;
    }

    /**
     * Method takes Answer Entity and deletes it from the database
     * @param answer Answer to be deleted
     * @return deleted Answer Entity
     */
    public AnswerEntity deleteAnswer(AnswerEntity answer){
        entityManager.remove(answer);
        return answer;
    }

    /**
     * Method takes an answerId, created a named query and fetches the answer from the database
     * @param answerId Id of the answer to be fetched
     * @return desired Answer Entity, null if no such entity exists
     */
    public AnswerEntity getAnswer(String answerId) {
        try{
            return entityManager.createNamedQuery("Answers.getById",AnswerEntity.class).setParameter("uuid",answerId).getSingleResult();
        }
        catch(NoResultException e){
            return null;
        }
    }

    /**
     * Method takes a question as input, creates a named query and fetches all answers posted on that question
     * @param question Question on which all answers need to be read
     * @return List of all answers for the input question, empty list if no answers available
     */
    public List<AnswerEntity> getAnswersByQuestion(QuestionEntity question){
        return entityManager.createNamedQuery("Answers.getByQuestion",AnswerEntity.class).setParameter("question",question).getResultList();
    }
}
