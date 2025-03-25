import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.example.Endpoints;
import org.example.Methods;
import org.json.JSONObject;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class UserTests {
    private final SoftAssert assertions = new SoftAssert();
    private final Endpoints endpoints = new Endpoints();
    private final JSONObject requestBody = new JSONObject();
    private final RequestSpecification baseUrl = RestAssured.given().baseUri(endpoints.baseUrl);

    @Test
    @Feature("Работа с данными пользователя")
    @Description("Получение данных пользователя из токена")
    public void givenValidToken_whenGetUserInfo_thenUserInfoReturned() {
        String token = Methods.getTokenByDefaultUser();

        Response response = baseUrl
                .basePath(endpoints.getCurrentUserInfoByToken)
                .header("Authorization", "Bearer" + token)
                .when()
                .get();
    }
}
