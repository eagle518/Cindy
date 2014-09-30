package co.mindie.cindy.queue;

import java.util.List;

/**
 * Created by simoncorsin on 29/09/14.
 */
public interface WorkQueue<T> {

	List<T> dequeueItems();

	void deleteItem(T item);

}
