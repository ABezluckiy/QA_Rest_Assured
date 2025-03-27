import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Endpoints;
import org.example.Messages;
import org.example.Methods;
import org.json.JSONObject;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;


public class UserTests {
    private final Endpoints endpoints = new Endpoints();
    private final Methods methods = new Methods();

    @Test(testName = "Успешное получение данных пользователя из токена")
    @Feature("Работа с данными пользователя")
    @Description("Получение данных пользователя из токена")
    public void givenValidToken_whenGetUserInfo_thenUserInfoReturned() {
        SoftAssert softAssert = new SoftAssert();
        String token = methods.getDefaultUserInfo().jsonPath().getString("accessToken");

        Response response = RestAssured
                .given()
                    .baseUri(endpoints.baseUrl)
                    .basePath(endpoints.getCurrentUserInfoByToken)
                    .header("Authorization", "Bearer " + token)
                .when()
                .get();

        softAssert.assertEquals(response.statusCode(), 200, Messages.incorrectStatusCode);
        softAssert.assertNotNull(response.jsonPath().getString("id"), Messages.userIdIsNull);
        softAssert.assertNotNull(response.jsonPath().getString("email"), Messages.userEmailIsNull);

        softAssert.assertAll();
    }

    @Test(testName = "Ошибка получения данных пользователя с невалидным токеном")
    @Feature("Работа с данными пользователями")
    @Description("Получение данных пользователя без токена")
    public void givenEmptyToken_whenGetUserInfo_thenUnauthorizedReturned() {
        SoftAssert softAssert = new SoftAssert();
        Response response = RestAssured
                    .given()
                    .baseUri(endpoints.baseUrl)
                    .basePath(endpoints.getCurrentUserInfoByToken)
                    .header("Authorization", "")
                .when()
                .get();

        softAssert.assertEquals(response.statusCode(), 401, Messages.incorrectStatusCode);
        softAssert.assertEquals(response.jsonPath().getString("message"), Messages.unauthorized, Messages.tokenIsValid);

        softAssert.assertAll();
    }

    @Test(testName = "Получение данных пользователя по id")
    @Feature("Работа с данными пользователями")
    @Description("Получение данных пользователя по уникальному идентификатору")
    public void givenValidUserId_whenGetUserInfo_thenUserInfoReturned() {
        SoftAssert softAssert = new SoftAssert();
        String id = methods.getDefaultUserInfo().jsonPath().getString("user.id");

        Response response = RestAssured
                    .given()
                    .baseUri(endpoints.baseUrl)
                    .basePath(endpoints.getUserInfoById)
                    .pathParam("id", id)
                .when()
                .get();

        softAssert.assertEquals(response.statusCode(), 200, Messages.incorrectStatusCode);
        softAssert.assertEquals(response.jsonPath().getString("id"), id, Messages.idNotMatching);

        softAssert.assertAll();
    }

    @Test(testName = "Ошибка получения данных пользователя с некорректным id")
    @Feature("Работа с данными пользователями")
    @Description("Ошибка при получение данных пользователя с некорректным уникальным идентификатором")
    public void givenInvalidDatatypeUserId_whenGetUserInfo_thenBadRequestReturned() {
        SoftAssert softAssert = new SoftAssert();
        Response response = RestAssured
                    .given()
                    .baseUri(endpoints.baseUrl)
                    .basePath(endpoints.getUserInfoById)
                    .pathParam("id", "userId")
                .when()
                .get();

        softAssert.assertEquals(response.statusCode(), 400, Messages.incorrectStatusCode);
        softAssert.assertEquals(response.jsonPath().getString("message"), Messages.validationFailed, Messages.paramIsValid);
        softAssert.assertEquals(response.jsonPath().getString("error"), Messages.badRequest, Messages.requestIsCorrect);

        softAssert.assertAll();
    }

    @Test(testName = "Успешное изменение информации пользователя")
    @Feature("Работа с данными пользователями")
    @Description("Успешное изменение информации пользователя")
    public void givenValidNewUserInfo_whenPatchUserInfo_thenNewUserInfoUpdatedAndReturned() {
        JSONObject requestBody = new JSONObject();
        Response login = methods.getDefaultUserInfo();
        String userId = login.jsonPath().getString("user.id");
        String token = login.jsonPath().getString("accessToken");
        String newFirstName = methods.getUniqueString();
        String newLastName = methods.getUniqueString();
        SoftAssert softAssert = new SoftAssert();

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

        softAssert.assertEquals(response.statusCode(), 200, Messages.incorrectStatusCode);
        softAssert.assertEquals(response.jsonPath().getString("id"), userId, Messages.idNotMatching);
        softAssert.assertEquals(response.jsonPath().getString("firstName"), newFirstName, Messages.userFirstNameNotChanged);
        softAssert.assertEquals(response.jsonPath().getString("lastName"), newLastName, Messages.userLastNameNotChanged);

        softAssert.assertAll();
        methods.returnUserData(userId, token);
    }

    @Test(testName = "Ошибка при попытке изменить данные пользователя пустым значением")
    @Feature("Работа с данными пользователями")
    @Description("Ошибка при попытке изменить данные пользователя пустым значением")
    public void givenEmptyData_whenPatchUserInfo_thenBadRequestReturned() {
        JSONObject requestBody = new JSONObject();
        Response login = methods.getDefaultUserInfo();
        String userId = login.jsonPath().getString("user.id");
        String token = login.jsonPath().getString("accessToken");
        SoftAssert softAssert = new SoftAssert();

        requestBody.put("firstName", "");
        requestBody.put("lastName", "");

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

        softAssert.assertEquals(response.statusCode(), 400, Messages.incorrectStatusCode);
        softAssert.assertTrue(response.jsonPath().getList("message").contains(Messages.firstNameShouldNotBeEmpty), Messages.firstNameShouldBeEmpty);
        softAssert.assertTrue(response.jsonPath().getList("message").contains(Messages.lastNameShouldNotBeEmpty), Messages.lastNameShouldBeEmpty);
        softAssert.assertEquals(response.jsonPath().getString("error"), Messages.badRequest, Messages.requestIsCorrect);

        softAssert.assertAll();
    }
}
