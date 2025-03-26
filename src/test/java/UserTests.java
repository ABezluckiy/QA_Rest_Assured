import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Constants;
import org.example.Endpoints;
import org.example.Messages;
import org.example.Methods;
import org.json.JSONObject;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class UserTests {
    private final SoftAssert assertions = new SoftAssert();
    private final Endpoints endpoints = new Endpoints();

    @Test
    @Feature("Работа с данными пользователя")
    @Description("Получение данных пользователя из токена")
    public void givenValidToken_whenGetUserInfo_thenUserInfoReturned() {
        String token = Methods.getCredentialsByDefaultUser().jsonPath().getString("accessToken");

        Response response = RestAssured
                .given()
                    .baseUri(endpoints.baseUrl)
                    .basePath(endpoints.getCurrentUserInfoByToken)
                    .header("Authorization", "Bearer " + token)
                .when()
                .get();

        assertions.assertEquals(response.statusCode(), 200, Messages.incorrectStatusCode);
        assertions.assertNotNull(response.jsonPath().getString("id"), Messages.userIdIsNull);
        assertions.assertNotNull(response.jsonPath().getString("email"), Messages.userEmailIsNull);

        assertions.assertAll();
    }

    @Test
    @Feature("Работа с данными пользователями")
    @Description("Получение данных пользователя без токена")
    public void givenEmptyToken_whenGetUserInfo_thenUnauthorizedReturned() {
        Response response = RestAssured
                    .given()
                    .baseUri(endpoints.baseUrl)
                    .basePath(endpoints.getCurrentUserInfoByToken)
                    .header("Authorization", "")
                .when()
                .get();

        assertions.assertEquals(response.statusCode(), 401, Messages.incorrectStatusCode);
        assertions.assertEquals(response.jsonPath().getString("message"), Messages.unauthorized, Messages.tokenIsValid);

        assertions.assertAll();
    }

    @Test
    @Feature("Работа с данными пользователями")
    @Description("Получение данных пользователя по уникальному идентификатору")
    public void givenValidUserId_whenGetUserInfo_thenUserInfoReturned() {
        String id = Methods.getCredentialsByDefaultUser().jsonPath().getString("user.id");

        Response response = RestAssured
                    .given()
                    .baseUri(endpoints.baseUrl)
                    .basePath(endpoints.getUserInfoById)
                    .pathParam("id", id)
                .when()
                .get();

        assertions.assertEquals(response.statusCode(), 200, Messages.incorrectStatusCode);
        assertions.assertEquals(response.jsonPath().getString("id"), id, Messages.idNotMatching);

        assertions.assertAll();
    }

    @Test
    @Feature("Работа с данными пользователями")
    @Description("Получение данных пользователя по уникальному идентификатору")
    public void givenInvalidDatatypeUserId_whenGetUserInfo_thenBadRequestReturned() {
        Response response = RestAssured
                    .given()
                    .baseUri(endpoints.baseUrl)
                    .basePath(endpoints.getUserInfoById)
                    .pathParam("id", "userId")
                .when()
                .get();

        assertions.assertEquals(response.statusCode(), 400, Messages.incorrectStatusCode);
        assertions.assertEquals(response.jsonPath().getString("message"), Messages.validationFailed, Messages.paramIsValid);
        assertions.assertEquals(response.jsonPath().getString("error"), Messages.badRequest, Messages.requestIsCorrect);

        assertions.assertAll();
    }

    @Test
    @Feature("Работа с данными пользователями")
    @Description("Получение данных пользователя по уникальному идентификатору")
    public void givenValidNewUserInfo_whenPatchUserInfo_thenNewUserInfoUpdatedAndReturned() {
        JSONObject requestBody = new JSONObject();
        Response login = Methods.getCredentialsByDefaultUser();
        String userId = login.jsonPath().getString("user.id");
        String token = login.jsonPath().getString("accessToken");
        String newFirstName = Methods.getUniqueString();
        String newLastName = Methods.getUniqueString();

        requestBody.put("firstName", newFirstName);
        requestBody.put("lastName", newLastName);

        Response response = RestAssured
                .given()
                    .baseUri(endpoints.baseUrl)
                    .basePath(endpoints.updateUserInfoById)
                    .pathParam("id", userId)
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .when()
                .patch();

        assertions.assertEquals(response.statusCode(), 200, Messages.incorrectStatusCode);
        assertions.assertEquals(response.jsonPath().getString("id"), userId, Messages.idNotMatching);
        assertions.assertEquals(response.jsonPath().getString("firstName"), newFirstName, Messages.userFirstNameNotChanged);
        assertions.assertEquals(response.jsonPath().getString("lastName"), newLastName, Messages.userLastNameNotChanged);

        assertions.assertAll();
        Methods.returnUserData(userId, token);
    }
}
