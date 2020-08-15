package com.example.springdemo.security;

import com.example.springdemo.model.user.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Security;
import java.util.Optional;

@Service
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserServiceImpl userService;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        try {
            String jwtToken = getJwtFromToken(httpServletRequest);

            if (StringUtils.hasText(jwtToken) && tokenProvider.validateToken(jwtToken)) {
                Integer userId = tokenProvider.getUserIdFromJWT(jwtToken);

                UserDetails userDetails = userService.findOne(userId);
                SecurityContext securityContext = SecurityContextHolder.getContext();
                Authentication auth = securityContext.getAuthentication();

                if(auth == null || auth instanceof AnonymousAuthenticationToken){
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(httpServletRequest)
                    );
                    securityContext.setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context {}", ex.getMessage());
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private String getJwtFromToken(HttpServletRequest request) {
        String bearToken = request.getHeader("Authorization");

        String jwt = null;
        if (StringUtils.hasText(bearToken) && bearToken.startsWith("Bearer")) {
            jwt = bearToken.substring(7);
        }
        return jwt;
    }

}
