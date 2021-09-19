package io.chillplus.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.chillplus.ValidationGroups;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Parameters;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

@Entity
@Table(name = "tv_show")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TvShow extends PanacheEntityBase {

  @Null(message = "ID will be auto-assigned, you may not provide one", groups =
      ValidationGroups.Post.class)
  @Id
  @GeneratedValue
  private Long id;

  @Column(name = "title")
  @NotBlank(message = "Title may not be blank")
  private String title;

  @Column(name = "category")
  private String category;

  protected TvShow() {
  }

  public TvShow(Long id, String title, String category) {
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

  public static List<TvShow> findAllOrderByTitle() {
    return list("order by title DESC");
  }

  public static TvShow findByTitle(String title) {
    return find("title", title).firstResult();
  }

  public static List<TvShow> findByCategoryIgnoreCase(
      String category,
      int pageIndex,
      int pageSize) {
    return find("LOWER(category) = LOWER(:category)", Parameters.with("category", category))
        .page(pageIndex, pageSize)
        .list();
  }
}
