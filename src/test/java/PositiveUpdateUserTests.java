import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Endpoints;
import org.example.Messages;
import org.example.Methods;
import org.json.JSONObject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class PositiveUpdateUserTests {
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
        softAssert.assertEquals(response.jsonPath().getString("id"), userId, Messages.idNotMatching);
        softAssert.assertEquals(response.jsonPath().getString("firstName"), newFirstName, Messages.userFirstNameNotChanged);
        softAssert.assertEquals(response.jsonPath().getString("lastName"), newLastName, Messages.userLastNameNotChanged);

        softAssert.assertAll();
        methods.returnUserData(userId, token);
    }
}
