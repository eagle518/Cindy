/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.criterions
// FulltextSearch.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Mar 26, 2014 at 5:57:57 PM
////////

package co.mindie.cindy.hibernate.criterions;

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
	private FulltextSearchMode searchMode;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public FulltextSearch(String propertyName, String searchText) {
		this(propertyName, searchText, FulltextSearchMode.NATURAL_LANGUAGE);
	}

	public FulltextSearch(String propertyName, String searchText, FulltextSearchMode searchMode) {
		if (propertyName == null) {
			throw new IllegalArgumentException("column");
		}
		if (searchText == null) {
			throw new IllegalArgumentException("searchText");
		}
		if (searchMode == null) {
			throw new IllegalArgumentException("searchMode");
		}

		this.propertyName = propertyName;
		this.searchText = searchText;
		this.searchMode = searchMode;
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

		String searchModeStr = null;

		switch (this.searchMode) {
			case BOOLEAN:
				searchModeStr = "BOOLEAN";
				break;
			case NATURAL_LANGUAGE:
				searchModeStr = "NATURAL_LANGUAGE";
				break;
			default:
				throw new HibernateException("Null SearchMode set");
		}

		String columnsStr = columns[0];

		for (int i = 1; i < columns.length; i++) {
			columnsStr += ", " + columns[i];
		}

		return "match (" + columnsStr + ") against (? IN " + searchModeStr + " MODE)";
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
