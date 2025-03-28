import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Constants;
import org.example.Messages;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import java.io.File;

public class CreatePostTests extends BaseTestForPostAndComments{
    private final String title = Constants.postName;
    private final String description = Constants.postDescription;
    private final String image = Constants.postImage;
    private final String[] tags = Constants.postTags;

    @Test(testName = "Успешное создание новости с валидными данными")
    @Feature("Работа с новостями")
    public void givenValidPostData_whenCreatePost_thenPostShouldCreate() {
        SoftAssert softAssert = new SoftAssert();

        Response response = RestAssured
                .given()
                    .baseUri(endpoints.baseUrl)
                    .basePath(endpoints.createPost)
                    .header("Authorization", "Bearer " + token)
                    .multiPart("title", title)
                    .multiPart("text", description)
                    .multiPart("tags", tags[0])
                    .multiPart("tags", tags[1])
                    .multiPart("file", new File(image), "image/png")
                .when()
                .post();

        softAssert.assertEquals(response.statusCode(), 201, Messages.incorrectStatusCode);
        softAssert.assertNotNull(response.jsonPath().getString("id"), Messages.postIdIsEmpty);
        softAssert.assertEquals(response.jsonPath().getString("title"), title, Messages.titleNotMismatched);
        softAssert.assertEquals(response.jsonPath().getString("text"), description, Messages.descriptionMismatched);
        softAssert.assertNotNull(response.jsonPath().getList("tags"), Messages.tagsNotMismatched);

        softAssert.assertAll();
    }

    @Test(testName = "Ошибка при создании новости без авторизации")
    @Feature("Работа с новостями")
    public void givenEmptyToken_whenCreatePost_thenReturnUnauthorized() {
        SoftAssert softAssert = new SoftAssert();

        Response response = RestAssured
                .given()
                    .baseUri(endpoints.baseUrl)
                    .basePath(endpoints.createPost)
                    .header("Authorization", "")
                    .multiPart("title", title)
                    .multiPart("text", description)
                    .multiPart("tags", tags[0])
                    .multiPart("tags", tags[1])
                    .multiPart("file", new File(image), "image/png")
                .when()
                .post();

        softAssert.assertEquals(response.statusCode(), 401, Messages.incorrectStatusCode);
        softAssert.assertEquals(response.jsonPath().getString("message"), Messages.unauthorized, Messages.userAuthorized);

        softAssert.assertAll();
    }

    @Test(testName = "Ошибка при создании новости с пустыми значениями")
    @Feature("Работа с новостями")
    public void givenEmptyPostData_whenCreatePost_thenReturnValidationErrors() {
        SoftAssert softAssert = new SoftAssert();

        Response response = RestAssured
                .given()
                    .baseUri(endpoints.baseUrl)
                    .basePath(endpoints.createPost)
                    .header("Authorization", "Bearer " + token)
                    .multiPart("title", "")
                    .multiPart("text", "")
                    .multiPart("tags", "")
                    .multiPart("file", "")
                .when()
                .post();

        softAssert.assertEquals(response.statusCode(), 400, Messages.incorrectStatusCode);
        softAssert.assertTrue(response.jsonPath().getList("message").contains(Messages.textShouldNotBeEmpty), Messages.textNotEmpty);
        softAssert.assertTrue(response.jsonPath().getList("message").contains(Messages.titleShouldNotBeEmpty), Messages.titleNotEmpty);
        softAssert.assertEquals(response.jsonPath().getString("error"), Messages.badRequest, Messages.requestIsCorrect);

        softAssert.assertAll();
    }
}
