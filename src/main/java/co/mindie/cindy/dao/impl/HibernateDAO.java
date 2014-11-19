/////////////////////////////////////////////////
// Project : webservice
// Package : com.ever.webservice.dao.impl
// DataAccessObject.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Mar 6, 2013 at 5:29:28 PM
////////

package co.mindie.cindy.dao.impl;

import co.mindie.cindy.automapping.Wired;
import co.mindie.cindy.dao.domain.AbstractListRequest;
import co.mindie.cindy.dao.domain.Page;
import co.mindie.cindy.dao.utils.CriteriaBuilder;
import co.mindie.cindy.dao.utils.CriteriaBuilderFactory;
import co.mindie.cindy.dao.utils.GroupByResultTransformer;
import co.mindie.cindy.database.HibernateDatabase;
import co.mindie.cindy.database.handle.HibernateDatabaseHandle;
import co.mindie.cindy.utils.FieldProperty;
import me.corsin.javatools.reflect.ReflectionUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class HibernateDAO<ElementType, PrimaryKey extends Serializable> extends AbstractDAO<ElementType, PrimaryKey, HibernateDatabase> implements KeyForEntityResolver<PrimaryKey, ElementType> {

	// //////////////////////
	// VARIABLES
	// //////////////

	public static final String DEFAULT_ID_PROPERTY_NAME = "id";
	public static final String DEFAULT_CREATED_DATE_PROPERTY_NAME = "createdDate";
	public static final String DEFAULT_UPDATED_DATE_PROPERTY_NAME = "updatedDate";
	private HibernateDatabaseHandle databaseHandle;

	@Wired private CriteriaBuilderFactory criteriaBuilderFactory;

	// //////////////////////
	// CONSTRUCTORS
	// //////////////

	public HibernateDAO(Class<ElementType> managedClass) {
		this(managedClass, DEFAULT_ID_PROPERTY_NAME, DEFAULT_CREATED_DATE_PROPERTY_NAME, DEFAULT_UPDATED_DATE_PROPERTY_NAME);
	}

	public HibernateDAO(Class<ElementType> managedClass, String primaryKeyPropertyName, String createdDatePropertyName, String updatedDatePropertyName) {
		super(managedClass, primaryKeyPropertyName, createdDatePropertyName, updatedDatePropertyName);
	}

	// //////////////////////
	// METHODS
	// //////////////

	protected static void limit(Object object, String fieldName, int size) {
		Object value = ReflectionUtils.getField(object, fieldName);

		if (value != null) {
			ReflectionUtils.setField(object, fieldName, limit((String) value, size));
		}
	}

	protected static String limit(String input, int size) {
		String output = input;

		if (output != null && output.length() > size) {
			output = output.substring(0, size);
		}

		return output;
	}

	/**
	 * Limit the fields so they can fit in the Database. This method is called
	 * automatically when saving or updating the element.
	 *
	 * @param element
	 */
	public void limit(ElementType element) {
		for (Field field : element.getClass().getDeclaredFields()) {
			FieldProperty property = field.getAnnotation(FieldProperty.class);

			if (property != null) {
				limit(element, field.getName(), property.size());
			}
		}
	}

	public ElementType findForKey(PrimaryKey key) {
		return (ElementType) this.getDatabaseHandle().getSession().get(this.getManagedClass(), key);
	}

	public List<Serializable> saveAll(Iterable<ElementType> elements) {
		List<Serializable> keys = new ArrayList<>();
		elements.forEach(e -> {
			keys.add(this.save(e));
		});
		return keys;
	}

	public Serializable save(ElementType element) {
		this.limit(element);

		DateTime currentTimeGMT = DateTime.now();
		ReflectionUtils.setField(element, this.getCreatedDatePropertyName(), currentTimeGMT);
		ReflectionUtils.setField(element, this.getUpdatedDatePropertyName(), currentTimeGMT);

		Serializable key = this.getDatabaseHandle().save(element);

		ReflectionUtils.setField(element, this.getPrimaryKeyPropertyName(), key);

		return key;
	}

	public void update(ElementType element) {
		this.limit(element);

		ReflectionUtils.setField(element, this.getUpdatedDatePropertyName(), DateTime.now());

		this.getDatabaseHandle().update(element);
	}

	public void delete(ElementType element) {
		this.getDatabaseHandle().delete(element);
	}

	public long getTotalCount() {
		Number number = (Number) this.getDatabaseHandle().getSession().createCriteria(this.getManagedClass()).setProjection(Projections.rowCount()).uniqueResult();

		return number.intValue();
	}

	public long getTotalCountSince(DateTime date) {
		Number number = (Number) this.getDatabaseHandle().getSession().createCriteria(this.getManagedClass()).setProjection(Projections.rowCount()).add(Restrictions.ge(this.getCreatedDatePropertyName(), date)).uniqueResult();

		return number.intValue();
	}

	protected Criteria createHibernateCriteria() {
		// TODO maybe remove this method and include the missing Criteria methods in the CriteriaBuilder (eg: setFetchMode)
		return this.getDatabaseHandle().getSession().createCriteria(this.getManagedClass());
	}

	protected CriteriaBuilder createCriteria() {
		return this.createCriteria(this.getManagedClass());
	}

	protected CriteriaBuilder createCriteria(Class<?> managedClass) {
		return this.criteriaBuilderFactory.createCriteria(
				this.getDatabaseHandle().getSession(),
				managedClass
		);
	}

	final protected Query createQuery(String query) {
		return this.getDatabaseHandle().getSession().createQuery(query);
	}

	final protected SQLQuery createSQLQuery(String query) {
		return this.getDatabaseHandle().getSession().createSQLQuery(query);
	}

	final protected SQLQuery createSQLQuery(String query, Class<?> outputClass) {
		return this.createSQLQuery(query).addEntity(outputClass);
	}

	final protected ElementType getSingleForValue(String fieldName, Object fieldValue) {
		return (ElementType) this.createCriteria().add(Restrictions.eq(fieldName, fieldValue)).single();
	}

	final protected ElementType[] transformResult(List<Object[]> result) {
		return this.transformResult(result, this.getManagedClass());
	}

	final protected <T> T[] transformResult(List<Object[]> result, Class<T> outputClass) {
		T[] transformedArray = (T[]) Array.newInstance(outputClass, result.size());

		GroupByResultTransformer groupByTransformer = new GroupByResultTransformer(outputClass);
		for (int i = 0; i < result.size(); i++) {
			transformedArray[i] = (T) groupByTransformer.transformTuple(result.get(i), null);
		}

		return transformedArray;
	}

	public void flush() {
		if (this.databaseHandle != null) {
			this.databaseHandle.flush();
		}
	}

	public void close() {
		if (this.databaseHandle != null) {
			this.databaseHandle.close();
		}
	}

	public List<ElementType> findAll() {
		return this.createCriteria()
				.list();
	}

	public Page<ElementType> findAll(AbstractListRequest abstractListRequest) {
		return this.createCriteria()
				.page(abstractListRequest);
	}

	public boolean exists(PrimaryKey key) {
		return this.findForKey(key) != null;
	}

	public long getTotalCountBetween(DateTime fromDate, DateTime toDate) {
		return ((Number) this.createCriteria()
				.add(Restrictions.ge(this.getCreatedDatePropertyName(), fromDate))
				.add(Restrictions.le(this.getCreatedDatePropertyName(), toDate))
				.setProjection(Projections.rowCount())
				.single()).longValue();
	}

	public List<ElementType> findAllSince(DateTime date) {
		return this.createCriteria()
				.add(Restrictions.gt(this.getCreatedDatePropertyName(), date))
				.list();
	}

	public Page<ElementType> findSince(DateTime date, AbstractListRequest abstractListRequest) {
		return this.createCriteria()
				.add(Restrictions.gt(this.getCreatedDatePropertyName(), date))
				.page(abstractListRequest);
	}

	public List<ElementType> findForKeys(List<PrimaryKey> keys) {
		return this.findForKeys(keys, this.getManagedClass(), this);
	}

	/**
	 * Resolves elements for keys. The PrimaryKey must have a working
	 * implementation of getHashCode() or this method will have an unexpected
	 * behavior. Important note: this has never been tested.
	 *
	 * @param keys
	 * @return
	 */
	public <OutputType, OutputTypeKey> List<OutputType> findForKeys(List<OutputTypeKey> keys, Class<OutputType> outputClass, KeyForEntityResolver<OutputTypeKey, OutputType> keyForEntityResolver) {
		if (keys.isEmpty()) {
			return new ArrayList<OutputType>();
		}

		Map<OutputTypeKey, OutputType> associations = new HashMap<>();

		List<OutputType> elements = this.createCriteria(outputClass).add(Restrictions.in(this.getPrimaryKeyPropertyName(), keys)).list();

		for (OutputType element : elements) {
			OutputTypeKey key = keyForEntityResolver.getKeyForEntity(element);
			if (key == null) {
				throw new RuntimeException("getKeyForElement did not return the primary key");
			}
			associations.put(key, element);
		}

		List<OutputType> outputList = new ArrayList<>();
		for (OutputTypeKey key : keys) {
			OutputType element = associations.get(key);

			// element might be null. Still have to think about what to do in
			// that case

			outputList.add(element);
		}

		return outputList;
	}

	@SuppressWarnings({"rawtypes", "unused"})
	protected <Output> List<Output> resolveExternal(Class<Output> outputType, List<Object> keys) {
//		IDAO outputDAO = this.getRequestServiceForModelClass(outputType);
		HibernateDAO outputDAO = null;

		if (outputDAO == null) {
			throw new RuntimeException("No DAO found for output type " + outputType);
		}

		return outputDAO.findForKeys(keys);
	}

	public HibernateDatabaseHandle getDatabaseHandle() {
		return this.databaseHandle;
	}

	public void setDatabaseHandle(HibernateDatabaseHandle databaseHandle) {
		this.databaseHandle = databaseHandle;
	}

	@Override
	public PrimaryKey getKeyForEntity(ElementType entity) {
		return (PrimaryKey) ReflectionUtils.getField(entity, this.getPrimaryKeyPropertyName());
	}

	// //////////////////////
	// GETTERS/SETTERS
	// //////////////
}
