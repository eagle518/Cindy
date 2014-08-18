/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.criterions
// FulltextSearch.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Mar 26, 2014 at 5:57:57 PM
////////

package co.mindie.wsframework.criterions;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.engine.spi.TypedValue;

public class FulltextSearch implements Criterion {

	////////////////////////
	// VARIABLES
	////////////////

	private static final long serialVersionUID = -7841457214173304285L;
	private String propertyName;
	private String searchText;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public FulltextSearch(String propertyName, String searchText) {
		if (propertyName == null) {
			throw new IllegalArgumentException("column");
		}
		if (searchText == null) {
			throw new IllegalArgumentException("searchText");
		}

		this.propertyName = propertyName;
		this.searchText = searchText;
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
		String[] columns = criteriaQuery.findColumns(propertyName, criteria);
		if (columns.length != 1) {
			throw new HibernateException("FulltextSearch may only be used with single-column properties");
		}

		return "match (" + columns[0] + ") against (? IN NATURAL LANGUAGE MODE)";
	}

	@Override
	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
		return new TypedValue[]{
				criteriaQuery.getTypedValue(criteria, propertyName, this.searchText)
		};
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
