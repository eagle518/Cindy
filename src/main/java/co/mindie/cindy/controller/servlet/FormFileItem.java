/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller.servlet
// FormFileItem.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 5, 2014 at 4:46:19 PM
////////

package co.mindie.cindy.controller.servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemHeaders;

import java.io.*;

public class FormFileItem implements FileItem {

	////////////////////////
	// VARIABLES
	////////////////

	private static final long serialVersionUID = 4204876205064815962L;

	private String name;
	private String value;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public FormFileItem(String name, String value) {
		this.name = name;
		this.value = value;
	}

	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	@Override
	public FileItemHeaders getHeaders() {
		return null;
	}

	@Override
	public void setHeaders(FileItemHeaders headers) {
		throw new RuntimeException("Invalid operation on FormFileItem");
	}

	@Override
	public InputStream getInputStream() throws IOException {
		throw new RuntimeException("Invalid operation on FormFileItem");
	}

	@Override
	public String getContentType() {
		return null;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean isInMemory() {
		return true;
	}

	@Override
	public long getSize() {
		return this.value.length();
	}

	@Override
	public byte[] get() {
		try {
			return this.value.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getString(String encoding)
			throws UnsupportedEncodingException {
		return this.value;
	}

	@Override
	public String getString() {
		return this.value;
	}

	@Override
	public void write(File file) throws Exception {
		throw new RuntimeException("Invalid operation on FormFileItem");
	}

	@Override
	public void delete() {
		throw new RuntimeException("Invalid operation on FormFileItem");
	}

	@Override
	public String getFieldName() {
		return this.name;
	}

	@Override
	public void setFieldName(String name) {
		throw new RuntimeException("Invalid operation on FormFileItem");
	}

	@Override
	public boolean isFormField() {
		return true;
	}

	@Override
	public void setFormField(boolean state) {
		throw new RuntimeException("Invalid operation on FormFileItem");
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		throw new RuntimeException("Invalid operation on FormFileItem");
	}
}
