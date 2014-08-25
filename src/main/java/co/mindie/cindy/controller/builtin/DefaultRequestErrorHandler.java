/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller.builtin
// DefaultRequestErrorHandler.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jul 24, 2014 at 3:50:49 PM
////////

package co.mindie.cindy.controller.builtin;

import java.io.IOException;

import co.mindie.cindy.context.RequestContext;
import co.mindie.cindy.controller.manager.IRequestErrorHandler;
import co.mindie.cindy.controller.manager.HttpResponse;
import co.mindie.cindy.CindyApp;
import co.mindie.cindy.automapping.Component;
import co.mindie.cindy.controller.manager.HttpRequest;

@Component(dependentClass = CindyApp.class)
public class DefaultRequestErrorHandler implements IRequestErrorHandler {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public DefaultRequestErrorHandler() {

	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public Object handleEndpointNotFound(HttpRequest request, HttpResponse response) {
		RequestErrorResponse error = new RequestErrorResponse();
		error.setMessage("Endpoint not found!");
		error.setException("EndpointNotFoundException");

		return error;
	}

	@Override
	public Object handleMaintenanceMode(HttpRequest request, HttpResponse response) {
		RequestErrorResponse error = new RequestErrorResponse();
		error.setMessage("Server is in maintenance");
		error.setException("MaintenanceModeException");

		return error;
	}

	@Override
	public Object handleRequestCreationFailed(HttpRequest request, HttpResponse response, Throwable e) {
		System.err.println("Failed to create request handle");
		e.printStackTrace();

		RequestErrorResponse error = new RequestErrorResponse();
		error.setMessage("Internal Server Error");
		error.setException("InternalErrorException");

		return error;
	}

	@Override
	public Object handleRequestException(RequestContext context, Throwable exception) {
		exception.printStackTrace();

		RequestErrorResponse error = new RequestErrorResponse();
		error.setException(exception.getClass().getSimpleName());
		error.setMessage(exception.getMessage());

		return error;
	}

	@Override
	public Object handleResponseConverterException(RequestContext context, Throwable exception) {
		System.err.println("Failed to convert response");
		exception.printStackTrace();

		RequestErrorResponse error = new RequestErrorResponse();
		error.setMessage("Internal Server Error");
		error.setException("InternalErrorException");

		return error;
	}

	@Override
	public void handleResponseWritingException(HttpRequest request, IOException exception) {
		System.err.println("Failed to write response to client");
		exception.printStackTrace();
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
