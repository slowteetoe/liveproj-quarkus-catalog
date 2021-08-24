package io.chillplus;

import io.chillplus.api.TvShow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.groups.ConvertGroup;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;

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

	@POST
	@Path("")
	public TvShow create(@Valid @NotNull @ConvertGroup(to = ValidationGroups.Post.class) TvShow tvShow) {
		return tvShowService.create(tvShow);
	}

	@DELETE
	public void deleteAll() {
		log.warn("Deleting ALL the data!!");
		tvShowService.deleteAll();
	}

	@DELETE
	@Path("/{id}")
	public void deleteOne(@PathParam("id") Long id) {
		log.info("Deleting tv show id={}", id);
		tvShowService.deleteById(id);
	}

}
