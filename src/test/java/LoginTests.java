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
    private final Endpoints endpoints = new Endpoints();
    private final RequestSpecification loginUrl = RestAssured.given().baseUri(endpoints.baseUrl).basePath(endpoints.login);
    private JSONObject requestBody;

    @Test(testName = "Успешная авторизация пользователя с валидными данными")
    @Feature("Авторизация пользователя")
    public void givenValidCredentials_whenLogin_thenSuccess() {
        SoftAssert softAssert = new SoftAssert();
        requestBody = new JSONObject();
        requestBody.put("email", Constants.correctEmailUser);
        requestBody.put("password", Constants.correctPasswordUser);

        Response response = loginUrl
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .when()
                .post();

        softAssert.assertEquals(response.statusCode(), 200, Messages.incorrectStatusCode);
        softAssert.assertNotNull(response.jsonPath().getString("accessToken"), Messages.tokenIsNull);
        softAssert.assertAll();
    }

    @Test(testName = "Ошибка авторизации пользователя с некорректной почтой")
    @Feature("Авторизация пользователя")
    public void givenIncorrectEmailCredentials_whenLogin_thenUnauthorized() {
        SoftAssert softAssert = new SoftAssert();
        requestBody = new JSONObject();
        requestBody.put("email", Constants.incorrectEmailUser);
        requestBody.put("password", Constants.correctPasswordUser);

        Response response = loginUrl
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .when()
                .post();

        softAssert.assertEquals(response.statusCode(), 401, Messages.incorrectStatusCode);
        softAssert.assertTrue(response.jsonPath().getString("message").equals(Messages.unauthorized), Messages.userAuthorized);
        softAssert.assertAll();
    }

    @Test(testName = "Ошибка авторизации пользователя с неподходящим паролем")
    @Feature("Авторизация пользователя")
    public void givenIncorrectPasswordCredentials_whenLogin_thenUnauthorized() {
        SoftAssert softAssert = new SoftAssert();
        requestBody = new JSONObject();
        requestBody.put("email", Constants.correctEmailUser);
        requestBody.put("password", Constants.incorrectPasswordUser);

        Response response = loginUrl
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .when()
                .post();

        softAssert.assertEquals(response.statusCode(), 401, Messages.incorrectStatusCode);
        softAssert.assertTrue(response.jsonPath().getString("message").equals(Messages.unauthorized), Messages.userAuthorized);
        softAssert.assertAll();
    }
}
