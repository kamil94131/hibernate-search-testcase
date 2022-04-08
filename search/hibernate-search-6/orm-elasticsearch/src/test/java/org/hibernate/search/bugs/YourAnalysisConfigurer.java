package org.hibernate.search.bugs;

import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurationContext;
import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurer;

public class YourAnalysisConfigurer implements ElasticsearchAnalysisConfigurer {
	@Override
	public void configure(ElasticsearchAnalysisConfigurationContext context) {
		context.analyzer( "nameAnalyzer" ).custom()
				.tokenizer( "whitespace" )
				.tokenFilters( "lowercase", "asciifolding" );
		definePatternReplaceSpecialCharsFilter(context);
		addStandardAnalyzer(context);
		addStandardNormalizer(context);
	}

	private void addStandardNormalizer(ElasticsearchAnalysisConfigurationContext context) {
		context.normalizer("standard_normalizer").custom()
				.tokenFilters("lowercase")
				.charFilters("special");
	}

	private void definePatternReplaceSpecialCharsFilter(ElasticsearchAnalysisConfigurationContext context) {
		context.charFilter("special")
				.type("pattern_replace")
				.param("pattern", "[^a-zA-Z0-9]+")
				.param("replacement", "");
	}

	private void addStandardAnalyzer(ElasticsearchAnalysisConfigurationContext context) {
		context.analyzer("standard_analyzer").custom()
				.tokenizer("standard")
				.charFilters("special")
				.tokenFilters("lowercase");
	}
}
