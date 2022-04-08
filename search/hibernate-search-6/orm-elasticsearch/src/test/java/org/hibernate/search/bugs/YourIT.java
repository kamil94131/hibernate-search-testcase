package org.hibernate.search.bugs;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.search.engine.search.common.BooleanOperator;
import org.hibernate.search.engine.search.predicate.dsl.SimpleQueryFlag;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;

import org.junit.Test;

public class YourIT extends SearchTestBase {

	@Override
	public Class<?>[] getAnnotatedClasses() {
		return new Class<?>[]{ YourAnnotatedEntity.class, Product.class };
	}

	@Test
	public void testYourBug() {
		try ( Session s = getSessionFactory().openSession() ) {
			Product product1 = new Product(1L, "description1");
			Product product2 = new Product(2L, "description2");
			Product product3 = new Product(3L, "description3");
			Product product4 = new Product(4L, null);
			Product product5 = new Product(5L, null);
	
			Transaction tx = s.beginTransaction();
			s.persist(product1);
			s.persist(product2);
			s.persist(product3);
			s.persist(product4);
			s.persist(product5);
			tx.commit();
		}

		try ( Session session = getSessionFactory().openSession() ) {
			SearchSession searchSession = Search.session( session );

			String searchQuery = "*";
			List<Product> products = searchSession
					.search(Product.class)
					.where(f -> f.simpleQueryString().fields("description_normalized.default")
							.matching(searchQuery)
							.analyzer("standard_analyzer")
							.flags(SimpleQueryFlag.WHITESPACE)
							.defaultOperator(BooleanOperator.AND)
					).fetchAllHits();

			assertThat( products )
					.hasSize(0); //return 5
		}
	}

}
