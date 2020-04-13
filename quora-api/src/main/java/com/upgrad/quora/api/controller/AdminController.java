package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.db.entity.UserEntity;
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.constants.UserStatus;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    UserService userService;

    @RequestMapping(path = "/user/{userId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> deleteUser (@PathVariable("userId") String userId, @RequestHeader("authorization") String authorization) throws AuthorizationFailedException, UserNotFoundException {
        String token = (authorization.contains("Bearer ")) ? StringUtils.substringAfter(authorization,"Bearer ") : authorization;
        UserEntity user = userService.getCurrentUser(token);
        userId = userService.deleteUser(user, userId);
        UserDeleteResponse response = new UserDeleteResponse().id(userId).status(UserStatus.DELETED_OK.getStatus());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
