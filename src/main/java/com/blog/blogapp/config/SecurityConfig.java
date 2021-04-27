package com.blog.blogapp.config;

import com.blog.blogapp.model.User;
import com.blog.blogapp.service.UserService;
import com.blog.blogapp.util.BlogReactiveUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.config.EnableWebFlux;

import java.util.UUID;

@Configuration
@EnableWebFlux
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Autowired
    private UserService userService;

    @Bean
    public SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) throws Exception {
        return http
                .authorizeExchange()
                .pathMatchers(HttpMethod.GET,
                        "/article", "/article/show/**", "/webjars/**", "/css/**", "/favicon.ico", "/").permitAll()
                .pathMatchers(HttpMethod.POST, "/article").authenticated()
                .pathMatchers("/article/edit/**", "/article/new", "/article/delete/**").authenticated()
                .and()
                .csrf().disable()
                .formLogin()
                .and()
                .logout()
                .and()
                .build();
    }

    @Bean
    public UserDetailsRepositoryReactiveAuthenticationManager authenticationManager(
            BlogReactiveUserDetailsService blogReactiveUserDetailsService) {

        UserDetailsRepositoryReactiveAuthenticationManager userDetailsRepositoryReactiveAuthenticationManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(blogReactiveUserDetailsService);

        userDetailsRepositoryReactiveAuthenticationManager.setPasswordEncoder(passwordEncoder());
        return userDetailsRepositoryReactiveAuthenticationManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            userService.deleteAll();
            userService.save(new User(UUID.randomUUID().toString(), "user", passwordEncoder().encode("password"), "USER", "User of Blog"));
            userService.save(new User(UUID.randomUUID().toString(), "admin", passwordEncoder().encode("password"), "ADMIN", "Admin of Blog"));
        };
    }

}
