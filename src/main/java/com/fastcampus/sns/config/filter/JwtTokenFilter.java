package com.fastcampus.sns.config.filter;

import com.fastcampus.sns.model.User;
import com.fastcampus.sns.service.UserService;
import com.fastcampus.sns.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final UserService userService;

    private final String key;

    private final static List<String> TOKEN_IN_PARAM_URLS = List.of("/api/v1/users/alarm/subscribe");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            final String token;

            if (TOKEN_IN_PARAM_URLS.contains(request.getRequestURI())) {
                log.info("Request with {} check the query param", request.getRequestURI());
                token = request.getQueryString().split("=")[1].trim();
            } else {
                String header = request.getHeader(HttpHeaders.AUTHORIZATION);

                if (header == null || !header.startsWith("Bearer ")) {
                    log.error("Error occurs while getting header. header is null or invalid");
                    return;
                }
                token = header.split(" ")[1].trim();
            }

            //TODK : check token is valid
            if (JwtTokenUtils.isExpired(token, key)) {
                log.error("Key is expired");
                return;
            }


            //TODO : get username from token
            String userName = JwtTokenUtils.getUserName(token, key);

            //TODK check the username is valid
            User user = userService.loadUserByUserName(userName);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    //TODO
                    user, null, user.getAuthorities()
            );

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return;
        } finally {
            filterChain.doFilter(request, response);
        }

    }
}
