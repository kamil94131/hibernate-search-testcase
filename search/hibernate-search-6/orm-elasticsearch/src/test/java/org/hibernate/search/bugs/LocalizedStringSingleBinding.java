package org.hibernate.search.bugs;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;
import java.util.function.Predicate;

import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.processing.PropertyMapping;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.processing.PropertyMappingAnnotationProcessor;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.processing.PropertyMappingAnnotationProcessorContext;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.processing.PropertyMappingAnnotationProcessorRef;
import org.hibernate.search.mapper.pojo.mapping.definition.programmatic.PropertyMappingStep;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
@PropertyMapping(processor = @PropertyMappingAnnotationProcessorRef(type = LocalizedStringSingleBinding.Processor.class))
@Repeatable(LocalizedStringSingleBinding.List.class)
@Documented
public @interface LocalizedStringSingleBinding {

	String field() default "";

	String analyzer() default "";

	String normalizer() default "";

	Projectable projectable() default Projectable.DEFAULT;

	Sortable sortable() default Sortable.DEFAULT;

	Searchable searchable() default Searchable.DEFAULT;

	class Processor implements PropertyMappingAnnotationProcessor<LocalizedStringSingleBinding> {

		@Override
		public void process(PropertyMappingStep mapping, LocalizedStringSingleBinding annotation, PropertyMappingAnnotationProcessorContext context) {
			LocalizedStringSingleBinder binder = new LocalizedStringSingleBinder();
			Predicate<String> isNotBlank = f -> f != null && !f.isEmpty(); // instead  of StringUtils.

			Optional.ofNullable(annotation.field()).filter(isNotBlank).ifPresent(binder::field);
			Optional.ofNullable(annotation.analyzer()).filter(isNotBlank).ifPresent(binder::analyzer);
			Optional.ofNullable(annotation.normalizer()).filter(isNotBlank).ifPresent(binder::normalizer);

			binder.projectable(annotation.projectable());
			binder.sortable(annotation.sortable());
			binder.searchable(annotation.searchable());
			mapping.binder(binder);
		}
	}

	@Documented
	@Target({ ElementType.METHOD, ElementType.FIELD })
	@Retention(RetentionPolicy.RUNTIME)
	@interface List {
		LocalizedStringSingleBinding[] value();
	}
}
