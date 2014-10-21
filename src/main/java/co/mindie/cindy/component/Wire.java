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
import java.util.Collection;

public class Wire {

	////////////////////////
	// VARIABLES
	////////////////

	private Field field;
	private Wired wired;
	private Box box;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public Wire(Field field, Box box, Wired wired) {
		field.setAccessible(true);

		this.field = field;
		this.box = box;
		this.wired = wired;
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

			if (value == null && this.wired.required()) {
				throw new Exception("Attempted to set a required wire to null");
			}

			if (oldValue != value) {
				this.field.set(object, value);
			}
		} catch (Exception e) {
			throw new CindyException("Unable to set wire " + this.field.getName() + " of type " + this.field.getType().getName() + " to " + object.getClass().getSimpleName(), e);
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
		return this.wired.fieldClass() != void.class ? this.wired.fieldClass() : this.field.getType();
	}

	public SearchScope getScope() {
		return this.wired.searchScope();
	}

	public boolean isRequired() {
		return this.wired.required();
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
}
