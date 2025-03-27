import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Endpoints;
import org.example.Messages;
import org.example.Methods;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class PositiveGetUserInfoTests {
    private final Endpoints endpoints = new Endpoints();
    private final Methods methods = new Methods();

    @Test(testName = "Успешное получение данных пользователя из токена")
    @Feature("Работа с данными пользователя")
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

    @Test(testName = "Успешное получение данных пользователя по id")
    @Feature("Работа с данными пользователями")
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
}
