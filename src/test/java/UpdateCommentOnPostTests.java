import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Constants;
import org.example.Endpoints;
import org.example.Messages;
import org.example.Methods;
import org.json.JSONObject;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class UpdateCommentOnPostTests {
    private final Methods methods = new Methods();
    private final Endpoints endpoints = new Endpoints();
    private final String newComment = methods.getUniqueString();

    @Test(testName = "Успешное изменение комментария к посту")
    @Feature("Работа с комментариями")
    public void givenValidComment_whenUpdateCommentOnPost_thenReturnedNewCommentInfo() {
        Response login = methods.getDefaultUserInfo();
        String token = login.jsonPath().getString("accessToken");
        String userId = login.jsonPath().getString("user.id");
        SoftAssert softAssert = new SoftAssert();
        JSONObject requestBody = new JSONObject();
        String commentId = methods.getCommentId();
        requestBody.put("text", newComment);

        Response response = RestAssured
                .given()
                    .baseUri(endpoints.baseUrl)
                    .basePath(endpoints.updateCommentById)
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .pathParam("id", commentId)
                    .body(requestBody.toString())
                .when()
                .patch();

        softAssert.assertEquals(response.statusCode(), 200, Messages.incorrectStatusCode);
        softAssert.assertEquals(response.jsonPath().getString("id"), commentId, Messages.idMismatched);
        softAssert.assertEquals(response.jsonPath().getString("text"), newComment, Messages.commentTextMismatched);
        softAssert.assertEquals(response.jsonPath().getString("authorId"), userId, Messages.idMismatched);

        softAssert.assertAll();
        methods.deletePostAfterUsing();
    }

    @Test(testName = "Ошибка при изменении комментария к посту на пустое значение")
    @Feature("Работа с комментариями")
    public void givenEmptyComment_whenUpdateCommentOnPost_thenReturnedBadRequest() {
        Response login = methods.getDefaultUserInfo();
        String token = login.jsonPath().getString("accessToken");
        SoftAssert softAssert = new SoftAssert();
        JSONObject requestBody = new JSONObject();
        String commentId = methods.getCommentId();
        requestBody.put("text", "");

        Response response = RestAssured
                .given()
                .baseUri(endpoints.baseUrl)
                .basePath(endpoints.updateCommentById)
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .pathParam("id", commentId)
                .body(requestBody.toString())
                .when()
                .patch();

        softAssert.assertEquals(response.statusCode(), 400, Messages.incorrectStatusCode);
        softAssert.assertTrue(response.jsonPath().getList("message").contains(Messages.textShouldNotBeEmpty), Messages.textNotEmpty);
        softAssert.assertEquals(response.jsonPath().getString("error"), Messages.badRequest, Messages.requestIsCorrect);

        softAssert.assertAll();
        methods.deletePostAfterUsing();
    }

    @Test(testName = "Ошибка при изменении несуществующего комментария")
    @Feature("Работа с комментариями")
    public void givenNotExistsCommentId_whenUpdateCommentOnPost_thenReturnedNotFound() {
        Response login = methods.getDefaultUserInfo();
        String token = login.jsonPath().getString("accessToken");
        SoftAssert softAssert = new SoftAssert();
        JSONObject requestBody = new JSONObject();
        requestBody.put("text", Constants.postComment);

        Response response = RestAssured
                .given()
                .baseUri(endpoints.baseUrl)
                .basePath(endpoints.updateCommentById)
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .pathParam("id", -1)
                .body(requestBody.toString())
                .when()
                .patch();

        softAssert.assertEquals(response.statusCode(), 404, Messages.incorrectStatusCode);
        softAssert.assertEquals(response.jsonPath().getString("message"), Messages.notFound, Messages.postWasFound);
        softAssert.assertAll();
    }

    @Test(testName = "Ошибка при изменении комментария по некорректному типу айди")
    @Feature("Работа с комментариями")
    public void givenInvalidCommentId_whenUpdateCommentOnPost_thenReturnedBadRequest() {
        Response login = methods.getDefaultUserInfo();
        String token = login.jsonPath().getString("accessToken");
        SoftAssert softAssert = new SoftAssert();
        JSONObject requestBody = new JSONObject();
        requestBody.put("text", Constants.postComment);

        Response response = RestAssured
                .given()
                .baseUri(endpoints.baseUrl)
                .basePath(endpoints.updateCommentById)
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .pathParam("id", "comment")
                .body(requestBody.toString())
                .when()
                .patch();

        softAssert.assertEquals(response.statusCode(), 400, Messages.incorrectStatusCode);
        softAssert.assertEquals(response.jsonPath().getString("message"), Messages.validationFailed, Messages.paramIsValid);
        softAssert.assertEquals(response.jsonPath().getString("error"), Messages.badRequest, Messages.requestIsCorrect);
        softAssert.assertAll();
    }
}
