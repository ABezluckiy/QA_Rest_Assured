package org.example;

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
}
