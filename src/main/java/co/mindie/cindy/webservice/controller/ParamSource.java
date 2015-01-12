package co.mindie.cindy.webservice.controller;

/**
 * Created by simoncorsin on 12/01/15.
 */
public enum ParamSource {

	/**
	 * The parameter source will be determined automatically.
	 */
	AUTO,

	/**
	 * The parameter value will be fetched from the request body
	 */
	BODY,

	/**
	 * The parameter value will be fetched from the query parameters
	 */
	QUERY,

	/**
	 * The parameter value will be fetched from the url
	 */
	URL

}
