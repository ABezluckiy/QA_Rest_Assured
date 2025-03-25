import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.example.Constants;
import org.example.Endpoints;
import org.example.Messages;
import org.json.JSONObject;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class LoginTests {
    private final SoftAssert assertions = new SoftAssert();
    private final Endpoints endpoints = new Endpoints();
    private final JSONObject requestBody = new JSONObject();
    private final RequestSpecification loginUrl = RestAssured.given().baseUri(endpoints.baseUrl).basePath(endpoints.login);

    @Test
    @Feature("Авторизация пользователя")
    @Description("Тест авторизации с валидными данными")
    public void givenValidCredentials_whenLogin_thenSuccess() {
        requestBody.put("email", Constants.correctEmailUser);
        requestBody.put("password", Constants.correctPasswordUser);

        Response response = loginUrl
                    .header("Content-Type", "application/json")
                    .body(requestBody.toString())
                .when()
                .post();

        assertions.assertEquals(response.statusCode(), 200, Messages.incorrectStatusCode);
        assertions.assertNotNull(response.jsonPath().getString("accessToken"), Messages.tokenIsNull);
        assertions.assertAll();
    }

    @Test
    @Feature("Авторизация пользователя")
    @Description("Тест авторизации с невалидными данными")
    public void givenIncorrectEmailCredentials_whenLogin_thenUnauthorized() {
        requestBody.put("email", Constants.incorrectEmailUser);
        requestBody.put("password", Constants.correctPasswordUser);

        Response response = loginUrl
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .when()
                .post();

        assertions.assertEquals(response.statusCode(), 401, Messages.incorrectStatusCode);
        assertions.assertTrue(response.jsonPath().getString("message").equals(Messages.unauthorized), Messages.userNotAuthorized);
        assertions.assertAll();
    }

    @Test
    @Feature("Авторизация пользователя")
    @Description("Тест авторизации с невалидными данными")
    public void givenIncorrectPasswordCredentials_whenLogin_thenUnauthorized() {
        requestBody.put("email", Constants.correctEmailUser);
        requestBody.put("password", Constants.incorrectPasswordUser);

        Response response = loginUrl
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .when()
                .post();

        assertions.assertEquals(response.statusCode(), 401, Messages.incorrectStatusCode);
        assertions.assertTrue(response.jsonPath().getString("message").equals(Messages.unauthorized), Messages.unauthorized);
        assertions.assertAll();
    }
}
