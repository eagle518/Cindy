/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.serveradapter
// Request.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 1, 2014 at 5:59:51 PM
////////

package co.mindie.cindy.controllermanager;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import co.mindie.cindy.automapping.HttpMethod;
import org.apache.commons.fileupload.FileItem;

public interface HttpRequest {

	Map<String, String[]> getQueryParameters();

	Map<String, List<FileItem>> getBodyParameters();

	InputStream getBody();

	String getPathInfo();

	String getServerUrl();

	HttpMethod getMethod();

	String getRemoteAddr();

}
