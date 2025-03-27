import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Constants;
import org.example.Endpoints;
import org.example.Messages;
import org.example.Methods;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class DeletePostTests {
    private final Endpoints endpoints = new Endpoints();
    private final Methods methods = new Methods();
    private final String title = Constants.postName;
    private final String description = Constants.postDescription;
    private final String image = Constants.postImage;
    private final String[] tags = Constants.postTags;


    @Test(testName = "Успешное удаление новости")
    @Feature("Работа с удалением новостей")
    public void givenValidPostId_whenDeletePost_thenPostDeleted() {
        Response login = methods.getDefaultUserInfo();
        String token = login.jsonPath().getString("accessToken");
        SoftAssert softAssert = new SoftAssert();

        Response createdPostForDelete = methods.createPostBeforeUsing(title, description, tags, image, token);

        Response response = RestAssured
                .given()
                .baseUri(endpoints.baseUrl)
                .basePath(endpoints.deletePostById)
                .pathParam("id", createdPostForDelete.jsonPath().getString("id"))
                .header("Authorization", "Bearer " + token)
                .when()
                .delete();

        softAssert.assertEquals(response.statusCode(), 200, Messages.incorrectStatusCode);
        softAssert.assertAll();
    }

    @Test(testName = "Ошибка при удалении новости с несуществующему id")
    @Feature("Работа с удалением новостей")
    public void givenNotExistsPostId_whenDeletePost_thenReturnBadRequest() {
        Response login = methods.getDefaultUserInfo();
        String token = login.jsonPath().getString("accessToken");
        SoftAssert softAssert = new SoftAssert();

        Response createdPostForDelete = methods.createPostBeforeUsing(title, description, tags, image, token);

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

        methods.deletePostAfterUsing(createdPostForDelete.jsonPath().getString("id"), token);
    }

    @Test(testName = "Ошибка при удалении новости с некорректным типом данных id")
    @Feature("Работа с удалением новостей")
    public void givenInvalidPostId_whenDeletePost_thenReturnBadRequest() {
        Response login = methods.getDefaultUserInfo();
        String token = login.jsonPath().getString("accessToken");
        SoftAssert softAssert = new SoftAssert();

        Response createdPostForDelete = methods.createPostBeforeUsing(title, description, tags, image, token);

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

        methods.deletePostAfterUsing(createdPostForDelete.jsonPath().getString("id"), token);
    }
}
