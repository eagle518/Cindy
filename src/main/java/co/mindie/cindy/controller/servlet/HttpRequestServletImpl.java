/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller.servlet
// HttpRequestServletImpl.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 4, 2014 at 12:14:27 PM
////////

package co.mindie.cindy.controller.servlet;

import co.mindie.cindy.automapping.HttpMethod;
import co.mindie.cindy.controller.manager.FormFileItem;
import co.mindie.cindy.controller.manager.HttpRequest;
import me.corsin.javatools.io.IOUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestServletImpl implements HttpRequest {

	////////////////////////
	// VARIABLES
	////////////////

	final private HttpServletRequest request;
	final private HttpMethod httpMethod;
	private Map<String, List<FileItem>> bodyParameters;
	private Map<String, String[]> queryParameters;
	private InputStream inputStream;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public HttpRequestServletImpl(HttpMethod httpMethod, HttpServletRequest request) throws IOException {
		this.request = request;
		this.httpMethod = httpMethod;

		if (ServletFileUpload.isMultipartContent(request)) {
			FileItemFactory fileItemFactory = new DiskFileItemFactory(
					DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD,
					new File(System.getProperty("java.io.tmpdir"))
			);
			ServletFileUpload fileUpload = new ServletFileUpload(fileItemFactory);
			try {
				this.bodyParameters = fileUpload.parseParameterMap(request);
			} catch (FileUploadException e) {
				throw new IOException(e);
			}
		} else {
			if (request.getMethod().equals("POST") || request.getMethod().equals("PUT")) {
				String contentType = request.getHeader("Content-Type");
				if (contentType != null && contentType.contains("application/x-www-form-urlencoded")) {
					this.bodyParameters = new HashMap<>();
					String[] parameters = IOUtils.readStreamAsString(request.getInputStream()).split("&");
					for (String parameter : parameters) {
						String[] keyVal = parameter.split("=");
						if (keyVal.length == 2) {
							String key = keyVal[0];
							String value = URLDecoder.decode(keyVal[1], "UTF-8");
							List<FileItem> items = new ArrayList<>();
							items.add(new FormFileItem(key, value));
							this.bodyParameters.put(key, items);
						}
					}
				} else {
					this.inputStream = request.getInputStream();
				}
			}
		}

		this.queryParameters = request.getParameterMap();
	}

	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	@Override
	public Map<String, String[]> getQueryParameters() {
		return this.queryParameters;
	}

	@Override
	public Map<String, List<FileItem>> getBodyParameters() {
		return this.bodyParameters;
	}

	@Override
	public InputStream getBody() {
		return this.inputStream;
	}

	@Override
	public String getPathInfo() {
		return this.request.getPathInfo();
	}

	@Override
	public String getServerUrl() {
		String daUrl = this.request.getRequestURL().toString();
		String pathInfo = this.request.getServletPath() + this.request.getPathInfo();

		return daUrl.substring(0, daUrl.length() - pathInfo.length());
	}

	@Override
	public HttpMethod getMethod() {
		return this.httpMethod;
	}

	@Override
	public String getRemoteAddr() {
		return this.request.getRemoteAddr();
	}
}
