import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Constants;
import org.example.Endpoints;
import org.example.Messages;
import org.example.Methods;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import java.io.File;

public class PositiveCreatePostTests {
    private final Endpoints endpoints = new Endpoints();
    private final Methods methods = new Methods();
    private final String title = Constants.newsName;
    private final String description = Constants.newsDescription;
    private final String image = Constants.newsImage;
    private final String[] tags = Constants.newsTags;

    @Test(testName = "Успешное создание новости с валидными данными")
    @Feature("Работа с новостями")
    public void givenValidNewsData_whenCreateNews_thenNewsShouldCreate() {
        Response login = methods.getDefaultUserInfo();
        String token = login.jsonPath().getString("accessToken");
        String userId = login.jsonPath().getString("user.id");
        SoftAssert softAssert = new SoftAssert();

        Response response = RestAssured
                .given()
                .baseUri(endpoints.baseUrl)
                .basePath(endpoints.createPost)
                .header("Authorization", "Bearer " + token)
                .multiPart("title", title)
                .multiPart("text", description)
                .multiPart("tags", tags[0])
                .multiPart("tags", tags[1])
                .multiPart("file", new File(image), "image/png")
                .when()
                .post();

        softAssert.assertEquals(response.statusCode(), 201, Messages.incorrectStatusCode);
        softAssert.assertNotNull(response.jsonPath().getString("id"), Messages.postIdIsEmpty);
        softAssert.assertEquals(response.jsonPath().getString("authorId"), userId, Messages.idNotMatching);
        softAssert.assertEquals(response.jsonPath().getString("title"), title, Messages.titleNotMatching);
        softAssert.assertEquals(response.jsonPath().getString("text"), description, Messages.descriptionNotMatching);
        softAssert.assertNotNull(response.jsonPath().getList("tags"), Messages.tagsNotMatching);

        softAssert.assertAll();
        methods.deleteNewsAfterUsing(response.jsonPath().getString("id"), token);
    }
}
