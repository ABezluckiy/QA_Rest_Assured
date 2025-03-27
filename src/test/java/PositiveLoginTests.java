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

public class PositiveLoginTests {
    private final Endpoints endpoints = new Endpoints();
    private final RequestSpecification loginUrl = RestAssured.given().baseUri(endpoints.baseUrl).basePath(endpoints.login);

    @Test(testName = "Успешная авторизация пользователя с валидными данными")
    @Feature("Авторизация пользователя")
    public void givenValidCredentials_whenLogin_thenSuccess() {
        SoftAssert softAssert = new SoftAssert();
        JSONObject requestBody = new JSONObject();
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
}
