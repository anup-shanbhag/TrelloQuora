package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.db.entity.UserAuthEntity;
import com.upgrad.quora.db.entity.UserEntity;
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.constants.UserRole;
import com.upgrad.quora.service.constants.UserStatus;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @RequestMapping(path = "/signup", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> registerUser(SignupUserRequest request) throws SignUpRestrictedException {
        UserEntity user = new UserEntity();
        user.setUuid(UUID.randomUUID().toString());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUserName(request.getUserName());
        user.setEmail(request.getEmailAddress());
        user.setPassword(request.getPassword());
        user.setSalt(StringUtils.remove(UUID.randomUUID().toString(),'-'));
        user.setRole(UserRole.REGULAR.getRole());
        user.setDob(request.getDob());
        user.setAboutMe(request.getAboutMe());
        user.setCountry(request.getCountry());
        user.setContactNumber(request.getContactNumber());
        user = userService.createUser(user);
        SignupUserResponse response = new SignupUserResponse().id(user.getUuid()).status(UserStatus.REGISTERED_OK.getStatus());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @RequestMapping(path = "/signin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> loginUser(@RequestHeader("authorization") String authorization) throws AuthenticationFailedException {
        String token = (authorization.contains("Basic ")) ? StringUtils.substringAfter(authorization, "Basic ") : authorization;
        StringTokenizer tokens =  new StringTokenizer(new String (Base64.getDecoder().decode(token)));
        UserAuthEntity userAuth = userService.authenticateUser(tokens.nextToken(),tokens.nextToken());
        SigninResponse response = new SigninResponse().id("").message(UserStatus.SIGN_IN_OK.getStatus());
        MultiValueMap<String,String> headers = new HttpHeaders();
        headers.add("access_token",userAuth.getAccessToken());
        return new ResponseEntity<SigninResponse>(response, headers, HttpStatus.OK);
    }

    @RequestMapping(path = "/signout", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> logoutUser(@RequestHeader("authorization") String authorization) throws SignOutRestrictedException {
        String token = (authorization.contains("Basic ")) ? StringUtils.substringAfter(authorization, "Basic ") : authorization;
        UserEntity user = userService.invalidateAuthorization(token);
        SignoutResponse response = new SignoutResponse().id(user.getUuid()).message(UserStatus.SIGN_OUT_OK.getStatus());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
