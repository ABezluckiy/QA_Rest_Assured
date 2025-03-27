import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Constants;
import org.example.Endpoints;
import org.example.Messages;
import org.example.Methods;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class NegativeUpdatePostTests {
    private final Endpoints endpoints = new Endpoints();
    private final Methods methods = new Methods();
    private final String title = Constants.newsName;
    private final String description = Constants.newsDescription;
    private final String image = Constants.newsImage;
    private final String[] tags = Constants.newsTags;

    @Test(testName = "Ошибка при изменении названия и описания новости пустыми значениями")
    @Feature("Работа с новостями")
    public void givenInvalidNewsIdAndNewData_whenUpdateNews_thenReturnedBadRequest() {
        Response login = methods.getDefaultUserInfo();
        String token = login.jsonPath().getString("accessToken");
        SoftAssert softAssert = new SoftAssert();

        Response createdNewsForUpdate = methods.createNewsBeforeUsing(title, description, tags, image, token);

        Response response = RestAssured
                .given()
                .baseUri(endpoints.baseUrl)
                .basePath(endpoints.updatePostById)
                .header("Authorization", "Bearer " + token)
                .pathParam("id", createdNewsForUpdate.jsonPath().getString("id"))
                .multiPart("title", "")
                .multiPart("text", "")
                .when()
                .patch();

        softAssert.assertEquals(response.statusCode(), 400, Messages.incorrectStatusCode);
        softAssert.assertTrue(response.jsonPath().getList("message").contains(Messages.textShouldNotBeEmpty), Messages.textNotEmpty);
        softAssert.assertTrue(response.jsonPath().getList("message").contains(Messages.titleShouldNotBeEmpty), Messages.titleNotEmpty);
        softAssert.assertEquals(response.jsonPath().getString("error"), Messages.badRequest, Messages.requestIsCorrect);

        softAssert.assertAll();
    }
}