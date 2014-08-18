/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller
// IExceptionHandler.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Jun 11, 2014 at 2:46:15 PM
////////

package co.mindie.wsframework.controller;

import java.io.IOException;

import co.mindie.wsframework.context.RequestContext;
import co.mindie.wsframework.controllermanager.HttpRequest;
import co.mindie.wsframework.controllermanager.HttpResponse;

public interface IRequestErrorHandler {

	Object handleEndpointNotFound(HttpRequest request, HttpResponse response);

	Object handleMaintenanceMode(HttpRequest request, HttpResponse response);

	Object handleRequestCreationFailed(HttpRequest request, HttpResponse response, Throwable e);

	Object handleRequestException(RequestContext context, Throwable exception);

	Object handleResponseConverterException(RequestContext context, Throwable exception);

	void handleResponseWritingException(HttpRequest request, IOException exception);

}
