import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Constants;
import org.example.Endpoints;
import org.example.Messages;
import org.example.Methods;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class NegativeDeletePostTests {
    private final Endpoints endpoints = new Endpoints();
    private final Methods methods = new Methods();
    private final String title = Constants.newsName;
    private final String description = Constants.newsDescription;
    private final String image = Constants.newsImage;
    private final String[] tags = Constants.newsTags;

    @Test(testName = "Ошибка при удалении новости с несуществующему id")
    @Feature("Работа с удалением новостей")
    public void givenNotExistsNewsId_whenDeleteNews_thenReturnBadRequest() {
        Response login = methods.getDefaultUserInfo();
        String token = login.jsonPath().getString("accessToken");
        SoftAssert softAssert = new SoftAssert();

        Response createdNewsForDelete = methods.createNewsBeforeUsing(title, description, tags, image, token);

        Response response = RestAssured
                .given()
                .baseUri(endpoints.baseUrl)
                .basePath(endpoints.deletePostById)
                .pathParam("id", -1)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete();

        softAssert.assertEquals(response.statusCode(), 404, Messages.incorrectStatusCode);
        softAssert.assertEquals(response.jsonPath().getString("message"), Messages.notFound, Messages.postWasFound);
        softAssert.assertAll();

        methods.deleteNewsAfterUsing(createdNewsForDelete.jsonPath().getString("id"), token);
    }

    @Test(testName = "Ошибка при удалении новости с некорректным типом данных id")
    @Feature("Работа с удалением новостей")
    public void givenInvalidPostId_whenDeletePost_thenReturnBadRequest() {
        Response login = methods.getDefaultUserInfo();
        String token = login.jsonPath().getString("accessToken");
        SoftAssert softAssert = new SoftAssert();

        Response createdNewsForDelete = methods.createNewsBeforeUsing(title, description, tags, image, token);

        Response response = RestAssured
                .given()
                .baseUri(endpoints.baseUrl)
                .basePath(endpoints.deletePostById)
                .pathParam("id", "invalidId")
                .header("Authorization", "Bearer " + token)
                .when()
                .delete();

        softAssert.assertEquals(response.statusCode(), 400, Messages.incorrectStatusCode);
        softAssert.assertEquals(response.jsonPath().getString("message"), Messages.validationFailed, Messages.paramIsValid);
        softAssert.assertAll();

        methods.deleteNewsAfterUsing(createdNewsForDelete.jsonPath().getString("id"), token);
    }
}
