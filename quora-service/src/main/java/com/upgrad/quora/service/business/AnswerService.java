package com.upgrad.quora.service.business;

import com.upgrad.quora.db.dao.AnswerDao;
import com.upgrad.quora.db.dao.QuestionDao;
import com.upgrad.quora.db.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnswerService {
    @Autowired
    AnswerDao answerDao;
    @Autowired
    QuestionDao questionDao;
    @Autowired
    UserDao userDao;
}
