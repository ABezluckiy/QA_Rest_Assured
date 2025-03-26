package org.example;

import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;

public class Methods {
    private static final Endpoints endpoints = new Endpoints();

    public static String getUniqueString() {
        return UUID.randomUUID().toString();
    }

    public static Response getTokenByDefaultUser() {
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

}
