package api.techhatch.com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
//        http
//        .authorizeHttpRequests(expressionInterceptUrlRegistry ->
//                expressionInterceptUrlRegistry
//                        .anyRequest()
//                        .permitAll())
//        .csrf(AbstractHttpConfigurer::disable);
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/","/api/v1/auth/**","/test","/error","/webjars/**","/login/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling( ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .oauth2Login(Customizer.withDefaults());

        return http.build();
    }

}
