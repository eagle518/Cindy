/////////////////////////////////////////////////
// Project : Ever WebService
// Package : com.ever.webservice.configuration
// AbstractConfiguration.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 14, 2013 at 4:19:36 PM
////////

package co.mindie.cindy.webservice.configuration;

import co.mindie.cindy.core.annotation.Load;
import co.mindie.cindy.core.tools.Initializable;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

@Load(creationPriority = -1)
public class Configuration implements Initializable {

	// //////////////////////
	// VARIABLES
	// //////////////

	private static final Logger LOGGER = Logger.getLogger(Configuration.class);
	private Properties configuration;
	private String configurationFilePath;

	// //////////////////////
	// CONSTRUCTORS
	// //////////////

	public Configuration() {
		this.configuration = new Properties();
	}

	public Configuration(Configuration original) {
		this();
		for (String key : original.getKeys()) {
			this.put(key, original.get(key));
		}
	}

	// //////////////////////
	// METHODS
	// //////////////

	@Override
	public void init() {
		if (this.configurationFilePath == null) {
			this.configurationFilePath = this.get("wsframework.configuration_file");
		}

		if (this.configurationFilePath == null) {
			this.configurationFilePath = this.get("cindy.configuration_file");
		}

		this.reloadConfiguration();
	}

	public void reloadConfiguration() {
		String configurationFilePath = this.configurationFilePath;

		if (configurationFilePath != null) {
			try {
				this.loadFile(configurationFilePath);
				LOGGER.info("Configuration loaded from " + configurationFilePath);
			} catch (IOException e) {
				throw new RuntimeException("Unable to read configuration file " + configurationFilePath, e);
			}
		}
	}

	public void loadFile(String path) throws IOException {
		InputStream fileContent = new FileInputStream(path);
		try {
			this.configuration.load(fileContent);
		} finally {
			fileContent.close();
		}
	}

	public void clearConfiguration() {
		this.configuration.clear();
	}

	public void put(String key, String value) {
		this.configuration.put(key, value);
	}

	public void put(String key, boolean booleanValue) {
		this.put(key, Boolean.toString(booleanValue));
	}

	public void put(String key, int value) {
		this.put(key, Integer.toString(value));
	}

	public void put(String key, float value) {
		this.put(key, Float.toString(value));
	}

	// //////////////////////
	// GETTERS/SETTERS
	// //////////////

	public Set<String> getKeys() {
		return this.configuration.stringPropertyNames();
	}

	public String get(String key) {
		String config = (String) this.configuration.get(key);

		if (config == null) {
			config = System.getProperty(key);

			if (config != null) {
				this.put(key, config);
			}
		}

		return config;
	}

	public Boolean getBoolean(String key) {
		String value = this.get(key);

		return value != null ? Boolean.valueOf(value) : null;
	}

	public boolean getBoolean(String key, boolean defValue) {
		String value = this.get(key);

		return value != null ? Boolean.valueOf(value) : defValue;
	}

	public Integer getInteger(String key) {
		String value = this.get(key);

		return value != null ? Integer.valueOf(value) : null;
	}

	public int getInteger(String key, int defValue) {
		Integer it = this.getInteger(key);

		if (it == null) {
			return defValue;
		}

		return it.intValue();
	}

	public Float getFloat(String key) {
		String value = this.get(key);

		return value != null ? Float.valueOf(value) : null;
	}

	public String getConfigurationFilePath() {
		return this.configurationFilePath;
	}

	public void setConfigurationFilePath(String configurationFilePath) {
		this.configurationFilePath = configurationFilePath;
	}
}
