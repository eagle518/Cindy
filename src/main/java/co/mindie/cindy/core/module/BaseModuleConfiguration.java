package co.mindie.cindy.core.module;

import co.mindie.cindy.core.exception.CindyException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class BaseModuleConfiguration implements ModuleConfiguration {

	////////////////////////
	// VARIABLES
	////////////////

	private Map<String, Object> map;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public BaseModuleConfiguration() {
		this.map = new HashMap<>();
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public void set(String key, Object value) {
		this.map.put(key, value);
	}

	@Override
	public Object get(String key) {
		return this.map.get(key);
	}

	public <T> T get(String key, Class<T> outputType) {
		Object value = this.get(key);

		if (value == null) {
			return null;
		}

		if (!outputType.isAssignableFrom(value.getClass())) {
			throw new CindyException("Incompatible type: Asked to get " + outputType + " but got " + value.getClass());
		}

		return (T)value;
	}

	public boolean isEnabled(String key) {
		Boolean enabled = this.get(key, Boolean.class);

		return enabled != null && enabled;
	}

	public void enable(String key) {
		this.set(key, true);
	}

	public void disable(String key) {
		this.set(key, false);
	}

	public Integer getInt(String key) {
		return this.get(key, Integer.class);
	}

	public Long getLong(String key) {
		return this.get(key, Long.class);
	}

	public Double getDouble(String key) {
		return this.get(key, Double.class);
	}

	public String getString(String key) {
		return this.get(key, String.class);
	}

	public Float getFloat(String key) {
		return this.get(key, Float.class);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	@Override
	public Map<String, Object> getMap() {
		return this.map;
	}

}
