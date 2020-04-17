package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.db.entity.UserEntity;
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.constants.UserStatus;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import com.upgrad.quora.service.utils.AppUtils;
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

    /**
     * This is used to delete a specific user in the application. It takes authorization token and the user id  of the user and removes the user from the application.
     * @param userId Id of the user to be deleted
     * @param authorization Authorization token from request header
     * @return Response Entity with Http Status Code, Id of the deleted user and message
     * @throws AuthorizationFailedException if the authorization token is invalid, expired or not found
     * @throws AuthorizationFailedException if the user is not an admin
     * @throws UserNotFoundException if userId is invalid (no such user exists)
     */
    @RequestMapping(path = "/user/{userId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> deleteUser (@PathVariable("userId") String userId, @RequestHeader("authorization") String authorization) throws AuthorizationFailedException, UserNotFoundException {
        String token = AppUtils.getBearerAuthToken(authorization);
        UserEntity user = userService.deleteUser(token, userId);
        UserDeleteResponse response = new UserDeleteResponse().id(user.getUuid()).status(UserStatus.DELETED_OK.getStatus());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
