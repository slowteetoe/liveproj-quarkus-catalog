package io.chillplus;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
public class TvShowResourceTest {

	@Test
	public void shouldListTvShows() {
		// should check better, timing issue whether there will be data or not
		given().when().get("/rest/tv").then().statusCode(200);
	}

	@Test
	public void shouldRequireValidDataToCreateTvShow() {
		// missing title, should really have custom exception response and assertions here
		given().contentType(ContentType.JSON).when().post("/rest/tv").then().statusCode(400);
	}

	@Test
	public void shouldCreateTvShow() {
		given().contentType(ContentType.JSON).body("{ \"title\": \"my_tvShow\" }").when().post(
				"/rest/tv").then().statusCode(200).body("id", notNullValue());
	}

	@Test
	public void shouldDeleteTvShows() {
		// unconditional delete, do we want a 200 or the (default) 204
		given().contentType(ContentType.JSON).when().delete("/rest/tv").then().statusCode(204);
	}

	@Test
	public void shouldDeleteIndividualTvShow() {
		// unconditional delete, do we want a 200 or the (default) 204
		given().contentType(ContentType.JSON).when().delete("/rest/tv/123").then().statusCode(204);
	}
}
