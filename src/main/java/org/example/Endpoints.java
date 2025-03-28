package org.example;

public class Endpoints {
    public String baseUrl = "https://api.news.academy.dunice.net";

    public String signup = "/auth/signup";
    public String login = "/auth/login";
    public String getCurrentUserInfoByToken = "/auth/whoami";

    public String updateUserInfoById =  "/users/{id}";
    public String getUserInfoById =  "/users/{id}";

    public String createPost =  "/posts";
    public String getPosts =  "/posts";
    public String getPostById = "/posts/{id}";
    public String updatePostById =  "/posts/{id}";
    public String deletePostById = "/posts/{id}";

    public String createComment = "/comments";
    public String updateCommentById = "/comments/{id}";
    public String deleteCommentById = "/comments/{id}";
}
