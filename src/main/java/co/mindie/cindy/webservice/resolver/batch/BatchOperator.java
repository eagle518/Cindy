package co.mindie.cindy.webservice.resolver.batch;

import java.util.List;

/**
 * Created by simoncorsin on 14/01/15.
 */
public interface BatchOperator<INPUT, OUTPUT> {

	List<OUTPUT> doBatchOperation(List<INPUT> operations);

}
