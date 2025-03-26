import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.example.Endpoints;
import org.example.Messages;
import org.example.Methods;
import org.json.JSONObject;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class UserTests {
    private final SoftAssert assertions = new SoftAssert();
    private final Endpoints endpoints = new Endpoints();
    private final JSONObject requestBody = new JSONObject();
    private final RequestSpecification baseUrl = RestAssured.given().baseUri(endpoints.baseUrl);

    @Test
    @Feature("Работа с данными пользователя")
    @Description("Получение данных пользователя из токена")
    public void givenValidToken_whenGetUserInfo_thenUserInfoReturned() {
        String token = Methods.getTokenByDefaultUser().jsonPath().getString("accessToken");

        Response response = baseUrl
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
        Response response = baseUrl
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
        String id = Methods.getTokenByDefaultUser().jsonPath().getString("user.id");

        Response response = baseUrl
                .basePath(endpoints.updateUserInfoById)
                .header("Authorization", "")
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
    public void givenInvalidUserId_whenGetUserInfo_thenBadRequestReturned() {
        Response response = baseUrl
                .basePath(endpoints.updateUserInfoById)
                .header("Authorization", "")
                .pathParam("id", "userId")
                .when()
                .get();

        assertions.assertEquals(response.statusCode(), 400, Messages.incorrectStatusCode);
        assertions.assertEquals(response.jsonPath().getString("message"), Messages.validationFailed, Messages.paramIsValid);
        assertions.assertEquals(response.jsonPath().getString("error"), Messages.badRequest, Messages.requestIsCorrect);

        assertions.assertAll();
    }
}
