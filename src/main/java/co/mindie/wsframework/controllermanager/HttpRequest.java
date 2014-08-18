/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.serveradapter
// Request.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 1, 2014 at 5:59:51 PM
////////

package co.mindie.wsframework.controllermanager;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

import co.mindie.wsframework.automapping.HttpMethod;

public interface HttpRequest {

	Map<String, String[]> getQueryParameters();

	Map<String, List<FileItem>> getBodyParameters();

	InputStream getBody();

	String getPathInfo();

	String getServerUrl();

	HttpMethod getMethod();

	String getRemoteAddr();

}
