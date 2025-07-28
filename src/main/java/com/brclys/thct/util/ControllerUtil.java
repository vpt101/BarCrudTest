package com.brclys.thct.util;

import com.brclys.thct.AppConstants;
import org.springframework.http.HttpHeaders;

import java.util.Objects;

public class ControllerUtil {

    public static String getJwtFromAuthHeader(HttpHeaders headers) {
        return Objects.requireNonNull(headers.get(AppConstants.AUTHORIZATION_HEADER).getFirst()).toString().substring(7);
    }
}
