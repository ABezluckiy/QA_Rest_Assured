import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.example.Constants;
import org.example.Endpoints;
import org.example.Messages;
import org.json.JSONObject;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class PositiveRegistrationTest {
    private final Endpoints endpoints = new Endpoints();
    private final RequestSpecification signUpUrl = RestAssured.given().baseUri(endpoints.baseUrl).basePath(endpoints.signup);

    @Ignore
    @Test(testName = "Успешная регистрация пользователя с валидными данными")
    @Feature("Регистрация пользователя")
    @Description("Тест регистрации пользователя с валидными данными")
    public void givenValidCredentials_whenSignUp_thenSuccess() {
        SoftAssert softAssert = new SoftAssert();
        JSONObject requestBody = new JSONObject();

        String userEmail = Constants.correctEmailUser;
        requestBody.put("email", userEmail);
        requestBody.put("password", Constants.correctPasswordUser);

        Response response = signUpUrl
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .when()
                .post();

        softAssert.assertEquals(response.statusCode(), 201, Messages.incorrectStatusCode);
        softAssert.assertEquals(response.jsonPath().getString("user.email"),userEmail, Messages.incorrectUserEmail);
        softAssert.assertNotNull(response.jsonPath().getString("accessToken"), Messages.tokenIsNull);
        softAssert.assertAll();
    }
}
