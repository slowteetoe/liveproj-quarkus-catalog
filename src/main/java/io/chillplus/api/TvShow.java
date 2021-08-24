package io.chillplus.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.chillplus.ValidationGroups;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TvShow {

	@Null(message = "ID will be auto-assigned, you may not provide one", groups =
			ValidationGroups.Post.class)
	private Long id;

	@NotBlank(message = "Title may not be blank")
	private String title;

	private String category;

	TvShow() {
	}

	TvShow(Long id, String title, String category) {
		this.id = id;
		this.title = title;
		this.category = category;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
