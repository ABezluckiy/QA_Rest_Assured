import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Constants;
import org.example.Endpoints;
import org.example.Messages;
import org.example.Methods;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.File;

public class NegativeCreatePostTests {
    private final Endpoints endpoints = new Endpoints();
    private final Methods methods = new Methods();
    private final String title = Constants.newsName;
    private final String description = Constants.newsDescription;
    private final String image = Constants.newsImage;
    private final String[] tags = Constants.newsTags;

    @Test(testName = "Ошибка при создании новости без авторизации")
    @Feature("Работа с новостями")
    public void givenEmptyToken_whenCreateNews_thenReturnAuthorizedError() {
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
    public void givenEmptyNewsData_whenCreateNews_thenReturnValidationErrors() {
        Response login = methods.getDefaultUserInfo();
        SoftAssert softAssert = new SoftAssert();
        String token = login.jsonPath().getString("accessToken");

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
