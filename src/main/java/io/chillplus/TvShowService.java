package io.chillplus;

import io.chillplus.api.TvShow;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TvShowService {

  public List<TvShow> getAll() {
    return TvShow.findAllOrderByTitle();
  }

  public TvShow findById(Long id) {
    return TvShow.findById(id);
  }

  public TvShow create(TvShow tvShow) {
    tvShow.persistAndFlush();
    return tvShow;
  }

  public void deleteAll() {
    TvShow.deleteAll();
  }

  public void deleteById(Long id) {
    TvShow.deleteById(id);
  }
}
