package co.mindie.cindy.resolver;

public interface IDynamicResolver<Input, Output> extends IResolver<Input, Output> {

	void appendSubResolver(IResolver resolver);

}
