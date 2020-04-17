package com.upgrad.quora.service.business;

import com.upgrad.quora.db.dao.QuestionDao;
import com.upgrad.quora.db.entity.QuestionEntity;
import com.upgrad.quora.db.entity.UserEntity;
import com.upgrad.quora.service.constants.ErrorConditions;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.constants.UserRole;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class QuestionService {

    @Autowired
    QuestionDao questionDao;

    @Autowired
    UserService userService;

    /**
     * Method takes a question entity and stores it in the database
     * @param token Authorization token
     * @param question Question to store in the database
     * @return Created question entity
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion (String token, QuestionEntity question) throws AuthorizationFailedException {
        try{
            UserEntity user = userService.getCurrentUser(token);
            question.setUser(user);
            return questionDao.createQuestion(question);
        }
        catch(AuthorizationFailedException e){
            if(e.getCode().equals(ErrorConditions.USER_SIGNED_OUT.getCode())){
                throw new AuthorizationFailedException(ErrorConditions.QUES_CREATE_AUTH_FAILURE.getCode(), ErrorConditions.QUES_CREATE_AUTH_FAILURE.getMessage());
            }
            else{
                throw e;
            }
        }
    }

    /**
     * Method takes a questionId as a parameter and fetches the entity from database
     * @param questionId
     * @return Question entity from the database table with id = questionId
     * @throws InvalidQuestionException
     */
    public QuestionEntity getQuestion (String questionId) throws InvalidQuestionException {
        QuestionEntity question = questionDao.getQuestion(questionId);
        if(question==null){
            throw new InvalidQuestionException(ErrorConditions.QUES_NOT_FOUND.getCode(), ErrorConditions.QUES_NOT_FOUND.getMessage());
        }
        else {
            return question;
        }
    }

    /**
     * Method takes question and user entities as parameters and updates the question in the database if the user is an admin or the question owner
     * @param token authorization token
     * @param question Question to to be removed
     * @return Id of the updated question
     * @throws AuthorizationFailedException if logged in user is not the question owner
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public String editQuestion (String token, QuestionEntity question) throws AuthorizationFailedException {
        try{
            UserEntity user = userService.getCurrentUser(token);
            if(user.getId().equals(question.getUser().getId())){
                questionDao.updateQuestion(question);
                return question.getUuid();
            }
            else{
                throw new AuthorizationFailedException(ErrorConditions.QUES_EDIT_UNAUTHORIZED.getCode(), ErrorConditions.QUES_EDIT_UNAUTHORIZED.getMessage());
            }
        } catch(AuthorizationFailedException e){
            if(e.getCode().equals(ErrorConditions.USER_SIGNED_OUT.getCode())){
                throw new AuthorizationFailedException(ErrorConditions.QUES_EDIT_AUTH_FAILURE.getCode(), ErrorConditions.QUES_EDIT_AUTH_FAILURE.getMessage());
            }
            else{
                throw e;
            }
        }
    }

    /**
     * Method takes question and user entities as parameters and removes the question from the database if the user is an admin or the question owner
     * @param token authorization token
     * @param question Question to to be removed
     * @return Id of the deleted question
     * @throws AuthorizationFailedException if logged in user is neither an admin nor the question owner
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteQuestion (String token, QuestionEntity question) throws AuthorizationFailedException {
        try{
            UserEntity user = userService.getCurrentUser(token);
            if(user.getId().equals(question.getUser().getId()) || user.getRole().equalsIgnoreCase(UserRole.ADMIN.getRole())){
                questionDao.deleteQuestion(question);
                return question.getUuid();
            }
            else{
                throw new AuthorizationFailedException(ErrorConditions.QUES_DELETE_UNAUTHORIZED.getCode(), ErrorConditions.QUES_DELETE_UNAUTHORIZED.getMessage());
            }
        } catch(AuthorizationFailedException e){
            if(e.getCode().equals(ErrorConditions.USER_SIGNED_OUT.getCode())){
                throw new AuthorizationFailedException(ErrorConditions.QUES_DELETE_AUTH_FAILURE.getCode(), ErrorConditions.QUES_DELETE_AUTH_FAILURE.getMessage());
            }
            else{
                throw e;
            }
        }
    }

    /**
     * Method returns a list of all questions available in the database irrespective of owner or posted user
     * @param token Authorization token
     * @return a list of all questions available in the database, empty list if no questions are available
     */
    public List<QuestionEntity> getAllQuestions(String token) throws AuthorizationFailedException {
        try{
            UserEntity user = userService.getCurrentUser(token);
            return questionDao.getAllQuestions();
        }
        catch(AuthorizationFailedException e){
            if(e.getCode().equals(ErrorConditions.USER_SIGNED_OUT.getCode())){
                throw new AuthorizationFailedException(ErrorConditions.QUES_GET_ALL_AUTH_FAILURE.getCode(), ErrorConditions.QUES_GET_ALL_AUTH_FAILURE.getMessage());
            }
            else{
                throw e;
            }
        }
    }

    /**
     * Method returns a list of all questions posted by a specific user
     * @param token Authorization token
     * @param userId Id of a user whose questions are to be fetched
     * @return a list of questions posted by the input user, empty list if no questions are available
     */
    public List<QuestionEntity> getUserQuestions(String token,String userId) throws AuthorizationFailedException, UserNotFoundException {
        try{
            UserEntity user = userService.getUserById(token, userId);
            return questionDao.getUserQuestions(user);
        }
        catch(AuthorizationFailedException e){
            if(e.getCode().equals(ErrorConditions.USER_SIGNED_OUT.getCode())){
                throw new AuthorizationFailedException(ErrorConditions.QUES_GET_AUTH_FAILURE.getCode(), ErrorConditions.QUES_GET_AUTH_FAILURE.getMessage());
            }
            else{
                throw e;
            }
        }
        catch(UserNotFoundException e){
            throw new UserNotFoundException(ErrorConditions.QUES_GET_FAILURE.getCode(), ErrorConditions.QUES_GET_FAILURE.getMessage());
        }
    }

}
