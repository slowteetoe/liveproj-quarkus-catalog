package io.chillplus;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import io.chillplus.api.TvShow;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestHTTPEndpoint(TvShowResource.class) // this sets the RestAssured base path, neat.
public class TvShowResourceTest {

  // clear out data each time
  @BeforeEach
  public void beforeEach() {
    given()
        .when()
        .delete("")
        .then()
        .statusCode(204);
  }

  @Test
  public void shouldListShows() {
    given()
        .when()
        .get("")
        .then()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("$.size()", is(0));

    TvShow tvShow = new TvShow(null, "This is a show", "Comedy");

    given()
        .body(tvShow)
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .when()
        .post("")
        .then()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("title", is(tvShow.getTitle()));

    tvShow = new TvShow(null, "A different kind of show", "Drama");

    given()
        .body(tvShow)
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .when()
        .post("")
        .then()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("title", is(tvShow.getTitle()));

    List<TvShow> actual = given()
        .when().get()
        .then()
        .extract()
        .body().jsonPath().getList(".", TvShow.class);

    assertThat(actual).hasSize(2);
    assertThat(actual).extracting("title")
        .containsOnly("This is a show", "A different kind of show");
  }

  @Test
  public void shouldReturnEmptyListWhenNoShowsExist() {
    given()
        .when().get()
        .then()
        .statusCode(200)
        .body(is("[]"));
  }

  @Test
  public void shouldRetrieveTvShow() {
    TvShow tvShow = new TvShow(null, "This is a show", "Comedy");

    TvShow actual = given()
        .body(tvShow)
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .when()
        .post()
        .then()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .extract().body().as(TvShow.class);

    assertThat(actual.getId()).isNotNull();

    given().contentType(ContentType.JSON)
        .when()
        .get("" + actual.getId())
        .then()
        .statusCode(200)
        .body("title", is(actual.getTitle()));
  }

  @Test
  public void shouldHandleNonexistentTvShow() {
    given().contentType(ContentType.JSON)
        .when()
        .get("123456789") // this show does not exist
        .then()
        .statusCode(404);
  }

  @Test
  public void shouldNotCreateTvShowsIfMissingTitle() {
    // TODO the default validation error JSON structure is horrible, figure something out
    given()
        .contentType(ContentType.JSON)
        .body("{ \"title\": \"\" }")
        .when()
        .post()
        .then()
        .statusCode(400)
        .body("parameterViolations[0].message", equalTo("Title may not be blank"));
  }

  @Test
  public void shouldNotCreateTvShowIfIdProvided() {
    given()
        .contentType(ContentType.JSON)
        .body("{ \"title\": \"do not create\", \"id\": 12345 }")
        .when()
        .post()
        .then()
        .statusCode(400)
        .body("parameterViolations[0].message",
            equalTo("ID will be auto-assigned, you may not provide one"));
  }

  @Test
  public void shouldDeleteAllTvShows() {

    TvShow tvShow = new TvShow(null, "This is a show", "Comedy");

    given()
        .body(tvShow)
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .when()
        .post()
        .then()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("title", is(tvShow.getTitle()));

    given()
        .when()
        .get()
        .then()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("$.size()", is(1));

    given().contentType(ContentType.JSON)
        .when()
        .delete()
        .then()
        .statusCode(204);

    given()
        .when()
        .get()
        .then()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("$.size()", is(0));
  }

  @Test
  public void shouldDeleteIndividualTvShow() {
    TvShow actual = given()
        .body(new TvShow(null, "This is a show", "Comedy"))
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .when()
        .post()
        .then()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .extract().body().as(TvShow.class);

    given()
        .when()
        .get("")
        .then()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("$.size()", is(1));

    given().contentType(ContentType.JSON)
        .when()
        .delete("" + actual.getId())
        .then()
        .statusCode(204);

    given().contentType(ContentType.JSON)
        .when()
        .get("" + actual.getId()) // this show does not exist
        .then()
        .statusCode(404);
  }


  @Test
  public void shouldFindTvShowByTitle() {
    given()
        .body(new TvShow(null, "Special Title", null))
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .when()
        .post()
        .then()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("title", is("Special Title"));

    given()
        .when()
        .get("/search/{title}", "Special Title")
        .then()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("title", is("Special Title"));
  }

  @Test
  public void shouldListTvShowsByCategory() {
    for (int i = 0; i < 100; i++) {
      String category = i % 2 == 0 ? "Comedy" : "Drama";
      String title = "Show Number " + i;

      given()
          .body(new TvShow(null, title, category))
          .contentType(ContentType.JSON)
          .accept(ContentType.JSON)
          .when()
          .post()
          .then()
          .statusCode(200)
          .contentType(ContentType.JSON)
          .body("title", is(title));
    }

    int pageIndex = 2;
    int pageSize = 20;
    List<TvShow> result = given()
        .when()
        .get("/categories/{category}?page={pageIndex}&size={pageSize}", "Comedy", pageIndex,
            pageSize)
        .then()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .extract().jsonPath().getList(".", TvShow.class);

    // the page index is apparently zero-based, so the 3rd page should have only 10 results since
    // there are only 50 entries total
    assertThat(result).hasSize(10);
    assertThat(result).extracting("category").containsOnly("Comedy");
  }
}
