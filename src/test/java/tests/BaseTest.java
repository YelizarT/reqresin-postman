package tests;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class BaseTest {
    final static String BASE_URI = "https://swapi.dev/api";
    static RequestSpecification specification = new RequestSpecBuilder()
            .setBaseUri(BASE_URI)
            .setContentType(ContentType.JSON).build();

    public static Response getRequest(String endPoint, Integer expectedStatusCode) {
        Response response = given()
                .spec(specification)
                .when().log().all()
                .get(endPoint)
                .then().log().all()
                .statusCode(expectedStatusCode)
                .extract().response();
        return response;
    }
}
