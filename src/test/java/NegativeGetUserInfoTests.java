import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Endpoints;
import org.example.Messages;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class NegativeGetUserInfoTests {
    private final Endpoints endpoints = new Endpoints();

    @Test(testName = "Ошибка получения данных пользователя с невалидным токеном")
    @Feature("Работа с данными пользователями")
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
}
