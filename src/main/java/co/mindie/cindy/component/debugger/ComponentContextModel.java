package co.mindie.cindy.component.debugger;

import java.util.List;

/**
 * Created by simoncorsin on 24/09/14.
 */
public class ComponentContextModel {

	////////////////////////
	// VARIABLES
	////////////////

	private long id;
	private Integer ownerId;
	private List<ComponentModel> components;
	private List<ComponentContextModel> isolatedChildComponentContexts;

	////////////////////////
	// CONSTRUCTORS
	////////////////


	////////////////////////
	// METHODS
	////////////////


	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Integer getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}

	public List<ComponentModel> getComponents() {
		return components;
	}

	public void setComponents(List<ComponentModel> components) {
		this.components = components;
	}

	public List<ComponentContextModel> getIsolatedChildComponentContexts() {
		return isolatedChildComponentContexts;
	}

	public void setIsolatedChildComponentContexts(List<ComponentContextModel> isolatedChildComponentContexts) {
		this.isolatedChildComponentContexts = isolatedChildComponentContexts;
	}
}
