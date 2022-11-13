package com.fastcampus.sns.config.filter;

import com.fastcampus.sns.model.User;
import com.fastcampus.sns.service.UserService;
import com.fastcampus.sns.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private final UserService userService;

    @Value("${jwt.secret-key}")
    private String key;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try{
            String header = request.getHeader(HttpHeaders.AUTHORIZATION);
            String requestURI = request.getRequestURI();
            log.info("requestURI: {}", requestURI);
            if(header == null || !header.startsWith("Bearer ")){
                log.error("Error occurs while getting header. header is null or invalid");
                return;
            }

            final String token = header.split(" ")[1].trim();

            //TODK : check token is valid
            if(JwtTokenUtils.isExpired(token, key)){
                log.error("Key is expired");
                return;
            }


            //TODO : get username from token
            String userName = JwtTokenUtils.getUserName(token,key);

            //TODK check the username is valid
            User user = userService.loadUserByUserName(userName);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    //TODO
                    user, null, user.getAuthorities()
            );

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }catch(RuntimeException e){
            log.error("Error occurs while validating. {}", e.toString());
            return;
        }finally {
            filterChain.doFilter(request,response);
        }

    }
}
