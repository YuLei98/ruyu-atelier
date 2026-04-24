package icu.ruiyu.framework.integration.security.auth;

import icu.ruiyu.framework.integration.security.model.Constants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import cn.hutool.jwt.JWTUtil;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    private final static String AUTH_HEADER = "Authorization";
    private final static String AUTH_HEADER_TYPE = "Bearer";

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private Constants constants;

    /**
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(AUTH_HEADER);
        if (Objects.isNull(authHeader) || !authHeader.startsWith(AUTH_HEADER_TYPE)) {
            filterChain.doFilter(request, response);
            return ;
        }
        String authToken = authHeader.split(" ")[1];
        log.info("auth Token: {}", authToken);
        if (!JWTUtil.verify(authToken, constants.getJwtSignKey().getBytes(StandardCharsets.UTF_8))) {
            log.info("invalid token");
            filterChain.doFilter(request, response);
            return;
        }



        final String userName = (String) JWTUtil.parseToken(authToken).getPayload("username");
        log.info("userName: {}", userName);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails.getUsername(),
                        userDetails.getPassword(),
                        userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);


        log.info("here......");
        filterChain.doFilter(request, response);
    }
}
