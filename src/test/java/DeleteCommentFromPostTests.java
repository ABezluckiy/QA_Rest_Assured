import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Endpoints;
import org.example.Messages;
import org.example.Methods;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class DeleteCommentFromPostTests {
    private final Methods methods = new Methods();
    private final Endpoints endpoints = new Endpoints();

    @Test(testName = "Успешное удаление комментария к посту")
    @Feature("Работа с комментариями")
    public void givenValidCommentId_whenDeleteCommentFromPost_thenReturnOK() {
        Response login = methods.getDefaultUserInfo();
        String token = login.jsonPath().getString("accessToken");
        SoftAssert softAssert = new SoftAssert();
        String commentId = methods.getCommentId();

        Response response = RestAssured
                .given()
                    .baseUri(endpoints.baseUrl)
                    .basePath(endpoints.deleteCommentById)
                    .pathParam("id", commentId)
                    .header("Authorization", "Bearer " + token)
                .when()
                .delete();

        softAssert.assertEquals(response.statusCode(), 200, Messages.incorrectStatusCode);
        softAssert.assertAll();

        methods.deletePostAfterUsing();
    }

    @Test(testName = "Ошибка при удалении комментария без авторизации")
    @Feature("Работа с комментариями")
    public void givenInvalidToken_whenDeleteCommentFromPost_thenReturnUnauthorized() {
        SoftAssert softAssert = new SoftAssert();
        String commentId = methods.getCommentId();

        Response response = RestAssured
                .given()
                .baseUri(endpoints.baseUrl)
                .basePath(endpoints.deleteCommentById)
                .pathParam("id", commentId)
                .header("Authorization", "")
                .when()
                .delete();

        softAssert.assertEquals(response.statusCode(), 401, Messages.incorrectStatusCode);
        softAssert.assertEquals(response.jsonPath().getString("message"), Messages.unauthorized, Messages.userAuthorized);
        softAssert.assertAll();

        methods.deletePostAfterUsing();
    }

    @Test(testName = "Ошибка при удалении комментария по невалидному id")
    @Feature("Работа с комментариями")
    public void givenInvalidCommentIdDatatype_whenDeleteCommentFromPost_thenReturnBadRequest() {
        Response login = methods.getDefaultUserInfo();
        String token = login.jsonPath().getString("accessToken");
        SoftAssert softAssert = new SoftAssert();

        Response response = RestAssured
                .given()
                .baseUri(endpoints.baseUrl)
                .basePath(endpoints.deleteCommentById)
                .pathParam("id", "id")
                .header("Authorization", "Bearer " + token)
                .when()
                .delete();

        softAssert.assertEquals(response.statusCode(), 400, Messages.incorrectStatusCode);
        softAssert.assertEquals(response.jsonPath().getString("message"), Messages.validationFailed, Messages.paramIsValid);
        softAssert.assertEquals(response.jsonPath().getString("error"), Messages.badRequest, Messages.requestIsCorrect);
        softAssert.assertAll();

        methods.deletePostAfterUsing();
    }

    @Test(testName = "Ошибка при удалении комментария по невалидному id")
    @Feature("Работа с комментариями")
    public void givenNotExistsCommentIdDatatype_whenDeleteCommentFromPost_thenReturnNotFound() {
        Response login = methods.getDefaultUserInfo();
        String token = login.jsonPath().getString("accessToken");
        SoftAssert softAssert = new SoftAssert();

        Response response = RestAssured
                .given()
                .baseUri(endpoints.baseUrl)
                .basePath(endpoints.deleteCommentById)
                .pathParam("id", -1)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete();
        softAssert.assertEquals(response.statusCode(), 404, Messages.incorrectStatusCode);
        softAssert.assertEquals(response.jsonPath().getString("message"), Messages.notFound, Messages.commentWasFound);
        softAssert.assertAll();

        methods.deletePostAfterUsing();
    }
}
