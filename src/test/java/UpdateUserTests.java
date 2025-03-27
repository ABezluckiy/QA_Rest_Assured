import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Endpoints;
import org.example.Messages;
import org.example.Methods;
import org.json.JSONObject;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class UpdateUserTests {
    private final Endpoints endpoints = new Endpoints();
    private final Methods methods = new Methods();

    @Test(testName = "Успешное изменение информации пользователя")
    @Feature("Работа с данными пользователями")
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
        softAssert.assertEquals(response.jsonPath().getString("id"), userId, Messages.idMismatched);
        softAssert.assertEquals(response.jsonPath().getString("firstName"), newFirstName, Messages.userFirstNameNotChanged);
        softAssert.assertEquals(response.jsonPath().getString("lastName"), newLastName, Messages.userLastNameNotChanged);

        softAssert.assertAll();
        methods.returnUserData(userId, token);
    }

    @Test(testName = "Ошибка при попытке изменить данные пользователя пустым значением")
    @Feature("Работа с данными пользователями")
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
