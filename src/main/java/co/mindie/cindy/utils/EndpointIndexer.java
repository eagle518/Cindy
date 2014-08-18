/////////////////////////////////////////////////
// Project : exiled-masterserver
// Package : com.kerious.exiled.masterserver.api.utils
// PathTree.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jan 11, 2013 at 1:29:48 PM
////////

package co.mindie.cindy.utils;

import java.util.HashMap;
import java.util.Map;

import co.mindie.cindy.exception.CindyException;
import me.corsin.javatools.string.Strings;
import co.mindie.cindy.controller.EndpointEntry;
import co.mindie.cindy.controller.EndpointPathResult;

public class EndpointIndexer {

	////////////////////////
	// VARIABLES
	////////////////

	private EndpointEntry endpoint;
	private Map<String, EndpointIndexer> endpointIndexerForPath;
	private EndpointIndexer wildcardPath;

	////////////////////////
	// NESTED CLASSES
	////////////////

	public EndpointIndexer() {
		this.endpointIndexerForPath = new HashMap<>();
	}

	////////////////////////
	// CONSTRUCTORS
	////////////////

	private final String[] createPathArray(String path) {
		if (path == null) {
			path = "";
		}

		int slashIndex = 0;

		while (slashIndex < path.length() && path.charAt(slashIndex) == '/') {
			slashIndex++;
		}

		if (slashIndex > 0) {
			path = path.substring(slashIndex, path.length());
		}

		String[] splittedPath = path.split("/");

		if (splittedPath.length == 1 && splittedPath[0].isEmpty()) {
			splittedPath = new String[]{};
		}

		return splittedPath;
	}

	////////////////////////
	// METHODS
	////////////////

	private EndpointEntry getEndpoint(EndpointPathResult pathResult, String[] pathArray, int currentIndex) {
		if (currentIndex == pathArray.length) {
			return this.endpoint;
		}

		String path = pathArray[currentIndex];
		EndpointIndexer nextIndexer = this.endpointIndexerForPath.get(path);
		String identifier = null;

		if (nextIndexer == null) {
			nextIndexer = this.wildcardPath;

			if (nextIndexer != null) {
				identifier = path;
			}
		}

		pathResult.addPathValue(identifier);

		return nextIndexer != null ? nextIndexer.getEndpoint(pathResult, pathArray, currentIndex + 1) : null;
	}

	public EndpointPathResult getEndpointPathResult(String path) {
		EndpointPathResult pathResult = new EndpointPathResult();
		String[] pathArray = this.createPathArray(path);

		pathResult.setEndpointEntry(this.getEndpoint(pathResult, pathArray, 0));

		return pathResult;
	}

	private EndpointIndexer addEndpoint(EndpointEntry endpoint, String[] pathArray, int currentIndex) {
		if (currentIndex == pathArray.length) {
			if (this.endpoint == null) {
				this.endpoint = endpoint;
				return this;
			} else {
				throw new CindyException(Strings.format("Attempted to add an endpoint {#0}, but {#1}", endpoint.getPath(), this.endpoint.getPath()));
			}
		} else {
			String path = pathArray[currentIndex];
			String wildcardIdentifier = null;

			if (path.startsWith("{") && path.endsWith("}")) {
				wildcardIdentifier = path.substring(1, path.length() - 1);
			} else if (path.startsWith(":")) {
				wildcardIdentifier = path.substring(1);
			}

			EndpointIndexer nextIndexer = null;
			if (wildcardIdentifier != null) {
				if (this.wildcardPath == null) {
					this.wildcardPath = new EndpointIndexer();
				}
				nextIndexer = this.wildcardPath;
				endpoint.addPathIdentifier(wildcardIdentifier, currentIndex);
			} else {
				nextIndexer = this.endpointIndexerForPath.get(path);

				if (nextIndexer == null) {
					nextIndexer = new EndpointIndexer();
					this.endpointIndexerForPath.put(path, nextIndexer);
				}
			}

			return nextIndexer.addEndpoint(endpoint, pathArray, currentIndex + 1);
		}
	}

	public final EndpointIndexer addEndpoint(EndpointEntry endpoint) {
		String[] pathArray = this.createPathArray(endpoint.getPath());

		return this.addEndpoint(endpoint, pathArray, 0);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
