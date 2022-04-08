package org.hibernate.search.bugs;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.DocumentId;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

@Entity
@Indexed
public class Product {

	@Id
	@DocumentId
	private Long id;

	@LocalizedStringSingleBinding(
			field = "description_normalized",
			searchable = Searchable.YES,
			projectable = Projectable.YES,
			normalizer = "standard_normalizer"
	)
	private String description;

	public Product() {
	}

	public Product(Long id, String description) {
		this.id = id;
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}
}
