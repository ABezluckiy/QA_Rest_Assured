import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Constants;
import org.example.Endpoints;
import org.example.Messages;
import org.example.Methods;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class UpdatePostTests {
    private final Endpoints endpoints = new Endpoints();
    private final Methods methods = new Methods();
    private final String title = Constants.postName;
    private final String description = Constants.postDescription;
    private final String image = Constants.postImage;
    private final String[] tags = Constants.postTags;
    private final String newTitle = methods.getUniqueString();
    private final String newDescription = methods.getUniqueString();

    @Test(testName = "Изменение названия и описания новости по уникальному идентификатору")
    @Feature("Работа с новостями")
    public void givenValidPostIdAndNewData_whenUpdatePost_thenReturnedPostInfo() {
        Response login = methods.getDefaultUserInfo();
        String token = login.jsonPath().getString("accessToken");
        SoftAssert softAssert = new SoftAssert();

        Response createdPostForUpdate = methods.createPostBeforeUsing(title, description, tags, image, token);

        Response response = RestAssured
                .given()
                .baseUri(endpoints.baseUrl)
                .basePath(endpoints.updatePostById)
                .header("Authorization", "Bearer " + token)
                .pathParam("id", createdPostForUpdate.jsonPath().getString("id"))
                .multiPart("title", newTitle)
                .multiPart("text", newDescription)
                .when()
                .patch();

        softAssert.assertEquals(response.statusCode(), 200, Messages.incorrectStatusCode);
        softAssert.assertEquals(response.jsonPath().getString("id"), createdPostForUpdate.jsonPath().getString("id"), Messages.idMismatched);
        softAssert.assertEquals(response.jsonPath().getString("title"), newTitle, Messages.titleNotMismatched);
        softAssert.assertEquals(response.jsonPath().getString("text"), newDescription, Messages.descriptionMismatched);

        softAssert.assertAll();

        methods.deletePostAfterUsing(createdPostForUpdate.jsonPath().getString("id"), token);
    }

    @Test(testName = "Ошибка при изменении названия и описания новости пустыми значениями")
    @Feature("Работа с новостями")
    public void givenInvalidPostIdAndNewData_whenUpdatePost_thenReturnedBadRequest() {
        Response login = methods.getDefaultUserInfo();
        String token = login.jsonPath().getString("accessToken");
        SoftAssert softAssert = new SoftAssert();

        Response createdPostForUpdate = methods.createPostBeforeUsing(title, description, tags, image, token);

        Response response = RestAssured
                .given()
                .baseUri(endpoints.baseUrl)
                .basePath(endpoints.updatePostById)
                .header("Authorization", "Bearer " + token)
                .pathParam("id", createdPostForUpdate.jsonPath().getString("id"))
                .multiPart("title", "")
                .multiPart("text", "")
                .when()
                .patch();

        softAssert.assertEquals(response.statusCode(), 400, Messages.incorrectStatusCode);
        softAssert.assertTrue(response.jsonPath().getList("message").contains(Messages.textShouldNotBeEmpty), Messages.textNotEmpty);
        softAssert.assertTrue(response.jsonPath().getList("message").contains(Messages.titleShouldNotBeEmpty), Messages.titleNotEmpty);
        softAssert.assertEquals(response.jsonPath().getString("error"), Messages.badRequest, Messages.requestIsCorrect);

        softAssert.assertAll();

        methods.deletePostAfterUsing(createdPostForUpdate.jsonPath().getString("id"), token);
    }
}
