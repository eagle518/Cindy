/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller
// ServletAdapter.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 4, 2014 at 12:12:43 PM
////////

package co.mindie.wsframework.controller.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import co.mindie.wsframework.automapping.HttpMethod;
import co.mindie.wsframework.controllermanager.ControllerManager;

public class ServletAdapter extends HttpServlet {

	////////////////////////
	// VARIABLES
	////////////////

	private static final long serialVersionUID = -2474977883142244729L;

	private ControllerManager controllerManager;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ServletAdapter(ControllerManager controllerManager) {
		this.controllerManager = controllerManager;
	}

	////////////////////////
	// METHODS
	////////////////

	private void handle(HttpMethod httpMethod, HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpRequestServletImpl httpRequest = new HttpRequestServletImpl(httpMethod, request);
		HttpResponseServletImpl httpResponse = new HttpResponseServletImpl(response);

		this.controllerManager.handle(httpRequest, httpResponse);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.handle(HttpMethod.DELETE, req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.handle(HttpMethod.GET, req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.handle(HttpMethod.POST, req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.handle(HttpMethod.PUT, req, resp);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
