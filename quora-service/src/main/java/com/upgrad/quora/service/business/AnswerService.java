package com.upgrad.quora.service.business;

import com.upgrad.quora.db.dao.AnswerDao;
import com.upgrad.quora.db.dao.QuestionDao;
import com.upgrad.quora.db.dao.UserDao;
import com.upgrad.quora.db.entity.AnswerEntity;
import com.upgrad.quora.db.entity.QuestionEntity;
import com.upgrad.quora.db.entity.UserEntity;
import com.upgrad.quora.service.constants.UserRole;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnswerService {
    @Autowired
    AnswerDao answerDao;
    @Autowired
    QuestionDao questionDao;
    @Autowired
    UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(AnswerEntity answer) {
        return answerDao.createAnswer(answer);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity getAnswer(String answerId) throws AnswerNotFoundException {
        AnswerEntity answer = answerDao.getAnswer(answerId);
        if(answer==null){
            throw new AnswerNotFoundException("ANS-001","Entered answer uuid does not exist");
        }
        else{
            return answer;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public String editAnswer(UserEntity user, AnswerEntity answer) throws AuthorizationFailedException {
        if(user.getId().equals(answer.getUser().getId())){
            answerDao.updateAnswer(answer);
            return answer.getUuid();
        }
        else{
            throw new AuthorizationFailedException("ATHR-003","Only the answer owner can edit the answer");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteAnswer(UserEntity user, AnswerEntity answer) throws AuthorizationFailedException {
        if(user.getId().equals(answer.getUser().getId()) || user.getRole().equalsIgnoreCase(UserRole.ADMIN.getRole())){
            answerDao.deleteAnswer(answer);
            return answer.getUuid();
        }
        else{
            throw new AuthorizationFailedException("ATHR-003","Only the answer owner or admin can delete the answer");
        }
    }

    public List<AnswerEntity> getAnswersForQuestion(QuestionEntity question) {
        return answerDao.getAnswersByQuestion(question);
    }
}
