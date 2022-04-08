package org.hibernate.search.bugs;

import org.hibernate.search.engine.backend.document.DocumentElement;
import org.hibernate.search.engine.backend.document.IndexObjectFieldReference;
import org.hibernate.search.engine.backend.document.model.dsl.IndexSchemaElement;
import org.hibernate.search.engine.backend.document.model.dsl.IndexSchemaObjectField;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.bridge.PropertyBridge;
import org.hibernate.search.mapper.pojo.bridge.binding.PropertyBindingContext;
import org.hibernate.search.mapper.pojo.bridge.mapping.programmatic.PropertyBinder;
import org.hibernate.search.mapper.pojo.bridge.runtime.PropertyBridgeWriteContext;


public class LocalizedStringSingleBinder implements PropertyBinder {

	private static final String DEFAULT_FIELD = "default";

	private String field;
	private String analyzer;
	private String normalizer;
	private Projectable projectable;
	private Sortable sortable;
	private Searchable searchable;

	public LocalizedStringSingleBinder field(String field) {
		this.field = field;
		return this;
	}

	public LocalizedStringSingleBinder analyzer(String analyzer) {
		this.analyzer = analyzer;
		return this;
	}

	public LocalizedStringSingleBinder normalizer(String normalizer) {
		this.normalizer = normalizer;
		return this;
	}

	public LocalizedStringSingleBinder projectable(Projectable projectable) {
		this.projectable = projectable;
		return this;
	}

	public LocalizedStringSingleBinder sortable(Sortable sortable) {
		this.sortable = sortable;
		return this;
	}

	public LocalizedStringSingleBinder searchable(Searchable searchable) {
		this.searchable = searchable;
		return this;
	}

	@Override
	public void bind(PropertyBindingContext context) {
		context.dependencies().useRootOnly();

		IndexSchemaElement schemaElement = context.indexSchemaElement();

		IndexSchemaObjectField field = schemaElement.objectField(this.field);

		field.fieldTemplate(String.format("%sTemplate", this.field),
				f -> f.asString()
						.searchable(searchable)
						.projectable(projectable)
						.sortable(sortable)
						.analyzer(analyzer)
						.normalizer(normalizer)
		);

		context.bridge(String.class, new Bridge(
				field.toReference()
		));
	}

	private static class Bridge implements PropertyBridge<String> {
		private final IndexObjectFieldReference field;

		private Bridge(IndexObjectFieldReference field) {
			this.field = field;
		}

		@Override
		public void write(DocumentElement target, String bridgedElement, PropertyBridgeWriteContext context) {
			DocumentElement fieldElement = target.addObject(field);

			// create index field description_normalized.default and put there value
			//in my case i have in addition localized fields like description_normalized.en, description_normalized.de
			//this is just a simpler version

			fieldElement.addValue(DEFAULT_FIELD, bridgedElement);
		}
	}
}
