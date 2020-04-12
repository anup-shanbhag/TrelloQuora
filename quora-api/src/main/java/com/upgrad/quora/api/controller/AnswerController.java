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

    @RequestMapping(path = "/answer/delete/{answerId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable("answerId") String answerId, @RequestHeader("authorization") String authorization) throws AuthorizationFailedException, AnswerNotFoundException {
        String token = (authorization.contains("Bearer ")) ? StringUtils.substringAfter(authorization,"Bearer ") : authorization;
        UserEntity user = userService.getCurrentUser(token);
        AnswerEntity answer = answerService.getAnswer(answerId);
        answerId = answerService.deleteAnswer(user,answer);
        AnswerDeleteResponse response = new AnswerDeleteResponse().id(answerId).status(AnswerStatus.ANSWER_DELETED.getStatus());
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @RequestMapping(path = "/answer/all/{questionId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDetailsResponse> getAllAnswersToQuestion(@PathVariable("questionId") String questionId, @RequestHeader("authorization") String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        try{
            String token = (authorization.contains("Bearer ")) ? StringUtils.substringAfter(authorization,"Bearer ") : authorization;
            UserEntity user = userService.getCurrentUser(token);
            QuestionEntity question = questionService.getQuestion(questionId);
            List<AnswerEntity> answers = answerService.getAnswersForQuestion(question);
            answers.forEach(answer-> System.out.println(answer.toString()));
            AnswerDetailsResponse response = new AnswerDetailsResponse();
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        catch(InvalidQuestionException e){
            throw new InvalidQuestionException("QUES-001","The question with entered uuid whose details are to be seen does not exist");
        }
    }
}
