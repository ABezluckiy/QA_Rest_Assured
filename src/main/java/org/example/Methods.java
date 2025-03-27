package org.example;

import java.io.File;
import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;

public class Methods {
    private static final Endpoints endpoints = new Endpoints();

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

    public Response createNewsBeforeUsing(
            String title,
            String description,
            String[] tags,
            String image,
            String token) {
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

    public void deleteNewsAfterUsing(String id, String token) {
        RestAssured
                .given()
                .baseUri(endpoints.baseUrl)
                .basePath(endpoints.deletePostById)
                .pathParam("id", id)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete();
    }
}
