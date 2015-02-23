package co.mindie.cindy.hibernate.utils;

import co.mindie.cindy.dao.domain.AbstractListRequest;
import co.mindie.cindy.dao.domain.Page;
import me.corsin.javatools.misc.Pool;
import me.corsin.javatools.reflect.ReflectionUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.transform.ResultTransformer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CriteriaBuilder {

	final private List<Order> orders;
	final private List<Criterion> criterions;

	////////////////////////
	// VARIABLES
	////////////////

	final private List<Alias> alias;
	private String groupByProperty;
	private Projection projection;
	private ResultTransformer resultTransformer;
	private Pool<CriteriaBuilder> criteriaBuilderPool;
	private Session session;
	private Class<?> returnedClass;

	public CriteriaBuilder() {
		this(null);
	}

	public CriteriaBuilder(Pool<CriteriaBuilder> criteriaBuilderPool) {
		this.orders = new ArrayList<>();
		this.criterions = new ArrayList<>();
		this.alias = new ArrayList<>();

		this.criteriaBuilderPool = criteriaBuilderPool;
		this.reset();
	}

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public CriteriaBuilder configure(Session session, Class<?> returnedClass) {
		this.session = session;
		this.returnedClass = returnedClass;

		return this;
	}

	public CriteriaBuilder add(Criterion criterion) {
		this.criterions.add(criterion);

		return this;
	}

	////////////////////////
	// METHODS
	////////////////

	public CriteriaBuilder addOrder(Order order) {
		this.orders.add(order);

		return this;
	}

	public CriteriaBuilder createAlias(String propertyName, String alias) {
		this.alias.add(new Alias(propertyName, alias));

		return this;
	}

	public void reset() {
		this.alias.clear();
		this.criterions.clear();
		this.orders.clear();

		this.groupByProperty = null;
		this.projection = null;
		this.resultTransformer = null;
		this.session = null;
		this.returnedClass = null;
	}

	public void release() {
		this.reset();

		if (this.criteriaBuilderPool != null) {
			this.criteriaBuilderPool.release(this);
		}
	}

	private void ensureConfigure() {
		if (this.session == null || this.returnedClass == null) {
			throw new RuntimeException("CriteriaBuilder wasn't properly configured. Missing session or returnedClass");
		}
	}

	protected Criteria getCountCriteria() {
		Criteria criteria = this.session.createCriteria(this.returnedClass).setProjection(Projections.rowCount());
		for (Alias alias : this.alias) {
			criteria.createAlias(alias.propertyName, alias.alias);
		}

		this.criterions.forEach(criteria::add);

		return criteria;
	}

	protected long getCount() {
		this.ensureConfigure();

		if (this.groupByProperty != null) {
			// FIXME I didn't find a good way to implement a count using a criteria
			return 0;
		} else {
			Criteria criteria = this.getCountCriteria();
			Number number = (Number) criteria.uniqueResult();
			return number.longValue();
		}
	}

	protected Criteria getResultCriteria() {
		this.ensureConfigure();

		Criteria criteria = this.session.createCriteria(this.returnedClass);

		for (Alias alias : this.alias) {
			criteria.createAlias(alias.propertyName, alias.alias);
		}

		for (Criterion criterion : this.criterions) {
			criteria.add(criterion);
		}

		for (Order order : this.orders) {
			criteria.addOrder(order);
		}

		if (this.projection != null) {
			criteria.setProjection(this.projection);
		}

		if (this.resultTransformer != null) {
			criteria.setResultTransformer(this.resultTransformer);
		}

		return criteria;
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> getResults() {
		return (List<T>) this.getResultCriteria().list();
	}

	@SuppressWarnings("unchecked")
	protected <T> List<T> getResults(AbstractListRequest abstractListRequest) {
		Criteria criteria = this.getResultCriteria();
		abstractListRequest.getSorts().forEach(sort -> {
			switch (sort.getDirection()) {
				case ASC:
					criteria.addOrder(Order.asc(sort.getProperty()));
					break;
				case DESC:
					criteria.addOrder(Order.desc(sort.getProperty()));
					break;
			}
		});

		if (abstractListRequest.getOffset() != null) {
			criteria.setFirstResult(abstractListRequest.getOffset());
		}
		if (abstractListRequest.getLimit() != null) {
			criteria.setMaxResults(abstractListRequest.getLimit());
		}

		return (List<T>) criteria
				.list();
	}

	@SuppressWarnings("unchecked")
	private <T> T getResult() {
		return (T) this.getResultCriteria().uniqueResult();
	}

	public <T> Page<T> page(AbstractListRequest abstractListRequest) {
		List<T> results = this.getResults(abstractListRequest);

		long count = this.getCount();

		Page<T> page = new Page<T>(results, abstractListRequest, count);

		this.release();

		return page;
	}

	public <T> List<T> list() {
		List<T> list = this.getResults();

		this.release();

		return list;
	}

	public <T> T single() {
		T element = this.getResult();

		this.release();

		return element;
	}

	public long count() {
		long size = this.getCount();

		this.release();

		return size;
	}

	private void generateProjectionForGroupBy(String groupByProperty) {
		ProjectionList projectionList = Projections.projectionList();

		for (Field field : this.returnedClass.getDeclaredFields()) {
			projectionList.add(Projections.property(field.getName()));
		}

		projectionList.add(Projections.groupProperty(groupByProperty));

		this.setProjection(projectionList);
		this.setResultTransformer(new GroupByResultTransformer(this.returnedClass));
	}

	public Pool<CriteriaBuilder> getPool() {
		return this.criteriaBuilderPool;
	}

	public CriteriaBuilder setPool(Pool<CriteriaBuilder> pool) {
		this.criteriaBuilderPool = pool;

		return this;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public Projection getProjection() {
		return projection;
	}

	public CriteriaBuilder setProjection(Projection projection) {
		this.projection = projection;

		return this;
	}

	public final ResultTransformer getResultTransformer() {
		return resultTransformer;
	}

	public CriteriaBuilder setResultTransformer(ResultTransformer resultTransformer) {
		this.resultTransformer = resultTransformer;

		return this;
	}

	public final String getGroupByPropertyName() {
		return this.groupByProperty;
	}

	public final CriteriaBuilder setGroupByPropertyName(String propertyName) {
		this.groupByProperty = propertyName;

		if (propertyName != null) {
			this.generateProjectionForGroupBy(propertyName);
		}

		return this;
	}

	private static class Alias {
		final public String propertyName;
		final public String alias;

		public Alias(String propertyName, String alias) {
			this.propertyName = propertyName;
			this.alias = alias;
		}
	}

	public static class GroupByResultTransformer implements ResultTransformer {
		private static final long serialVersionUID = 882346353573589561L;
		private Class<?> outputClass;

		public GroupByResultTransformer(Class<?> outputClass) {
			this.outputClass = outputClass;
		}

		@Override
		public Object transformTuple(Object[] tuple, String[] aliases) {
			try {
				Field[] fields = this.outputClass.getDeclaredFields();
				Object outputObject = this.outputClass.newInstance();

				for (int i = 0; i < fields.length && i < tuple.length; i++) {
					ReflectionUtils.setField(outputObject, fields[i], tuple[i]);
				}

				return outputObject;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public List transformList(List collection) {
			return collection;
		}

	}
}
