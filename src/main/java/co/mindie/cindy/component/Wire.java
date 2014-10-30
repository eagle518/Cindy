/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.component
// Wire.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 11, 2014 at 2:14:47 PM
////////

package co.mindie.cindy.component;

import co.mindie.cindy.automapping.Box;
import co.mindie.cindy.automapping.Wired;
import co.mindie.cindy.automapping.SearchScope;
import co.mindie.cindy.exception.CindyException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

public class Wire {

	////////////////////////
	// VARIABLES
	////////////////

	final private Field field;
	final private Box box;
	final private String context;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public Wire(Field field, Box box, String context) {
		field.setAccessible(true);

		this.field = field;
		this.box = box;
		this.context = context;
	}

	////////////////////////
	// METHODS
	////////////////

	public boolean canSet(Class<?> cls) {
		return this.field.getType().isAssignableFrom(cls);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public void set(Object object, Object value) {
		try {
			Object oldValue = this.field.get(object);

			if (oldValue != value) {
				this.field.set(object, value);
			}
		} catch (Exception e) {
			throw new CindyException("Unable to set wire \"" + this.field.getName() + "\" of type " + this.field.getType().getName() + " to " + object.getClass().getSimpleName(), e);
		}
	}

	public Object get(Object object) {
		try {
			return this.field.get(object);
		} catch (Exception e) {
			throw new CindyException("Unable to get wire " + this.field.getName() + " of type " + this.field.getType().getName() + " to " + object.getClass().getSimpleName(), e);
		}
	}

	public Class<?> getFieldType() {
		if (this.isList()) {
			ParameterizedType stringListType = (ParameterizedType)this.field.getGenericType();

			if (stringListType == null || stringListType.getActualTypeArguments().length == 0) {
				return Object.class;
			}

			return (Class<?>)(stringListType.getActualTypeArguments()[0]);
		} else {
			return this.field.getType();
		}
	}

	public boolean isList() {
		return Collection.class.isAssignableFrom(this.field.getType());
	}

	public Field getField() {
		return this.field;
	}

	public Box getBox() {
		return box;
	}

	public String getContext() {
		return context;
	}
}
