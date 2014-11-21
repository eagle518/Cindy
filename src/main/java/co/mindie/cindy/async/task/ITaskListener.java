/////////////////////////////////////////////////
// Project : SCJavaTools
// Package : me.corsin.javatools.task
// ITaskListener.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 19, 2013 at 3:16:12 PM
////////

package co.mindie.cindy.async.task;

public interface ITaskListener<T> {
	
	void onCompleted(Object taskCreator, T task);

}
