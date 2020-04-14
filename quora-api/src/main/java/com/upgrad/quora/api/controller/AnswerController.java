package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.db.entity.AnswerEntity;
import com.upgrad.quora.db.entity.QuestionEntity;
import com.upgrad.quora.db.entity.UserEntity;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.constants.AnswerStatus;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    UserService userService;
    @Autowired
    QuestionService questionService;
    @Autowired
    AnswerService answerService;

    /**
     * This is used to create an answer for a question in the application. It takes the questionId, answer details and authorization token and creates an answer for the input question.
     * @param questionId Question Id
     * @param authorization Authorization token from request header
     * @param request Answer details
     * @return Response entity Http Status code, id of the created answer and message
     * @throws AuthorizationFailedException if the authorization token is invalid, expired or not found
     * @throws InvalidQuestionException if questionId is invalid (no such question exists)
     */
    @RequestMapping(path = "/question/{questionId}/answer/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(@PathVariable("questionId") String questionId, @RequestHeader("authorization") String authorization, AnswerRequest request) throws AuthorizationFailedException, InvalidQuestionException {
        try{
            String token = (authorization.contains("Bearer ")) ? StringUtils.substringAfter(authorization,"Bearer ") : authorization;
            UserEntity user = userService.getCurrentUser(token);
            QuestionEntity question = questionService.getQuestion(questionId);
            AnswerEntity answer = new AnswerEntity();
            answer.setAnswer(request.getAnswer());
            answer.setDate(LocalDate.now());
            answer.setUuid(UUID.randomUUID().toString());
            answer.setUser(user);
            answer.setQuestion(question);
            answer = answerService.createAnswer(answer);
            AnswerResponse response = new AnswerResponse().id(answer.getUuid()).status(AnswerStatus.ANSWER_CREATED.getStatus());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        catch(InvalidQuestionException e){
            throw new InvalidQuestionException("QUES-001","The question entered is invalid");
        }
    }

    /**
     * This is used to edit an answer that has been posted by a user. Note, only the answer owner can edit an answer. It takes answerId, answer content and authorization token to find and update the answer in the database.
     * @param answerId id of the answer to be edited
     * @param authorization Authorization token from request header
     * @param request updated answer details
     * @return Response entity with Http Status code, id of answer updated and message
     * @throws AuthorizationFailedException if the authorization token is invalid, expired or not found
     * @throws AnswerNotFoundException if an answer with input questionId doesn't exist
     */
    @RequestMapping(path = "/answer/edit/{answerId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswer(@PathVariable("answerId") String answerId, @RequestHeader("authorization") String authorization, AnswerEditRequest request) throws AuthorizationFailedException, AnswerNotFoundException {
        String token = (authorization.contains("Bearer ")) ? StringUtils.substringAfter(authorization,"Bearer ") : authorization;
        UserEntity user = userService.getCurrentUser(token);
        AnswerEntity answer = answerService.getAnswer(answerId);
        answer.setAnswer(request.getContent());
        answerId = answerService.editAnswer(user,answer);
        AnswerEditResponse response = new AnswerEditResponse().id(answerId).status(AnswerStatus.ANSWER_EDITED.getStatus());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * This is used to delete an answer that has been posted by a user. Note, only the answer owner or an admin can delete an answer. It takes answerId, answer content and authorization token to find and delete the answer in the database.
     * @param answerId id of the answer to be removed
     * @param authorization Authorization token from request header
     * @return Response entity with Http Status code, id of answer updated and message
     * @throws AuthorizationFailedException if the authorization token is invalid, expired or not found
     * @throws AuthorizationFailedException if a non-admin non-owner attempts to delete an answer
     * @throws AnswerNotFoundException if an answer with input questionId doesn't exist
     */
    @RequestMapping(path = "/answer/delete/{answerId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable("answerId") String answerId, @RequestHeader("authorization") String authorization) throws AuthorizationFailedException, AnswerNotFoundException {
        String token = (authorization.contains("Bearer ")) ? StringUtils.substringAfter(authorization,"Bearer ") : authorization;
        UserEntity user = userService.getCurrentUser(token);
        AnswerEntity answer = answerService.getAnswer(answerId);
        answerId = answerService.deleteAnswer(user,answer);
        AnswerDeleteResponse response = new AnswerDeleteResponse().id(answerId).status(AnswerStatus.ANSWER_DELETED.getStatus());
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    /**
     * This is used to fetch all answers posted for a specific question in the application. It takes authorization token and the question id and fetches list answers posted for the input question.
     * @param questionId Id of the question whose answers need to be listed
     * @param authorization Authorization token from request header
     * @return Response Entity with questionId, message and Http Status Code
     * @throws AuthorizationFailedException if the authorization token is invalid, expired or not found
     * @throws InvalidQuestionException if a question with input questionId doesn't exist
     */
    @RequestMapping(path = "/answer/all/{questionId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(@PathVariable("questionId") String questionId, @RequestHeader("authorization") String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        String token = (authorization.contains("Bearer ")) ? StringUtils.substringAfter(authorization,"Bearer ") : authorization;
        UserEntity user = userService.getCurrentUser(token);
        List<AnswerDetailsResponse> response = new ArrayList<>();
        answerService.getAnswersForQuestion(questionId).forEach (answer -> response.add(new AnswerDetailsResponse().id(answer.getUuid()).answerContent(answer.getAnswer()).questionContent(answer.getQuestion().getContent())));
        if(response.isEmpty()){
            return new ResponseEntity<>(response,HttpStatus.NO_CONTENT);
        }
        else{
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }
}
