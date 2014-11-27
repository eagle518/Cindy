package co.mindie.cindy.hibernate.utils;

import org.hibernate.Session;

public interface CriteriaBuilderFactory {
	CriteriaBuilder createCriteria(Session session, Class<?> managedClass);
}
