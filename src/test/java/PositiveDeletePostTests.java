import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Constants;
import org.example.Endpoints;
import org.example.Messages;
import org.example.Methods;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class PositiveDeletePostTests {
    private final Endpoints endpoints = new Endpoints();
    private final Methods methods = new Methods();
    private final String title = Constants.newsName;
    private final String description = Constants.newsDescription;
    private final String image = Constants.newsImage;
    private final String[] tags = Constants.newsTags;


    @Test(testName = "Успешное удаление новости")
    @Feature("Работа с удалением новостей")
    public void givenValidNewsId_whenDeleteNews_thenNewsDeleted() {
        Response login = methods.getDefaultUserInfo();
        String token = login.jsonPath().getString("accessToken");
        SoftAssert softAssert = new SoftAssert();

        Response createdNewsForDelete = methods.createNewsBeforeUsing(title, description, tags, image, token);

        Response response = RestAssured
                .given()
                .baseUri(endpoints.baseUrl)
                .basePath(endpoints.deletePostById)
                .pathParam("id", createdNewsForDelete.jsonPath().getString("id"))
                .header("Authorization", "Bearer " + token)
                .when()
                .delete();

        softAssert.assertEquals(response.statusCode(), 200, Messages.incorrectStatusCode);
        softAssert.assertAll();
    }
}
