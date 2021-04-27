package com.blog.blogapp.util;

import com.blog.blogapp.model.User;
import com.blog.blogapp.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Service
public class BlogReactiveUserDetailsService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    public BlogReactiveUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public Mono<UserDetails> findByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return Mono.empty();
        }

        return Mono.just(new
                org.springframework.security.core.userdetails.User
                (user.getUsername(), user.getPassword(), Arrays.asList(new
                        SimpleGrantedAuthority(user.getRole()))));
    }

}
