import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Constants;
import org.example.Messages;
import org.json.JSONObject;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class CreateCommentOnPostTests extends BaseTestForPostAndComments{
    private final String comment = Constants.postComment;

    @Test(testName = "Успешное создание комментария к посту")
    @Feature("Работа с комментариями")
    public void givenValidComment_whenCreateCommentOnPost_thenReturnedCommentInfo() {
        SoftAssert softAssert = new SoftAssert();
        JSONObject requestBody = new JSONObject();
        requestBody.put("postId", postId);
        requestBody.put("text", comment);

        Response response = RestAssured
                .given()
                    .baseUri(endpoints.baseUrl)
                    .basePath(endpoints.createComment)
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .body(requestBody.toString())
                .when()
                .post();

        softAssert.assertEquals(response.statusCode(), 201, Messages.incorrectStatusCode);
        softAssert.assertNotNull(response.jsonPath().getString("id"), Messages.commentIdMustBePresent);
        softAssert.assertEquals(response.jsonPath().getString("text"), comment);
        softAssert.assertEquals(response.jsonPath().getString("postId"), postId, Messages.idMismatched);

        softAssert.assertAll();
    }

    @Test(testName = "Ошибка при создании пустого комментария к посту")
    @Feature("Работа с комментариями")
    public void givenEmptyComment_whenCreateCommentOnPost_thenReturnedBadRequest() {
        SoftAssert softAssert = new SoftAssert();
        JSONObject requestBody = new JSONObject();
        requestBody.put("postId", postId);
        requestBody.put("text", "");

        Response response = RestAssured
                .given()
                    .baseUri(endpoints.baseUrl)
                    .basePath(endpoints.createComment)
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .body(requestBody.toString())
                .when()
                .post();

        softAssert.assertEquals(response.statusCode(), 400, Messages.incorrectStatusCode);
        softAssert.assertTrue(response.jsonPath().getList("message").contains(Messages.textShouldNotBeEmpty), Messages.textNotEmpty);
        softAssert.assertEquals(response.jsonPath().getString("error"), Messages.badRequest, Messages.requestIsCorrect);

        softAssert.assertAll();
    }

    @Test(testName = "Ошибка создания комментария к посту с несуществующим id")
    @Feature("Работа с комментариями")
    public void givenNotExistsPostId_whenCreateCommentOnPost_thenReturnedBadRequest() {
        SoftAssert softAssert = new SoftAssert();
        JSONObject requestBody = new JSONObject();
        requestBody.put("postId", -1);
        requestBody.put("text", comment);

        Response response = RestAssured
                .given()
                    .baseUri(endpoints.baseUrl)
                    .basePath(endpoints.createComment)
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .body(requestBody.toString())
                .when()
                .post();

        softAssert.assertEquals(response.statusCode(), 404, Messages.incorrectStatusCode);
        softAssert.assertEquals(response.jsonPath().getString("message"), Messages.notFound, Messages.postWasFound);

        softAssert.assertAll();
    }

    @Test(testName = "Ошибка создания комментария к посту с невалидным id")
    @Feature("Работа с комментариями")
    public void givenInvalidDataPostId_whenCreateCommentOnPost_thenReturnedBadRequest() {
        SoftAssert softAssert = new SoftAssert();
        JSONObject requestBody = new JSONObject();
        requestBody.put("postId", "invalid");
        requestBody.put("text", comment);

        Response response = RestAssured
                .given()
                    .baseUri(endpoints.baseUrl)
                    .basePath(endpoints.createComment)
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .body(requestBody.toString())
                .when()
                .post();

        softAssert.assertEquals(response.statusCode(), 400, Messages.incorrectStatusCode);
        softAssert.assertTrue(response.jsonPath().getList("message").contains(Messages.postIdMustBeANumber), Messages.postIdIsNumber);
        softAssert.assertEquals(response.jsonPath().getString("error"), Messages.badRequest, Messages.requestIsCorrect);

        softAssert.assertAll();
    }

    @Test(testName = "Ошибка создания комментария без авторизации")
    @Feature("Работа с комментариями")
    public void givenInvalidToken_whenCreateCommentOnPost_thenReturnedUnauthorized() {
        SoftAssert softAssert = new SoftAssert();

        Response response = RestAssured
                .given()
                    .baseUri(endpoints.baseUrl)
                    .basePath(endpoints.createComment)
                    .header("Content-Type", "application/json")
                .when()
                .post();

        softAssert.assertEquals(response.statusCode(), 401, Messages.incorrectStatusCode);
        softAssert.assertEquals(response.jsonPath().getString("message"), Messages.unauthorized, Messages.userAuthorized);
        softAssert.assertAll();
    }
}
