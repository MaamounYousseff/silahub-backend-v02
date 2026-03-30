package com.example.feed.infrastructure;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.Arrays;
import java.util.Optional;


@Service
@Slf4j
public class CookieService
{

    public static final String FEED_OFFSET_NAME = "feedOffset";
    public static final int FEED_COOKIE_MAX_AGE = 3600;
    public static final boolean FEED_COOKIE_HTTP_ONLY = true;
    public static final String FEED_COOKIE_PATH = "/";
    public static final int FEED_COOKIE_INITIAL_VALUE = 0;


    public Optional<Integer> getFeedOffset() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attrs.getRequest();
        if (request == null) {
            return Optional.empty();
        }

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> FEED_OFFSET_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .map(value -> {
                    try {
                        return Integer.valueOf(value);
                    } catch (NumberFormatException e) {
                        return FEED_COOKIE_INITIAL_VALUE;
                    }
                })
                .findFirst()
                .map(Optional::of)
                .orElseGet(() -> Optional.of(0));
    }




    public void updateOffset(int offset,  HttpServletResponse response) {

        Cookie cookie = new Cookie(FEED_OFFSET_NAME, ""+offset);
        cookie.setValue(String.valueOf(offset));  // update value
        cookie.setPath(FEED_COOKIE_PATH);                      // ensure path is correct
        cookie.setHttpOnly(FEED_COOKIE_HTTP_ONLY);                 // optional: security
        cookie.setMaxAge(FEED_COOKIE_MAX_AGE);
        cookie.setSecure(false);

        response.addCookie(cookie);
    }





}
