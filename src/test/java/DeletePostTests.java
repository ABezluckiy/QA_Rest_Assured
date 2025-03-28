import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Messages;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class DeletePostTests extends BaseTestForPostAndComments{
    @Test(testName = "Успешное удаление новости")
    @Feature("Работа с удалением новостей")
    public void givenValidPostId_whenDeletePost_thenPostDeleted() {
        SoftAssert softAssert = new SoftAssert();

        Response response = RestAssured
                .given()
                    .baseUri(endpoints.baseUrl)
                    .basePath(endpoints.deletePostById)
                    .pathParam("id", postId)
                    .header("Authorization", "Bearer " + token)
                .when()
                .delete();

        softAssert.assertEquals(response.statusCode(), 200, Messages.incorrectStatusCode);
        softAssert.assertAll();
    }

    @Test(testName = "Ошибка при удалении новости с несуществующему id")
    @Feature("Работа с удалением новостей")
    public void givenNotExistsPostId_whenDeletePost_thenReturnBadRequest() {
        SoftAssert softAssert = new SoftAssert();

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
    }

    @Test(testName = "Ошибка при удалении новости с некорректным типом данных id")
    @Feature("Работа с удалением новостей")
    public void givenInvalidPostId_whenDeletePost_thenReturnBadRequest() {
        SoftAssert softAssert = new SoftAssert();

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
    }

    @Test(testName = "Ошибка при удалении новости с некорректным типом данных id")
    @Feature("Работа с удалением новостей")
    public void givenInvalidToken_whenDeletePost_thenReturnUnauthorized() {
        SoftAssert softAssert = new SoftAssert();

        Response response = RestAssured
                .given()
                    .baseUri(endpoints.baseUrl)
                    .basePath(endpoints.deletePostById)
                    .pathParam("id", postId)
                .when()
                .delete();

        softAssert.assertEquals(response.statusCode(), 401, Messages.incorrectStatusCode);
        softAssert.assertEquals(response.jsonPath().getString("message"), Messages.unauthorized, Messages.userAuthorized);
        softAssert.assertAll();
    }
}
