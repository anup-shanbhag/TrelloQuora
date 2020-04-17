package com.upgrad.quora.service.utils;

import org.apache.commons.lang3.StringUtils;

public class AppUtils {
    public static String getBearerAuthToken(String headerParam){
        return (headerParam.contains("Bearer ")) ? StringUtils.substringAfter(headerParam,"Bearer ") : headerParam;
    }

    public static String getBasicAuthToken(String headerParam){
        return (headerParam.contains("Basic ")) ? StringUtils.substringAfter(headerParam,"Basic ") : headerParam;
    }
}
