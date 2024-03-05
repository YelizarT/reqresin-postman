package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Vader {
    @Test
    public void testCharacterNameContainsVader() {
        Response response = RestAssured.given()
                .baseUri("https://swapi.dev/api/people/")
                .param("search", "Vader")
                .get();
        response.then().assertThat().statusCode(200);
        assertTrue(response.body().asString().contains("Vader"));
    }
}
