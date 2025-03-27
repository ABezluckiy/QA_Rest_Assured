import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Constants;
import org.example.Endpoints;
import org.example.Messages;
import org.example.Methods;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class PositiveUpdatePostTests {
    private final Endpoints endpoints = new Endpoints();
    private final Methods methods = new Methods();
    private final String title = Constants.newsName;
    private final String description = Constants.newsDescription;
    private final String image = Constants.newsImage;
    private final String[] tags = Constants.newsTags;
    private final String newTitle = methods.getUniqueString();
    private final String newDescription = methods.getUniqueString();

    @Test(testName = "Изменение названия и описания новости по уникальному идентификатору")
    @Feature("Работа с новостями")
    public void givenValidNewsIdAndNewData_whenUpdateNews_thenReturnedNewsInfo() {
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
                .multiPart("title", newTitle)
                .multiPart("text", newDescription)
                .when()
                .patch();

        softAssert.assertEquals(response.statusCode(), 200, Messages.incorrectStatusCode);
        softAssert.assertEquals(response.jsonPath().getString("id"), createdNewsForUpdate.jsonPath().getString("id"), Messages.idNotMatching);
        softAssert.assertEquals(response.jsonPath().getString("title"), newTitle, Messages.titleNotMatching);
        softAssert.assertEquals(response.jsonPath().getString("text"), newDescription, Messages.descriptionNotMatching);

        softAssert.assertAll();

        methods.deleteNewsAfterUsing(createdNewsForUpdate.jsonPath().getString("id"), token);
    }
}
