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
            return Optional.of(FEED_COOKIE_INITIAL_VALUE);
        }

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.of(FEED_COOKIE_INITIAL_VALUE);

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




    public void updateOffset(int offset) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attrs.getRequest();
        HttpServletResponse response = attrs.getResponse();

        if (request == null || response == null) {
            log.error("Request or Response not available");
            return;
        }

        Cookie[] cookies = request.getCookies();
        boolean found = false;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (FEED_OFFSET_NAME.equals(cookie.getName())) {
                    cookie.setValue(String.valueOf(offset));  // update value
                    cookie.setPath(FEED_COOKIE_PATH);                      // ensure path is correct
                    cookie.setHttpOnly(FEED_COOKIE_HTTP_ONLY);                 // optional: security
                    cookie.setMaxAge(FEED_COOKIE_MAX_AGE);                   // optional: lifetime
                    response.addCookie(cookie);               // send back to client
                    found = true;
                    break;
                }
            }
        }

        if (!found) {
            log.info("cookie does not exist");
            // Cookie doesn't exist, create a new one
            Cookie newCookie = new Cookie(FEED_OFFSET_NAME, String.valueOf(offset));
            newCookie.setPath(FEED_COOKIE_PATH);
            newCookie.setHttpOnly(true);
            newCookie.setMaxAge(FEED_COOKIE_MAX_AGE);
            response.addCookie(newCookie);
        }
    }



    public int  createCookie()
    {
        Cookie cookie = new Cookie(FEED_OFFSET_NAME, ""+FEED_COOKIE_INITIAL_VALUE);
        return FEED_COOKIE_INITIAL_VALUE;
    }

}
