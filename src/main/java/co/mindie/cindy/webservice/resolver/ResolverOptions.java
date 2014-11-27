package co.mindie.cindy.webservice.resolver;

import me.corsin.javatools.misc.NullArgumentException;

import java.util.HashMap;
import java.util.Map;

public class ResolverOptions {

	////////////////////////
	// VARIABLES
	////////////////

	private Map<String, String> options;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ResolverOptions(String key, String value) {
		Map<String, String> options = new HashMap<>();

		options.put(key, value);

		this.options = options;
	}

	public ResolverOptions(Map<String, String> options) {
		if (options == null) {
			throw new NullArgumentException("options");
		}

		this.options = options;
	}

	////////////////////////
	// METHODS
	////////////////

	public String get(String key) {
		return this.options.get(key);
	}

	public Integer getInt(String key) {
		String str = this.get(key);

		if (str == null) {
			return null;
		}

		return Integer.valueOf(str);
	}

	public int getInt(String key, int defaultValue) {
		Integer value = this.getInt(key);

		return value != null ? value : defaultValue;
	}

	public Long getLong(String key) {
		String str = this.get(key);

		if (str == null) {
			return null;
		}

		return Long.valueOf(str);
	}

	public double getLong(String key, long defaultValue) {
		Long value = this.getLong(key);

		return value != null ? value : defaultValue;
	}

	public Float getFloat(String key) {
		String str = this.get(key);

		if (str == null) {
			return null;
		}

		return Float.valueOf(str);
	}

	public float getFloat(String key, float defaultValue) {
		Float value = this.getFloat(key);

		return value != null ? value : defaultValue;
	}

	public Double getDouble(String key) {
		String str = this.get(key);

		if (str == null) {
			return null;
		}

		return Double.valueOf(str);
	}

	public double getDouble(String key, double defaultValue) {
		Double value = this.getDouble(key);

		return value != null ? value : defaultValue;
	}


	public Boolean getBoolean(String key) {
		String str = this.get(key);

		if (str == null) {
			return null;
		}

		return Boolean.valueOf(str);
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		Boolean value = this.getBoolean(key);

		return value != null ? value.booleanValue() : defaultValue;
	}

	private static ResolverOptions emptyOptions = new ResolverOptions(new HashMap<>());
	public static ResolverOptions emptyOptions() {
		return emptyOptions;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
