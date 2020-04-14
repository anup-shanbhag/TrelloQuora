package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.db.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/userprofile")
public class CommonController {

    @Autowired
    private UserService userService;

    /**
     * This is user to fetch the details of a user registered in the application. It takes the authorization token and the userId as input and fetches the profile information for the user.
     * @param authorization Authorization token from request header
     * @param userId Id of the user to be retrieved
     * @return Response entity with Http Status code and profile information of the input user
     * @throws AuthorizationFailedException if the authorization token is invalid, expired or not found
     * @throws UserNotFoundException if userId is invalid (no such user exists)
     */
    @RequestMapping(method = RequestMethod.GET, path = "/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> getUserProfile(@RequestHeader("authorization") final String authorization,
                                                              @PathVariable("userId") final String userId)
            throws AuthorizationFailedException, UserNotFoundException {
        String token = (authorization.contains("Bearer ")) ? StringUtils.substringAfter(authorization,"Bearer ") : authorization;
        UserEntity user = userService.getUserById(userId, token);
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userName(user.getUserName())
                .emailAddress(user.getEmail())
                .country(user.getCountry())
                .aboutMe(user.getAboutMe())
                .dob(user.getDob())
                .contactNumber(user.getContactNumber());
        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);
    }
}
