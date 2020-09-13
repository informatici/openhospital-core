package org.isf;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Transactional
@RunWith(SpringRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public abstract class OHCoreIntegrationTest {
	@Autowired
	private EntityManager entityManager;

	protected void cleanH2InMemoryDb() {
		List<Object[]> show_tables = entityManager.createNativeQuery("SHOW TABLES").getResultList();
		show_tables
				.stream()
				.map(result -> (String) result[0])
				.forEach(this::truncateTable);
	}

	private void truncateTable(String name) {
		entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
		entityManager.createNativeQuery("TRUNCATE TABLE " + name).executeUpdate();
		entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
	}
}
