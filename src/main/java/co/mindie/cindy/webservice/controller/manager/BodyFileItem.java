/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.controller.servlet
// BodyFileItem.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Aug 5, 2014 at 4:46:19 PM
////////

package co.mindie.cindy.webservice.controller.manager;

import co.mindie.cindy.core.tools.io.InputStreamCreator;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemHeaders;

import java.io.*;

public class BodyFileItem implements FileItem {

	////////////////////////
	// VARIABLES
	////////////////

	private static final long serialVersionUID = 4204876205064815962L;

	final private String name;
	final private String contentType;
	final private boolean isInMemory;
	final private InputStreamCreator inputStreamCreator;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public BodyFileItem(String name, String contentType, boolean isInMemory, InputStreamCreator inputStreamCreator) {
		this.name = name;
		this.contentType = contentType;
		this.isInMemory = isInMemory;
		this.inputStreamCreator = inputStreamCreator;
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
		throw new RuntimeException("Invalid operation on BodyFileItem");
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return this.inputStreamCreator.createInputStream();
	}

	@Override
	public String getContentType() {
		return this.contentType;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean isInMemory() {
		return this.isInMemory;
	}

	@Override
	public long getSize() {
		return this.inputStreamCreator.getInputStreamLength();
	}

	@Override
	public byte[] get() {
		throw new RuntimeException("Invalid operation on BodyFileItem");
	}

	@Override
	public String getString(String encoding)
			throws UnsupportedEncodingException {
		throw new RuntimeException("Invalid operation on BodyFileItem");
	}

	@Override
	public String getString() {
		throw new RuntimeException("Invalid operation on BodyFileItem");
	}

	@Override
	public void write(File file) throws Exception {
		throw new RuntimeException("Invalid operation on BodyFileItem");
	}

	@Override
	public void delete() {
		throw new RuntimeException("Invalid operation on BodyFileItem");
	}

	@Override
	public String getFieldName() {
		return this.name;
	}

	@Override
	public void setFieldName(String name) {
		throw new RuntimeException("Invalid operation on BodyFileItem");
	}

	@Override
	public boolean isFormField() {
		return false;
	}

	@Override
	public void setFormField(boolean state) {
		throw new RuntimeException("Invalid operation on BodyFileItem");
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		throw new RuntimeException("Invalid operation on BodyFileItem");
	}
}
