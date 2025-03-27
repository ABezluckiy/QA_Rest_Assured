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

public class RegistrationTest {
    private final Endpoints endpoints = new Endpoints();
    private final RequestSpecification signUpUrl = RestAssured.given().baseUri(endpoints.baseUrl).basePath(endpoints.signup);
    private JSONObject requestBody;


    @Ignore
    @Test(testName = "Успешная регистрация пользователя с валидными данными")
    @Feature("Регистрация пользователя")
    @Description("Тест регистрации пользователя с валидными данными")
    public void givenValidCredentials_whenSignUp_thenSuccess() {
        SoftAssert softAssert = new SoftAssert();
        requestBody = new JSONObject();

        String userEmail = Constants.correctEmailUser;
        requestBody.put("email", userEmail);
        requestBody.put("password", Constants.correctPasswordUser);

        Response response = signUpUrl
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .when()
                .post();

        softAssert.assertEquals(response.statusCode(), 201, Messages.incorrectStatusCode);
        softAssert.assertEquals(response.jsonPath().getString("user.email"), userEmail, Messages.incorrectUserEmail);
        softAssert.assertNotNull(response.jsonPath().getString("accessToken"), Messages.tokenIsNull);
        softAssert.assertAll();
    }

    @Test(testName = "Ошибка регистрации пользователя с почтой уже существующей в базе")
    @Feature("Регистрация пользователя")
    public void givenExistsCredentials_whenSignUp_thenError() {
        SoftAssert softAssert = new SoftAssert();
        requestBody = new JSONObject();

        String userEmail = Constants.correctEmailUser;
        requestBody.put("email", userEmail);
        requestBody.put("password", Constants.correctPasswordUser);

        Response response = signUpUrl
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .when()
                .post();

        softAssert.assertEquals(response.statusCode(), 400, Messages.incorrectStatusCode);
        softAssert.assertTrue(response.jsonPath().getString("message").equals(Messages.validationError), Messages.validationError);
        softAssert.assertTrue(response.jsonPath().getList("errors").contains(Messages.emailMustBeUnique), Messages.emailMustBeUnique);
        softAssert.assertAll();
    }

    @Test(testName = "Ошибка регистрации пользователя с пустыми данными")
    @Feature("Регистрация пользователя")
    public void givenEmptyCredentials_whenSignUp_thenErrors() {
        SoftAssert softAssert = new SoftAssert();
        requestBody = new JSONObject();
        requestBody.put("email", "");
        requestBody.put("password", "");

        Response response = signUpUrl
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .when()
                .post();

        softAssert.assertEquals(response.statusCode(), 400, Messages.incorrectStatusCode);
        softAssert.assertTrue(response.jsonPath().getString("error").equals(Messages.badRequest), Messages.requestIsCorrect);
        softAssert.assertTrue(response.jsonPath().getList("message").contains(Messages.emailShouldBePresent), Messages.emailMustBeUnique);
        softAssert.assertTrue(response.jsonPath().getList("message").contains(Messages.emailShouldNotBeEmpty), Messages.emailMustBeUnique);
        softAssert.assertTrue(response.jsonPath().getList("message").contains(Messages.passwordShouldNotBeEmpty), Messages.emailMustBeUnique);
        softAssert.assertAll();
    }

    @Test(testName = "Ошибка регистрации пользователя с пустой почтой")
    @Feature("Регистрация пользователя")
    public void givenEmptyEmail_whenSignUp_thenErrors() {
        SoftAssert softAssert = new SoftAssert();
        requestBody = new JSONObject();
        requestBody.put("email", "");
        requestBody.put("password", Constants.correctPasswordUser);

        Response response = signUpUrl
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .when()
                .post();

        softAssert.assertEquals(response.statusCode(), 400, Messages.incorrectStatusCode);
        softAssert.assertTrue(response.jsonPath().getString("error").equals(Messages.badRequest), Messages.requestIsCorrect);
        softAssert.assertTrue(response.jsonPath().getList("message").contains(Messages.emailShouldBePresent), Messages.emailMustBeUnique);
        softAssert.assertTrue(response.jsonPath().getList("message").contains(Messages.emailShouldNotBeEmpty), Messages.emailMustBeUnique);
        softAssert.assertAll();
    }
}
