package io.chillplus;

import io.chillplus.api.TvShow;
import java.util.List;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.groups.ConvertGroup;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/rest/tv")
@Consumes("application/json")
@Produces("application/json")
public class TvShowResource {

  private static final Logger log = LoggerFactory.getLogger(TvShowResource.class);

  @Inject
  TvShowService tvShowService;

  @GET
  public List<TvShow> getAll() {
    return tvShowService.getAll();
  }

  @GET
  @Path("/{id}")
  public TvShow getOneById(@PathParam("id") Long id) {
    TvShow result = tvShowService.findById(id);
    if (result == null) {
      throw new NotFoundException("Unknown tv show, id=" + id);
    }
    return result;
  }

  @Transactional
  @POST
  @Path("")
  public TvShow create(
      @Valid @NotNull @ConvertGroup(to = ValidationGroups.Post.class) TvShow tvShow) {
    return tvShowService.create(tvShow);
  }

  @Transactional
  @DELETE
  public void deleteAll() {
    log.warn("Deleting ALL the data!!");
    tvShowService.deleteAll();
  }

  @Transactional
  @DELETE
  @Path("/{id}")
  public void deleteOne(@PathParam("id") Long id) {
    log.info("Deleting tv show id={}", id);
    tvShowService.deleteById(id);
  }

  @GET
  @Path("/search/{title}")
  public TvShow getOneByTitle(@PathParam("title") String title) {
    TvShow entity = TvShow.findByTitle(title);
    if (entity == null) {
      throw new NotFoundException("Entity does not exist.");
    }
    return entity;
  }

  @GET
  @Path("/categories/{category}")
  public List<TvShow> getAllByCategory(
      @PathParam("category") String category,
      @DefaultValue("1") @QueryParam(value = "page") int pageIndex,
      @DefaultValue("10") @QueryParam(value = "size") int pageSize) {
    return TvShow.findByCategoryIgnoreCase(category, pageIndex, pageSize);
  }
}
