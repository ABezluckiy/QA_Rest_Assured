import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Constants;
import org.example.Endpoints;
import org.example.Methods;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;

public class BaseTestForUser {
    protected final Methods methods = new Methods();
    protected final Endpoints endpoints = new Endpoints();
    private final Response userInfo = methods.getDefaultUserInfo();
    protected final String token = userInfo.jsonPath().getString("accessToken");
    protected final String userId = userInfo.jsonPath().getString("user.id");
    protected String newFirstName = methods.getUniqueString();
    protected String newLastName = methods.getUniqueString();

    @AfterMethod
    protected void returnUserData() {
        JSONObject returnUserData = new JSONObject();
        returnUserData.put("firstName", Constants.correctUserFirstname);
        returnUserData.put("lastName", Constants.correctUserLastname);
        RestAssured
                .given()
                    .baseUri(endpoints.baseUrl)
                    .basePath(endpoints.updateUserInfoById)
                    .pathParam("id", userId)
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .body(returnUserData.toString())
                .when()
                .patch();
    }
}
