import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Constants;
import org.example.Endpoints;
import org.example.Messages;
import org.example.Methods;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class NegativeGetPostsTests {
    private final Endpoints endpoints = new Endpoints();

    @Test(testName = "Ошибка при попытке получить новость с невалидными данными")
    @Feature("Работа с новостями")
    public void givenInvalidPageableParameters_whenGetNewsList_thenReturnedServerError() {
        SoftAssert softAssert = new SoftAssert();

        Response response = RestAssured
                .given()
                .baseUri(endpoints.baseUrl)
                .basePath(endpoints.getPosts)
                .queryParam("limit", "text")
                .when()
                .get();

        softAssert.assertEquals(response.statusCode(), 500, Messages.incorrectStatusCode);
        softAssert.assertEquals(response.jsonPath().getString("message"), Messages.internalServerError, Messages.serverMustBeReturnError);

        softAssert.assertAll();
    }
}
