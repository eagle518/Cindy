package co.mindie.cindy.webservice.resolver;

public interface IDynamicResolver<Input, Output> extends IResolver<Input, Output> {

	void appendSubResolver(IResolver resolver);

}
