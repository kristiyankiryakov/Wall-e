package com.kris.wall_e.service.impl;

import com.kris.wall_e.exception.UnauthorizedOperationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserIdentityService {

    /**
     * Extracts the username from the security context.
     *
     * @return The username of the authenticated user or throws an exception if no user is authenticated.
     * @throws UnauthorizedOperationException If no user is authenticated.
     */
    public String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedOperationException("No authenticated user found");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            return (String) principal;
        }
        throw new UnauthorizedOperationException("Unable to retrieve username from authentication");
    }

}
