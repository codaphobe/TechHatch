package api.techhatch.com.config;

import api.techhatch.com.service.CustomUserDetailsService;
import api.techhatch.com.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ApplicationContext context;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        if( authHeader!=null && authHeader.startsWith("Bearer")){
            token = authHeader.substring(7);
            email = jwtUtil.getEmailFromToken(token);
        }

        if(email!=null && SecurityContextHolder.getContext().getAuthentication()==null){
            UserDetails userDetails = context.getBean(CustomUserDetailsService.class).loadUserByUsername(email);
            if (jwtUtil.validateToken(token, userDetails)){
                String role = jwtUtil.getRoleFromToken(token);
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, Collections.singleton(authority));
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request,response);
    }
}
