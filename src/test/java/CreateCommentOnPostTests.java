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

public class CreateCommentOnPostTests {
    private final Methods methods = new Methods();
    private final Endpoints endpoints = new Endpoints();
    private final String title = Constants.postName;
    private final String description = Constants.postDescription;
    private final String image = Constants.postImage;
    private final String[] tags = Constants.postTags;
    private final String comment = Constants.postComment;

    @Test(testName = "Успешное создание комментария к посту")
    @Feature("Работа с комментариями")
    public void givenValidComment_whenCreateCommentOnPost_thenReturnedCommentInfo() {
        Response login = methods.getDefaultUserInfo();
        String token = login.jsonPath().getString("accessToken");
        String userId = login.jsonPath().getString("user.id");
        SoftAssert softAssert = new SoftAssert();
        JSONObject requestBody = new JSONObject();
        Response createPostForAddComment = methods.createPostBeforeUsing(title, description, tags, image, token);
        String postId = createPostForAddComment.jsonPath().getString("id");
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
        softAssert.assertEquals(response.jsonPath().getString("authorId"), userId, Messages.authorMismatched);
        softAssert.assertEquals(response.jsonPath().getString("postId"), postId, Messages.idMismatched);

        softAssert.assertAll();

        methods.deletePostAfterUsing(postId, token);
    }

    @Test(testName = "Ошибка при создании пустого комментария к посту")
    @Feature("Работа с комментариями")
    public void givenEmptyComment_whenCreateCommentOnPost_thenReturnedBadRequest() {
        Response login = methods.getDefaultUserInfo();
        String token = login.jsonPath().getString("accessToken");
        SoftAssert softAssert = new SoftAssert();
        JSONObject requestBody = new JSONObject();
        Response createPostForAddComment = methods.createPostBeforeUsing(title, description, tags, image, token);
        String postId = createPostForAddComment.jsonPath().getString("id");
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

        methods.deletePostAfterUsing(postId, token);
    }

    @Test(testName = "Ошибка создания комментария к посту с несуществующим id")
    @Feature("Работа с комментариями")
    public void givenNotExistsPostId_whenCreateCommentOnPost_thenReturnedBadRequest() {
        Response login = methods.getDefaultUserInfo();
        String token = login.jsonPath().getString("accessToken");
        SoftAssert softAssert = new SoftAssert();
        JSONObject requestBody = new JSONObject();
        Response createPostForAddComment = methods.createPostBeforeUsing(title, description, tags, image, token);
        String postId = createPostForAddComment.jsonPath().getString("id");
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

        methods.deletePostAfterUsing(postId, token);
    }

    @Test(testName = "Ошибка создания комментария к посту с невалидным id")
    @Feature("Работа с комментариями")
    public void givenInvalidDataPostId_whenCreateCommentOnPost_thenReturnedBadRequest() {
        Response login = methods.getDefaultUserInfo();
        String token = login.jsonPath().getString("accessToken");
        SoftAssert softAssert = new SoftAssert();
        JSONObject requestBody = new JSONObject();
        Response createPostForAddComment = methods.createPostBeforeUsing(title, description, tags, image, token);
        String postId = createPostForAddComment.jsonPath().getString("id");
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

        methods.deletePostAfterUsing(postId, token);
    }
}
