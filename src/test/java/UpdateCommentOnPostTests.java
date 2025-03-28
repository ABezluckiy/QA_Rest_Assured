import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Constants;
import org.example.Messages;
import org.json.JSONObject;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class UpdateCommentOnPostTests extends BaseTestForPostAndComments{
    private final String newComment = methods.getUniqueString();

    @Test(testName = "Успешное изменение комментария к посту")
    @Feature("Работа с комментариями")
    public void givenValidComment_whenUpdateCommentOnPost_thenReturnNewCommentInfo() {
        SoftAssert softAssert = new SoftAssert();
        JSONObject requestBody = new JSONObject();
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

        softAssert.assertAll();
    }

    @Test(testName = "Ошибка при изменении комментария к посту на пустое значение")
    @Feature("Работа с комментариями")
    public void givenEmptyComment_whenUpdateCommentOnPost_thenReturnBadRequest() {
        SoftAssert softAssert = new SoftAssert();
        JSONObject requestBody = new JSONObject();
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
    }

    @Test(testName = "Ошибка при изменении несуществующего комментария")
    @Feature("Работа с комментариями")
    public void givenNotExistsCommentId_whenUpdateCommentOnPost_thenReturnNotFound() {
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
    public void givenInvalidCommentId_whenUpdateCommentOnPost_thenReturnBadRequest() {
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

    @Test(testName = "Ошибка при изменении комментария без авторизации")
    @Feature("Работа с комментариями")
    public void givenEmptyToken_whenUpdateCommentOnPost_thenReturnUnauthorized() {
        SoftAssert softAssert = new SoftAssert();
        JSONObject requestBody = new JSONObject();
        requestBody.put("text", newComment);

        Response response = RestAssured
                .given()
                    .baseUri(endpoints.baseUrl)
                    .basePath(endpoints.updateCommentById)
                    .header("Content-Type", "application/json")
                    .pathParam("id", commentId)
                    .body(requestBody.toString())
                .when()
                .patch();

        softAssert.assertEquals(response.statusCode(), 401, Messages.incorrectStatusCode);
        softAssert.assertEquals(response.jsonPath().getString("message"), Messages.unauthorized, Messages.userAuthorized);
        softAssert.assertAll();
    }
}
