package co.mindie.cindy.webservice.resolver.batch;

/**
 * Created by simoncorsin on 14/01/15.
 */
public interface BatchOperationResult<OUTPUT> {

	void onResult(OUTPUT output);

}
