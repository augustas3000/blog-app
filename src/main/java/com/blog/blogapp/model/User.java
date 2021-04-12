package com.blog.blogapp.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "blog_user")
public class User {

    @Id
    private String id;

    private String username;

    private String password;

    private String role;

    private String description;

}
