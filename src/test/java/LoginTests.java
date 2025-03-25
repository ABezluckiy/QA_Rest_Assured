import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.example.Constants;
import org.example.Endpoints;
import org.json.JSONObject;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class LoginTests {
    private final SoftAssert assertions = new SoftAssert();
    private final Endpoints endpoints = new Endpoints();
    private final JSONObject requestBody = new JSONObject();
    private final RequestSpecification baseUrl = RestAssured.given().baseUri(endpoints.baseUrl);

    @Test
    @Feature("Авторизация пользователя")
    @Description("Тест авторизации с валидными данными")
    public void givenValidCredentials_whenLogin_thenSuccess() {
        requestBody.put("email", Constants.correctEmailUser);
        requestBody.put("password", Constants.passwordUser);

        Response response = baseUrl
                    .basePath(endpoints.login)
                    .header("Content-Type", "application/json")
                    .body(requestBody.toString())
                .when()
                .post();

        assertions.assertEquals(response.statusCode(), 200, "Incorrect status code.");
        assertions.assertNotNull(response.jsonPath().getString("accessToken"), "Token is null");
        assertions.assertAll();
    }
}
