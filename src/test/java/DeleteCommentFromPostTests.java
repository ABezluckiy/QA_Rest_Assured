import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Messages;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class DeleteCommentFromPostTests extends BaseTestForPostAndComments{
    @Test(testName = "Успешное удаление комментария к посту")
    @Feature("Работа с комментариями")
    public void givenValidCommentId_whenDeleteCommentFromPost_thenReturnOK() {
        SoftAssert softAssert = new SoftAssert();

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
    }

    @Test(testName = "Ошибка при удалении комментария без авторизации")
    @Feature("Работа с комментариями")
    public void givenInvalidToken_whenDeleteCommentFromPost_thenReturnUnauthorized() {
        SoftAssert softAssert = new SoftAssert();

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
    }

    @Test(testName = "Ошибка при удалении комментария по невалидному id")
    @Feature("Работа с комментариями")
    public void givenInvalidCommentIdDatatype_whenDeleteCommentFromPost_thenReturnBadRequest() {
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
    }

    @Test(testName = "Ошибка при удалении комментария по невалидному id")
    @Feature("Работа с комментариями")
    public void givenNotExistsCommentIdDatatype_whenDeleteCommentFromPost_thenReturnNotFound() {
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
    }
}
