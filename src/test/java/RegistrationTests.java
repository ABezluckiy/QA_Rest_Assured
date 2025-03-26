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

public class RegistrationTests {
    private final SoftAssert assertions = new SoftAssert();
    private final Endpoints endpoints = new Endpoints();
    private JSONObject requestBody;
    private final RequestSpecification signUpUrl = RestAssured.given().baseUri(endpoints.baseUrl).basePath(endpoints.signup);

    @Ignore
    @Test
    @Feature("Регистрация пользователя")
    @Description("Тест регистрации пользователя с валидными данными")
    public void givenValidCredentials_whenSignUp_thenSuccess() {
        requestBody = new JSONObject();

        String userEmail = Constants.correctEmailUser;
        requestBody.put("email", userEmail);
        requestBody.put("password", Constants.correctPasswordUser);

        Response response = signUpUrl
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .when()
                .post();

        assertions.assertEquals(response.statusCode(), 201, Messages.incorrectStatusCode);
        assertions.assertEquals(response.jsonPath().getString("user.email"),userEmail, Messages.incorrectUserEmail);
        assertions.assertNotNull(response.jsonPath().getString("accessToken"), Messages.tokenIsNull);
        assertions.assertAll();
    }

    @Test
    @Feature("Регистрация пользователя")
    @Description("Тест регистрации пользователя с существующей почтой")
    public void givenExistsCredentials_whenSignUp_thenError() {
        requestBody = new JSONObject();

        String userEmail = Constants.correctEmailUser;
        requestBody.put("email", userEmail);
        requestBody.put("password", Constants.correctPasswordUser);

        Response response = signUpUrl
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .when()
                .post();

        assertions.assertEquals(response.statusCode(), 400, Messages.incorrectStatusCode);
        assertions.assertTrue(response.jsonPath().getString("message").equals(Messages.validationError), Messages.validationError);
        assertions.assertTrue(response.jsonPath().getList("errors").contains(Messages.emailMustBeUnique), Messages.emailMustBeUnique);
        assertions.assertAll();
    }

    @Test
    @Feature("Регистрация пользователя")
    @Description("Тест регистрации пользователя с пустыми данными")
    public void givenEmptyCredentials_whenSignUp_thenErrors() {
        requestBody = new JSONObject();
        requestBody.put("email", "");
        requestBody.put("password", "");

        Response response = signUpUrl
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .when()
                .post();

        assertions.assertEquals(response.statusCode(), 400, Messages.incorrectStatusCode);
        assertions.assertTrue(response.jsonPath().getString("error").equals(Messages.badRequest), Messages.badRequest);
        assertions.assertTrue(response.jsonPath().getList("message").contains(Messages.emailShouldBePresent), Messages.emailMustBeUnique);
        assertions.assertTrue(response.jsonPath().getList("message").contains(Messages.emailShouldNotBeEmpty), Messages.emailMustBeUnique);
        assertions.assertTrue(response.jsonPath().getList("message").contains(Messages.passwordShouldNotBeEmpty), Messages.emailMustBeUnique);
        assertions.assertAll();
    }

    @Test
    @Feature("Регистрация пользователя")
    @Description("Тест регистрации пользователя с пустыми данными")
    public void givenEmptyEmail_whenSignUp_thenErrors() {
        requestBody = new JSONObject();
        requestBody.put("email", "");
        requestBody.put("password", Constants.correctPasswordUser);

        Response response = signUpUrl
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .when()
                .post();

        assertions.assertEquals(response.statusCode(), 400, Messages.incorrectStatusCode);
        assertions.assertTrue(response.jsonPath().getString("error").equals(Messages.badRequest), Messages.badRequest);
        assertions.assertTrue(response.jsonPath().getList("message").contains(Messages.emailShouldBePresent), Messages.emailMustBeUnique);
        assertions.assertTrue(response.jsonPath().getList("message").contains(Messages.emailShouldNotBeEmpty), Messages.emailMustBeUnique);
        assertions.assertAll();
    }
}
