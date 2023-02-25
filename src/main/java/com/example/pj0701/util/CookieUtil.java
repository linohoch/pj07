package com.example.pj0701.util;

import com.example.pj0701.vo.CookieVO;
import org.springframework.util.SerializationUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Base64;


public class CookieUtil {

    public static String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return "null";
    }
    public static void appendValue(HttpServletRequest request,
                                      HttpServletResponse response,
                                      String name, String no) {
        String array = getCookieValue(request, name);
        System.out.println("append cookie: "+array+"-"+no);
        addCookie(response,name, array+"-"+no,10000);
    }

    public static boolean isInListAs(HttpServletRequest request, String name, String no) {
        System.out.println("cookie 리스트값 확인 ->"+getCookieValue(request, name));
        String[] array=getCookieValue(request, name).split("-");
        for (String a : array) {
            if (a.equals(no)) return true;
        }
        return false;
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);

        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }

    public static String serialize(Object obj) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(obj));
    }

    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(
                SerializationUtils.deserialize(
                        Base64.getUrlDecoder().decode(cookie.getValue())
                )
        );
    }

}
