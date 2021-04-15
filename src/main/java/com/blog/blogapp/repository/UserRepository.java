package com.blog.blogapp.repository;

import com.blog.blogapp.model.User;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserRepository extends ElasticsearchRepository<User, String> {

    User findByUsername(String username);
    
}
