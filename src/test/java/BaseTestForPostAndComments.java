import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Constants;
import org.example.Endpoints;
import org.example.Methods;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.util.List;

public class BaseTestForPostAndComments {
    protected final Methods methods = new Methods();
    protected final Endpoints endpoints = new Endpoints();
    protected final Response userInfo = methods.getDefaultUserInfo();
    protected final String token = userInfo.jsonPath().getString("accessToken");
    protected String postId;
    protected String commentId;
    private final String userId = userInfo.jsonPath().getString("user.id");
    private final String comment = Constants.postComment;

    @AfterMethod
    protected void afterMethod() {
        List<Object> newsId = RestAssured
                .given()
                    .baseUri(endpoints.baseUrl)
                    .basePath(endpoints.getPosts)
                    .queryParam("authorId", userId)
                    .when()
                .get()
                .then()
                    .extract()
                    .jsonPath()
                    .getList("posts.id");

        for (Object id : newsId) {
            RestAssured
                    .given()
                    .baseUri(endpoints.baseUrl)
                    .basePath(endpoints.deletePostById)
                    .pathParam("id", id.toString())
                    .header("Authorization", "Bearer " + token)
                    .when()
                    .delete();
        }
    }

    @BeforeMethod
    protected void beforeMethod() {
        Response createdPost = RestAssured
                .given()
                    .baseUri(endpoints.baseUrl)
                    .basePath(endpoints.createPost)
                    .header("Authorization", "Bearer " + token)
                    .multiPart("title", Constants.postName)
                    .multiPart("text", Constants.postDescription)
                    .multiPart("tags", Constants.postTags[0])
                    .multiPart("tags", Constants.postTags[1])
                    .multiPart("file", new File(Constants.postImage), "image/png")
                .when()
                .post();

        postId = createdPost.jsonPath().getString("id");

        JSONObject requestBody = new JSONObject();
        requestBody.put("postId", postId);
        requestBody.put("text", comment);

        commentId = RestAssured
                .given()
                .baseUri(endpoints.baseUrl)
                .basePath(endpoints.createComment)
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .when()
                .post()
                .jsonPath().getString("id");
    }
}
