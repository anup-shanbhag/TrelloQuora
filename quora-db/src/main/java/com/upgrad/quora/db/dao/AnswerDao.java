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

    public AnswerEntity createAnswer(AnswerEntity answer) {
        entityManager.persist(answer);
        return answer;
    }

    public AnswerEntity updateAnswer(AnswerEntity answer){
        entityManager.merge(answer);
        return answer;
    }

    public AnswerEntity deleteAnswer(AnswerEntity answer){
        entityManager.remove(answer);
        return answer;
    }

    public AnswerEntity getAnswer(String answerId) {
        try{
            return entityManager.createNamedQuery("Answers.getById",AnswerEntity.class).setParameter("uuid",answerId).getSingleResult();
        }
        catch(NoResultException e){
            return null;
        }
    }

    public List<AnswerEntity> getAnswersByQuestion(QuestionEntity question){
        return entityManager.createNamedQuery("Answers.getByQuestion",AnswerEntity.class).setParameter("question",question).getResultList();
    }
}
