package co.mindie.cindy.dao;

import co.mindie.cindy.AbstractCindyTest;
import co.mindie.cindy.core.annotation.Load;
import co.mindie.cindy.core.annotation.Wired;
import co.mindie.cindy.core.component.metadata.ComponentMetadataManagerBuilder;
import co.mindie.cindy.core.tools.Initializable;
import co.mindie.cindy.dao.domain.*;
import co.mindie.cindy.hibernate.dao.HibernateDAO;
import co.mindie.cindy.hibernate.database.HibernateDatabase;
import co.mindie.cindy.hibernate.database.handle.HibernateDatabaseHandle;
import co.mindie.cindy.hibernate.utils.CindyHibernateConfiguration;
import co.mindie.cindy.hibernate.utils.CriteriaBuilder;
import co.mindie.cindy.hibernate.utils.CriteriaBuilderFactory;
import com.google.common.collect.Lists;
import me.corsin.javatools.misc.SynchronizedPool;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.*;
import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HibernateDAOTest extends AbstractCindyTest {

	// //////////////////////
	// VARIABLES
	// //////////////

	private static final Logger LOGGER = Logger.getLogger(HibernateDAOTest.class);
	private static final String DATABASE_PATH = "/tmp/";
	private static final String DATABASE_PREFIX = "cindy-test-database";

	@Wired private FakeDAO dao;

	// //////////////////////
	// TESTS LIFECYCLE
	// //////////////

	@After
	public void tearDown() {
		this.emptyDatabase();
	}

	private void emptyDatabase() {
		if (this.dao != null && this.dao.getDatabaseHandle() != null) {
			Session session = this.dao.getDatabaseHandle().getSession();

			this.dao.getDatabaseHandle().flush();
			this.dao.getDatabaseHandle().beginTransaction();

			// Remove the constraints to delete everything without any problem
			session.createSQLQuery("SET foreign_key_checks = 0;").executeUpdate();
			// For each table... DELETE THEM ALL!
			for (String tableName : this.dao.getDatabaseHandle().getHibernateDatabase().getTablesNames()) {
				session.createSQLQuery("DELETE FROM " + tableName + ";").executeUpdate();
			}
			// Put the constraints back.
			session.createSQLQuery("SET foreign_key_checks = 1;").executeUpdate();

			this.dao.getDatabaseHandle().flushAndClear();
		}
	}

	@BeforeClass
	public static void staticSetUpTests() {
		deleteDatabaseFile();
	}

	@AfterClass
	public static void staticTearDownTests() {
		deleteDatabaseFile();
	}

	private static void deleteDatabaseFile() {
		try {
			File dir = new File(DATABASE_PATH);
			File[] dbFiles = dir.listFiles((file, name) -> name.startsWith(DATABASE_PREFIX));
			for (File dbFile : dbFiles) {
				if (dbFile.delete()) {
					LOGGER.debug("Deleted database file at " + dbFile.getPath());
				} else {
					LOGGER.error("Unable to delete database file at " + dbFile.getPath());
				}
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	// //////////////////////
	// TESTS
	// //////////////

	@Test
	public void dao_autodetects_managed_class() {
		TestDAO testDAO = new TestDAO2();

		assertEquals(HibernateDAOTest.class, testDAO.getManagedClass());

		// Second time should use the cached impl
		// Result should be the same
		testDAO = new TestDAO2();

		assertEquals(HibernateDAOTest.class, testDAO.getManagedClass());
	}

	@Test
	public void save_defines_sets_the_default_values_for_id_createDate_and_updatedDate() {
		// GIVEN
		DateTime now = setCurrentDateNow();
		FakeObject fake = new FakeObject();

		// WHEN
		Integer id = (int) this.dao.save(fake);

		// THEN
		assertNotNull(id);
		assertEquals(now, fake.getCreatedDate());
		assertEquals(now, fake.getUpdatedDate());
	}

	@Test
	public void findAll_with_page_request_only_returns_the_expected_entities() {
		// GIVEN
		FakeObject fake1 = new FakeObject();
		FakeObject fake2 = new FakeObject();
		FakeObject fake3 = new FakeObject();
		FakeObject fake4 = new FakeObject();
		FakeObject fake5 = new FakeObject();
		FakeObject fake6 = new FakeObject();

		this.dao.saveAll(Lists.newArrayList(fake1, fake2, fake3, fake4, fake5, fake6));

		AbstractListRequest abstractListRequest = new OffsetedRequest(2, 3, new Sort(Direction.DESC, "id"));

		// WHEN
		Page<FakeObject> page = this.dao.findAll(abstractListRequest);

		// THEN
		assertEquals(6, page.getTotalElements());
		assertEquals(3, page.size());

		List<FakeObject> data = page.getData();
		assertEquals(fake4.getId(), data.get(0).getId());
		assertEquals(fake3.getId(), data.get(1).getId());
		assertEquals(fake2.getId(), data.get(2).getId());
	}

	// //////////////////////
	// TOOLS
	// //////////////

	@Entity
	@Table(name = "fake")
	public class FakeObject {
		@Id
		@GeneratedValue
		private int id;
		@Temporal(TemporalType.TIMESTAMP)
		@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
		private DateTime createdDate;
		@Temporal(TemporalType.TIMESTAMP)
		@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
		private DateTime updatedDate;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public DateTime getCreatedDate() {
			return createdDate;
		}

		public void setCreatedDate(DateTime createdDate) {
			this.createdDate = createdDate;
		}

		public DateTime getUpdatedDate() {
			return updatedDate;
		}

		public void setUpdatedDate(DateTime updatedDate) {
			this.updatedDate = updatedDate;
		}
	}

	public static class FakeDAO extends HibernateDAO<Integer, FakeObject> implements Initializable {
		@Wired private FakeDatabaseHandle databaseHandle;

		public FakeDAO() {
			super(FakeObject.class);
		}

		@Override
		public void init() {
			this.setDatabaseHandle(this.databaseHandle);
		}
	}

	@Override
	protected void onLoad(ComponentMetadataManagerBuilder metadataManager) {
		super.onLoad(metadataManager);

		metadataManager.loadComponent(FakeDatabase.class);
		metadataManager.loadComponent(FakeDatabaseHandle.class);
		metadataManager.loadComponent(FakeDAO.class);
	}

	public static class FakeDatabaseHandle extends HibernateDatabaseHandle {
		@Wired private FakeDatabase database;

		@Override
		public HibernateDatabase getHibernateDatabase() {
			return database;
		}
	}

	public static class FakeDatabase extends HibernateDatabase {
		@Override
		protected org.hibernate.cfg.Configuration getHibernateConfiguration() {
			String dbPath = System.getProperty("cindy.test.db.path");
			if (dbPath == null) {
				dbPath = DATABASE_PATH + DATABASE_PREFIX;
			}
			String jdbcUrl = "jdbc:h2:" + dbPath + ";MODE=MYSQL";
			return new CindyHibernateConfiguration(jdbcUrl, true)
					.scanPackageForAnnotatedClasses("co.mindie.cindy.dao");
		}
	}

	@Load
	public static class FakeCriteriaBuilderFactory extends SynchronizedPool<CriteriaBuilder> implements CriteriaBuilderFactory {
		@Override
		public CriteriaBuilder createCriteria(Session session, Class<?> managedClass) {
			return this.obtain().configure(
					session,
					managedClass
			);
		}

		@Override
		protected CriteriaBuilder instantiate() {
			return new CriteriaBuilder(this);
		}
	}

	public static class TestDAO2 extends TestDAO<HibernateDAOTest> {

	}

	public static class TestDAO<T> extends HibernateDAO<Integer, T> {

	}
}