package co.mindie.cindy.component.debugger;

import co.mindie.cindy.component.ComponentContext;

import java.util.List;

/**
 * Created by simoncorsin on 24/09/14.
 */
public class ComponentModel {

	////////////////////////
	// VARIABLES
	////////////////

	private int id;
	private String type;
	private List<ComponentContextModel> subComponentContexts;

	////////////////////////
	// CONSTRUCTORS
	////////////////


	////////////////////////
	// METHODS
	////////////////


	////////////////////////
	// GETTERS/SETTERS
	////////////////


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<ComponentContextModel> getSubComponentContexts() {
		return subComponentContexts;
	}

	public void setSubComponentContexts(List<ComponentContextModel> subComponentContexts) {
		this.subComponentContexts = subComponentContexts;
	}
}
