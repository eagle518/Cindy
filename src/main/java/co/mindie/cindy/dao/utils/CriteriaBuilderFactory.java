package co.mindie.cindy.dao.utils;

import org.hibernate.Session;

public interface CriteriaBuilderFactory {
	CriteriaBuilder createCriteria(Session session, Class<?> managedClass);
}
