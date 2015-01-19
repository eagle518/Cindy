/////////////////////////////////////////////////
// Project : webservice
// Package : com.ever.webservice.dao.impl
// DataAccessObject.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Mar 6, 2013 at 5:29:28 PM
////////

package co.mindie.cindy.dao;

import me.corsin.javatools.misc.NullArgumentException;
import me.corsin.javatools.reflect.ReflectionUtils;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractDAO<ElementType> {

	// //////////////////////
	// VARIABLES
	// //////////////

	private static final Map<Class<?>, Class<?>> daoClassToManagedClassCache = new HashMap<>();

	private final String primaryKeyPropertyName;
	private final String createdDatePropertyName;
	private final String updatedDatePropertyName;
	private final Class<ElementType> managedClass;

	// //////////////////////
	// CONSTRUCTORS
	// //////////////

	public AbstractDAO(String primaryKeyPropertyName, String createdDatePropertyName, String updatedDatePropertyName) {
		this(null, primaryKeyPropertyName, createdDatePropertyName, updatedDatePropertyName);
	}

	@SuppressWarnings("unchecked")
	public AbstractDAO(Class<ElementType> managedClass, String primaryKeyPropertyName, String createdDatePropertyName, String updatedDatePropertyName) {
		if (managedClass == null) {
			synchronized (daoClassToManagedClassCache) {
				Class<?> myCls = this.getClass();
				managedClass = (Class<ElementType>)daoClassToManagedClassCache.get(myCls);

				if (managedClass == null) {
					managedClass = (Class<ElementType>) ReflectionUtils.getGenericTypeParameter(myCls, AbstractDAO.class, 0);

					if (managedClass != null) {
						daoClassToManagedClassCache.put(myCls, managedClass);
					}
				}
			}
		}

		if (managedClass == null) {
			throw new NullArgumentException("managedClass");
		}

		this.managedClass = managedClass;
		this.primaryKeyPropertyName = primaryKeyPropertyName;
		this.createdDatePropertyName = createdDatePropertyName;
		this.updatedDatePropertyName = updatedDatePropertyName;
	}

	// //////////////////////
	// METHODS
	// //////////////


	// //////////////////////
	// GETTERS/SETTERS
	// //////////////

	/**
	 * Returns the name of the property that hold the primary key value.
	 *
	 * @return the primary key property name
	 */
	public String getPrimaryKeyPropertyName() {
		return this.primaryKeyPropertyName;
	}

	public String getCreatedDatePropertyName() {
		return this.createdDatePropertyName;
	}

	public String getUpdatedDatePropertyName() {
		return this.updatedDatePropertyName;
	}

	public Class<ElementType> getManagedClass() {
		return this.managedClass;
	}
}
