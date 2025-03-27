import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Constants;
import org.example.Endpoints;
import org.example.Messages;
import org.example.Methods;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class PositiveGetPostsTests {
    private final Endpoints endpoints = new Endpoints();
    private final Methods methods = new Methods();
    private final String title = Constants.newsName;
    private final String description = Constants.newsDescription;
    private final String image = Constants.newsImage;
    private final String[] tags = Constants.newsTags;

    @Test(testName = "Успешное получение всех новостей с ограничением количества")
    @Feature("Работа с новостями")
    public void givenValidPageableParameters_whenGetNewsList_thenReturnedPageableNewsList() {
        Response login = methods.getDefaultUserInfo();
        String token = login.jsonPath().getString("accessToken");
        SoftAssert softAssert = new SoftAssert();
        Response createdNewsForGet = methods.createNewsBeforeUsing(title, description, tags, image, token);

        Response response = RestAssured
                .given()
                .baseUri(endpoints.baseUrl)
                .basePath(endpoints.getPosts)
                .queryParam("limit", Constants.newsLimit)
                .when()
                .get();

        softAssert.assertEquals(response.statusCode(), 200, Messages.incorrectStatusCode);
        softAssert.assertEquals(response.jsonPath().getList("posts").size(), Constants.newsLimit, Messages.sizeNotMatching);

        softAssert.assertAll();

        methods.deleteNewsAfterUsing(createdNewsForGet.jsonPath().getString("id"), token);
    }

    @Test(testName = "Успешное получение новости по уникальному идентификатору")
    @Feature("Работа с новостями")
    public void givenValidNewsId_whenGetNews_thenReturnedNewsInfo() {
        Response login = methods.getDefaultUserInfo();
        String token = login.jsonPath().getString("accessToken");
        String userId = login.jsonPath().getString("user.id");
        SoftAssert softAssert = new SoftAssert();

        Response createdNewsForSearch = methods.createNewsBeforeUsing(title, description, tags, image, token);

        Response response = RestAssured
                .given()
                .baseUri(endpoints.baseUrl)
                .basePath(endpoints.getPostById)
                .pathParam("id", createdNewsForSearch.jsonPath().getString("id"))
                .when()
                .get();

        softAssert.assertEquals(response.statusCode(), 200, Messages.incorrectStatusCode);
        softAssert.assertNotNull(response.jsonPath().getString("id"), Messages.newsIdIsEmpty);
        softAssert.assertEquals(response.jsonPath().getString("authorId"), userId, Messages.idNotMatching);
        softAssert.assertEquals(response.jsonPath().getString("title"), title, Messages.titleNotMatching);
        softAssert.assertEquals(response.jsonPath().getString("text"), description, Messages.descriptionNotMatching);
        softAssert.assertNotNull(response.jsonPath().getList("tags"), Messages.tagsNotMatching);

        softAssert.assertAll();
        methods.deleteNewsAfterUsing(createdNewsForSearch.jsonPath().getString("id"), token);
    }
}
