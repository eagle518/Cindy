/////////////////////////////////////////////////
// Project : WSFramework
// Package : co.mindie.wsframework.queue
// IQueue.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Dec 30, 2013 at 3:49:31 PM
////////

package co.mindie.wsframework.queue;

public interface IMessageQueueHandler {

	void enqueueMessage(String queueUrl, String messageBody);

}
