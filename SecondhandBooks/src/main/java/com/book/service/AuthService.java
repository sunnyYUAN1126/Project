package com.book.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private static final String P_USER_ID = "user_id";
    private static final String P_ROLE = "role";
    private static final String ROLE_ADMIN = "管理員"; // Matches database role name

    /**
     * Checks if the user is logged in.
     * 
     * @param session The current HTTP session.
     * @return true if logged in (user_id exists), false otherwise.
     */
    public boolean isLoggedIn(HttpSession session) {
        return session.getAttribute(P_USER_ID) != null;
    }

    /**
     * Gets the current user ID.
     * 
     * @param session The current HTTP session.
     * @return The user ID.
     * @throws ResponseStatusException if not logged in (401).
     */
    public Long getCurrentUserId(HttpSession session) {
        Object userId = session.getAttribute(P_USER_ID);
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "請先登入");
        }
        return (Long) userId;
    }

    /**
     * Checks if the current user is an admin.
     * 
     * @param session The current HTTP session.
     * @return true if admin, false otherwise.
     */
    public boolean isAdmin(HttpSession session) {
        Object role = session.getAttribute(P_ROLE);
        return ROLE_ADMIN.equals(role);
    }

    /**
     * Validates that the user is logged in.
     * 
     * @param session The current HTTP session.
     * @throws ResponseStatusException if not logged in (401).
     */
    public void validateLogin(HttpSession session) {
        if (!isLoggedIn(session)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "請先登入");
        }
    }

    /**
     * Validates that the user is an admin.
     * 
     * @param session The current HTTP session.
     * @throws ResponseStatusException if not admin (403) or not logged in (401).
     */
    public void validateAdmin(HttpSession session) {
        validateLogin(session);
        if (!isAdmin(session)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "權限不足：僅管理員可執行此操作");
        }
    }
}
