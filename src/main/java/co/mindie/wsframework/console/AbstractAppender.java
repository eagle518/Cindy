/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.console
// AbstractAppender.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 30, 2014 at 1:06:31 PM
////////

package co.mindie.wsframework.console;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;

public abstract class AbstractAppender implements Appender {

	////////////////////////
	// VARIABLES
	////////////////

	private Filter filter;
	private ErrorHandler errorHandler;
	private Layout layout;
	private String name;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public AbstractAppender() {

	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public void addFilter(Filter newFilter) {
		if (this.filter == null) {
			this.filter = newFilter;
		}
	}

	@Override
	public void clearFilters() {
		this.filter = null;
	}

	@Override
	public void close() {

	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	@Override
	public Filter getFilter() {
		return this.filter;
	}

	@Override
	public ErrorHandler getErrorHandler() {
		return this.errorHandler;
	}

	@Override
	public Layout getLayout() {
		return this.layout;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	@Override
	public void setLayout(Layout layout) {
		this.layout = layout;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}
}
