package org.example;

import java.io.File;
import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;

public class Methods {
    private static final Endpoints endpoints = new Endpoints();
    private final String title = Constants.postName;
    private final String description = Constants.postDescription;
    private final String image = Constants.postImage;
    private final String[] tags = Constants.postTags;
    private final String comment = Constants.postComment;

    public String getUniqueString() {
        return UUID.randomUUID().toString();
    }

    public Response getDefaultUserInfo() {
        JSONObject requestBody = new JSONObject();
        requestBody.put("email", Constants.correctEmailUser);
        requestBody.put("password", Constants.correctPasswordUser);

        return RestAssured
                .given()
                    .baseUri(endpoints.baseUrl)
                    .basePath(endpoints.login)
                    .header("Content-Type", "application/json")
                    .body(requestBody.toString())
                .when()
                .post();
    }

    public void returnUserData(String userId, String token) {
        JSONObject returnUserData = new JSONObject();
        returnUserData.put("firstName", Constants.correctUserFirstname);
        returnUserData.put("lastName", Constants.correctUserLastname);
        RestAssured
                .given()
                .baseUri(endpoints.baseUrl)
                .basePath(endpoints.updateUserInfoById)
                .pathParam("id", userId)
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .body(returnUserData.toString())
                .when()
                .patch();
    }

    public Response createPostBeforeUsing() {
        String token = getDefaultUserInfo().jsonPath().getString("accessToken");
        return RestAssured
                .given()
                .baseUri(endpoints.baseUrl)
                .basePath(endpoints.createPost)
                .header("Authorization", "Bearer " + token)
                .multiPart("title", title)
                .multiPart("text", description)
                .multiPart("tags", tags[0])
                .multiPart("tags", tags[1])
                .multiPart("file", new File(image), "image/png")
                .when()
                .post();
    }

    public void deletePostAfterUsing() {
        Response loginForDelete = getDefaultUserInfo();
        String userId = loginForDelete.jsonPath().getString("user.id");
        String token = loginForDelete.jsonPath().getString("accessToken");

        String newsId = RestAssured
                .given()
                    .baseUri(endpoints.baseUrl)
                    .basePath(endpoints.getPosts)
                    .queryParam("authorId", userId)
                .when()
                .get()
                .jsonPath()
                .getString("posts.id")
                .replace('[', ' ')
                .replace(']', ' ')
                .trim();

        RestAssured
                .given()
                    .baseUri(endpoints.baseUrl)
                    .basePath(endpoints.deletePostById)
                    .pathParam("id", newsId)
                    .header("Authorization", "Bearer " + token)
                .when()
                .delete();
    }

    public String getCommentId () {
        JSONObject requestBody = new JSONObject();
        String token = getDefaultUserInfo().jsonPath().getString("accessToken");

        Response createPostForAddComment = createPostBeforeUsing();
        String postId = createPostForAddComment.jsonPath().getString("id");
        requestBody.put("postId", postId);
        requestBody.put("text", comment);

        Response response = RestAssured
                .given()
                .baseUri(endpoints.baseUrl)
                .basePath(endpoints.createComment)
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .when()
                .post();

        return response.jsonPath().getString("id");
    }
}
