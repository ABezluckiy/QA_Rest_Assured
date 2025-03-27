import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Constants;
import org.example.Endpoints;
import org.example.Messages;
import org.example.Methods;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class GetPostsTests {
    private final Endpoints endpoints = new Endpoints();
    private final Methods methods = new Methods();

    @Test(testName = "Успешное получение всех новостей с ограничением количества")
    @Feature("Работа с новостями")
    public void givenValidPageableParameters_whenGetPostList_thenReturnedPageablePostList() {
        Response login = methods.getDefaultUserInfo();
        SoftAssert softAssert = new SoftAssert();
        Response createdPostForGet = methods.createPostBeforeUsing();

        Response response = RestAssured
                .given()
                .baseUri(endpoints.baseUrl)
                .basePath(endpoints.getPosts)
                .queryParam("limit", Constants.postLimit)
                .when()
                .get();

        softAssert.assertEquals(response.statusCode(), 200, Messages.incorrectStatusCode);
        softAssert.assertEquals(response.jsonPath().getList("posts").size(), Constants.postLimit, Messages.sizeMismatched);

        softAssert.assertAll();

        methods.deletePostAfterUsing();
    }

    @Test(testName = "Успешное получение новости по уникальному идентификатору")
    @Feature("Работа с новостями")
    public void givenValidPostId_whenGetPost_thenReturnedPostInfo() {
        SoftAssert softAssert = new SoftAssert();

        Response createdPostForSearch = methods.createPostBeforeUsing();

        Response response = RestAssured
                .given()
                .baseUri(endpoints.baseUrl)
                .basePath(endpoints.getPostById)
                .pathParam("id", createdPostForSearch.jsonPath().getString("id"))
                .when()
                .get();

        softAssert.assertEquals(response.statusCode(), 200, Messages.incorrectStatusCode);
        softAssert.assertNotNull(response.jsonPath().getString("id"), Messages.postIdIsEmpty);
        softAssert.assertEquals(response.jsonPath().getString("title"), Constants.postName, Messages.titleNotMismatched);
        softAssert.assertEquals(response.jsonPath().getString("text"), Constants.postDescription, Messages.descriptionMismatched);
        softAssert.assertNotNull(response.jsonPath().getList("tags"), Messages.tagsNotMismatched);

        softAssert.assertAll();
        methods.deletePostAfterUsing();
    }

    @Test(testName = "Ошибка при попытке получить новость с невалидными данными")
    @Feature("Работа с новостями")
    public void givenInvalidPageableParameters_whenGetPostList_thenReturnedServerError() {
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
