package io.chillplus;

import io.chillplus.api.TvShow;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class TvShowService {

	private static final AtomicLong SEQ = new AtomicLong();

	private final Map<Long, TvShow> data = new TreeMap<>();

	public List<TvShow> getAll() {
		return new ArrayList<>(data.values());
	}

	public TvShow findById(Long id) {
		return data.get(id);
	}

	public TvShow create(TvShow tvShow) {
		tvShow.setId(SEQ.incrementAndGet());
		data.put(tvShow.getId(), tvShow);
		return tvShow;
	}

	public void deleteAll() {
		data.clear();
	}

	public void deleteById(Long id) {
		data.remove(id);
	}
}
