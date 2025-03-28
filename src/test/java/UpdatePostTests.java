import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Messages;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class UpdatePostTests extends BaseTestForPostAndComments{
    private final String newTitle = methods.getUniqueString();
    private final String newDescription = methods.getUniqueString();

    @Test(testName = "Изменение названия и описания новости по уникальному идентификатору")
    @Feature("Работа с новостями")
    public void givenValidPostIdAndNewData_whenUpdatePost_thenReturnedPostInfo() {
        SoftAssert softAssert = new SoftAssert();

        Response response = RestAssured
                .given()
                .baseUri(endpoints.baseUrl)
                .basePath(endpoints.updatePostById)
                .header("Authorization", "Bearer " + token)
                .pathParam("id", postId)
                .multiPart("title", newTitle)
                .multiPart("text", newDescription)
                .when()
                .patch();

        softAssert.assertEquals(response.statusCode(), 200, Messages.incorrectStatusCode);
        softAssert.assertEquals(response.jsonPath().getString("id"), postId, Messages.idMismatched);
        softAssert.assertEquals(response.jsonPath().getString("title"), newTitle, Messages.titleNotMismatched);
        softAssert.assertEquals(response.jsonPath().getString("text"), newDescription, Messages.descriptionMismatched);

        softAssert.assertAll();
    }

    @Test(testName = "Ошибка при изменении названия и описания новости пустыми значениями")
    @Feature("Работа с новостями")
    public void givenInvalidNewData_whenUpdatePost_thenReturnedBadRequest() {
        SoftAssert softAssert = new SoftAssert();

        Response response = RestAssured
                .given()
                .baseUri(endpoints.baseUrl)
                .basePath(endpoints.updatePostById)
                .header("Authorization", "Bearer " + token)
                .pathParam("id", postId)
                .multiPart("title", "")
                .multiPart("text", "")
                .when()
                .patch();

        softAssert.assertEquals(response.statusCode(), 400, Messages.incorrectStatusCode);
        softAssert.assertTrue(response.jsonPath().getList("message").contains(Messages.textShouldNotBeEmpty), Messages.textNotEmpty);
        softAssert.assertTrue(response.jsonPath().getList("message").contains(Messages.titleShouldNotBeEmpty), Messages.titleNotEmpty);
        softAssert.assertEquals(response.jsonPath().getString("error"), Messages.badRequest, Messages.requestIsCorrect);

        softAssert.assertAll();
    }
}
