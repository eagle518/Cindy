/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.queue
// IQueue.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Dec 30, 2013 at 3:49:31 PM
////////

package co.mindie.cindy.worker.queue;

public interface MessageQueueHandler {

	void enqueueMessage(String queueUrl, String messageBody);

}
