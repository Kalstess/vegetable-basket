package com.example.cailanzi.util;

import com.example.cailanzi.entity.User;
import jakarta.servlet.http.HttpServletRequest;

public class UserContext {
    private static final String CURRENT_USER_ATTR = "currentUser";

    public static User getCurrentUser(HttpServletRequest request) {
        Object user = request.getAttribute(CURRENT_USER_ATTR);
        if (user instanceof User) {
            return (User) user;
        }
        return null;
    }

    public static void setCurrentUser(HttpServletRequest request, User user) {
        request.setAttribute(CURRENT_USER_ATTR, user);
    }
}

