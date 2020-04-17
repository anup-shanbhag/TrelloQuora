package com.upgrad.quora.service.business;

import com.upgrad.quora.db.dao.AnswerDao;
import com.upgrad.quora.db.dao.QuestionDao;
import com.upgrad.quora.db.dao.UserDao;
import com.upgrad.quora.db.entity.AnswerEntity;
import com.upgrad.quora.db.entity.QuestionEntity;
import com.upgrad.quora.db.entity.UserEntity;
import com.upgrad.quora.service.constants.ErrorConditions;
import com.upgrad.quora.service.constants.UserRole;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
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
    QuestionService questionService;

    @Autowired
    UserService userService;

    /**
     * Method takes an answer as input and stores it in the database
     * @param answer answer to be stored
     * @return persisted answer
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(String token, String questionId, AnswerEntity answer) throws AuthorizationFailedException, InvalidQuestionException {
        try{
            UserEntity user = userService.getCurrentUser(token);
            QuestionEntity question = questionService.getQuestion(questionId);
            answer.setUser(user);
            answer.setQuestion(question);
            return answerDao.createAnswer(answer);
        } catch(AuthorizationFailedException e){
            if(e.getCode().equals(ErrorConditions.USER_SIGNED_OUT.getCode())){
                throw new AuthorizationFailedException(ErrorConditions.ANS_CREATE_AUTH_FAILURE.getCode(), ErrorConditions.ANS_CREATE_AUTH_FAILURE.getMessage());
            }
            else{
                throw e;
            }
        }
        catch(InvalidQuestionException e){
            throw new InvalidQuestionException(ErrorConditions.ANS_CREATE_FAILURE.getCode(),ErrorConditions.ANS_CREATE_FAILURE.getMessage());
        }
    }

    /**
     * Method takes answerId in input and fetches it from the database
     * @param answerId id of answer to be retrieved
     * @return desired Answer Entity
     * @throws AnswerNotFoundException if an answer with input answerId doesn't exist
     */
    public AnswerEntity getAnswer(String answerId) throws AnswerNotFoundException {
        AnswerEntity answer = answerDao.getAnswer(answerId);
        if(answer==null){
            throw new AnswerNotFoundException(ErrorConditions.ANS_NOT_FOUND.getCode(),ErrorConditions.ANS_NOT_FOUND.getMessage());
        }
        else{
            return answer;
        }
    }

    /**
     * Method takes current user and answer as input and updates the answer only if the current user is the answer owner
     * @param token Authorization token
     * @param answer answer to be updated
     * @return updated answer
     * @throws AuthorizationFailedException if a non-owner attempts to edit an answer
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswer(String token, AnswerEntity answer) throws AuthorizationFailedException {
        try{
            UserEntity user = userService.getCurrentUser(token);
            if(user.getId().equals(answer.getUser().getId())){
                return answerDao.updateAnswer(answer);
            }
            else{
                throw new AuthorizationFailedException(ErrorConditions.ANS_EDIT_UNAUTHORIZED.getCode(),ErrorConditions.ANS_EDIT_UNAUTHORIZED.getMessage());
            }
        }
        catch(AuthorizationFailedException e){
            if(e.getCode().equals(ErrorConditions.USER_SIGNED_OUT.getCode())){
                throw new AuthorizationFailedException(ErrorConditions.ANS_EDIT_AUTH_FAILURE.getCode(), ErrorConditions.ANS_EDIT_AUTH_FAILURE.getMessage());
            }
            else{
                throw e;
            }
        }
    }

    /**
     * Method takes current user and answer as input and deletes the answer only if the current user is an admin or the answer owner
     * @param token Authorization token
     * @param answerId Id of the answer to be deleted
     * @return deleted answer
     * @throws AuthorizationFailedException if a non-admin non-owner attempts to delete an answer
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity deleteAnswer(String token, String answerId) throws AuthorizationFailedException, AnswerNotFoundException {
        try{
            UserEntity user = userService.getCurrentUser(token);
            AnswerEntity answer = this.getAnswer(answerId);
            if(user.getId().equals(answer.getUser().getId()) || user.getRole().equalsIgnoreCase(UserRole.ADMIN.getRole())){
                return answerDao.deleteAnswer(answer);
            }
            else{
                throw new AuthorizationFailedException(ErrorConditions.ANS_DELETE_UNAUTHORIZED.getCode(),ErrorConditions.ANS_DELETE_UNAUTHORIZED.getMessage());
            }
        }
        catch(AuthorizationFailedException e){
            if(e.getCode().equals(ErrorConditions.USER_SIGNED_OUT.getCode())){
                throw new AuthorizationFailedException(ErrorConditions.ANS_DELETE_AUTH_FAILURE.getCode(), ErrorConditions.ANS_DELETE_AUTH_FAILURE.getMessage());
            }
            else{
                throw e;
            }
        }
    }

    /**
     * Method takes a question as parameter and fetches all answers posted on it from the database
     * @param token Authorization token
     * @param questionId Id of question for which all answers are to be fetched
     * @return List of all answers for question, empty list of no answers are available
     */
    public List<AnswerEntity> getAnswersForQuestion(String token, String questionId) throws InvalidQuestionException, AuthorizationFailedException {
        try{
            UserEntity user = userService.getCurrentUser(token);
            QuestionEntity question = questionService.getQuestion(questionId);
            return answerDao.getAnswersByQuestion(question);
        }
        catch(AuthorizationFailedException e){
            if(e.getCode().equals(ErrorConditions.USER_SIGNED_OUT.getCode())){
                throw new AuthorizationFailedException(ErrorConditions.ANS_GET_AUTH_FAILURE.getCode(), ErrorConditions.ANS_GET_AUTH_FAILURE.getMessage());
            }
            else{
                throw e;
            }
        }
        catch(InvalidQuestionException e){
            throw new InvalidQuestionException(ErrorConditions.ANS_GET_FAILURE.getCode(),ErrorConditions.ANS_GET_FAILURE.getMessage());
        }
    }
}
