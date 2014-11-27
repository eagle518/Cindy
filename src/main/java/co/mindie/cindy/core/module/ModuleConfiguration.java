package co.mindie.cindy.core.module;

import java.util.Map;

public interface ModuleConfiguration {

	void set(String key, Object value);

	Object get(String key);

	Map<String, Object> getMap();

}
