package io.chillplus;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import io.chillplus.api.TvShow;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

@QuarkusTest
@TestHTTPEndpoint(TvShowResource.class) // this sets the RestAssured base path, neat.
public class TvShowResourceTest {

  @InjectMock
  TvShowService tvShowService;

  @Test
  public void shouldListShows() {
    Mockito.when(tvShowService.getAll())
        .thenReturn(
            Arrays.asList(
                new TvShow(123L, "This is a show", null),
                new TvShow(456L, "A different kind of show", null)));

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
    Mockito.when(tvShowService.getAll()).thenReturn(Collections.emptyList());
    given()
        .when().get()
        .then()
        .statusCode(200)
        .body(is("[]"));
  }

  @Test
  public void shouldRetrieveTvShow() {
    Mockito.when(tvShowService.findById(98765L)).thenReturn(
        new TvShow(98765L, "A great show", null));

    given().contentType(ContentType.JSON)
        .when()
        .get("98765")
        .then()
        .statusCode(200)
        .body("title", is("A great show"));
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
  public void shouldCreateValidTvShow() {
    // make this larger than an int so that we're sure un-/marshaling works
    doAnswer((invocation) -> {
      TvShow arg = invocation.getArgument(0);
      arg.setId((long) Integer.MAX_VALUE + 1);
      return arg;
    }).when(tvShowService).create(any());

    given().contentType(ContentType.JSON)
        .body("{ \"title\": \"my_tvShow\" }")
        .when()
        .post()
        .then()
        .statusCode(200)
        .body("id", equalTo(2147483648L))
        .body("title", equalTo("my_tvShow"));
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
    given().contentType(ContentType.JSON)
        .when()
        .delete()
        .then()
        .statusCode(204);

    verify(tvShowService).deleteAll();
  }

  @Test
  public void shouldDeleteIndividualTvShow() {
    ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
    doNothing().when(tvShowService).deleteById(captor.capture());

    given().contentType(ContentType.JSON)
        .when()
        .delete("123")
        .then()
        .statusCode(204);

    assertThat(captor.getValue()).isEqualTo(123L);
  }

}
