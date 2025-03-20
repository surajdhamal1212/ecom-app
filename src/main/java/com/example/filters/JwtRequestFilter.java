package com.example.filters;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.services.jwt.UserDetailsServiceImpl;
import com.example.utils.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;

    // ✅ Constructor Injection
    public JwtRequestFilter(UserDetailsServiceImpl userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // इन पाथ्स पर फ़िल्टर स्किप करें
        return new AntPathMatcher().match("/auth/sign-up", request.getServletPath()) ||
               new AntPathMatcher().match("/auth/authenticate", request.getServletPath()) ||
               request.getMethod().equals("OPTIONS");
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        System.out.println("📢 [JwtRequestFilter] Request received for: " + request.getRequestURI());
        System.out.println("📢 Authorization Header: " + authHeader);

        try {
            // ✅ Extract JWT Token from the Authorization Header
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                System.out.println("✅ Extracted Token: " + token);

                // ✅ Extract Username from JWT Token
                username = jwtUtil.extractUsername(token);
                System.out.println("✅ Extracted Username: " + username);
            } else {
                System.out.println("❌ No JWT Token found in request");
            }

            // ✅ Authenticate the User if Token is Valid
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtUtil.validateToken(token, userDetails)) {
                    System.out.println("✅ Token Validated Successfully");

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    System.out.println("✅ User Authenticated and SecurityContext Updated");
                } else {
                    System.out.println("❌ Token validation failed");
                }
            } else {
                System.out.println("⚠️ User is already authenticated or username is null");
            }
        } catch (ExpiredJwtException e) {
            System.out.println("❌ Token Expired: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has expired");
            return;
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException e) {
            System.out.println("❌ Invalid Token: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        } catch (Exception e) {
            System.out.println("❌ Unexpected Error: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred");
            return;
        }

        System.out.println("➡️ Forwarding request to next filter...");
        filterChain.doFilter(request, response);
    }


//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        final String authHeader = request.getHeader("Authorization");
//        String token = null;
//        String username = null;
//
//        try {
//            // ✅ Extract JWT Token from the Authorization Header
//            if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                token = authHeader.substring(7);
//
//                // ✅ Extract Username from JWT Token
//                username = jwtUtil.extractUsername(token);
//            }
//
//            // ✅ Authenticate the User if Token is Valid
//            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//                if (jwtUtil.validateToken(token, userDetails)) {
//                    UsernamePasswordAuthenticationToken authToken =
//                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                    SecurityContextHolder.getContext().setAuthentication(authToken);
//                }
//            }
//        } catch (ExpiredJwtException e) {
//            // Handle expired token
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has expired");
//            return;
//        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException e) {
//            // Handle invalid token
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
//            return;
//        } catch (Exception e) {
//            // Handle other exceptions
//            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred");
//            return;
//        }
//
//        // ✅ Continue the Filter Chain
//        filterChain.doFilter(request, response);
//    }
}