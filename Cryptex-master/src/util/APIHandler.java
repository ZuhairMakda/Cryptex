package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Includes methods to handle API calls
 * @author Somar Aani
 */
public class APIHandler {

	/** Enum containing all possible API calls */
	public static enum CallType {
		COIN_LIST ("https://min-api.cryptocompare.com/data/all/coinlist"),
		PRICE_MULTI_FULL ("https://min-api.cryptocompare.com/data/pricemultifull"),
		PRICE ("https://min-api.cryptocompare.com/data/price"),
		SNAP_SHOT_FULL ("https://www.cryptocompare.com/api/data/coinsnapshotfullbyid"),
		HISTO_DAY("https://min-api.cryptocompare.com/data/histoday"),
		TRADING_PAIRS("https://min-api.cryptocompare.com/data/all/exchanges");

		/** contains path to data in API */
		private final String path;

		CallType(String path) {
			this.path = path;
		}
	}


	/**
	 * Makes a request to the API using a CallType and wanted parameters
	 * @param type type of call to make
	 * @param params list of input parameters. Each input field must be followed by its parameter. Format is (tag1, input1, tag2, input2 ... tag n, input n)
	 * @return JSON object containing data from API
	 * @throws APINotRespondingException if API does not respond or responds with an error
	 * @throws IllegalArgumentException if length of parameters is not even (not every parameter tag has a corresponding input)
	 */
	public static JsonObject request(CallType type, String... params) throws APINotRespondingException {

		if(params.length % 2 != 0) {
			throw new IllegalArgumentException("Input parameters must match number of tags!");
		}

		//create url from input parameters
		String urlString = type.path + "?";
		for(int i = 0; i < params.length - 1; i++) {
			urlString += params[i] + "=" + params[i+1] + "&";
		}

		//Logger.info("Attempting to fetch " + urlString);

		InputStream input;
		URL url;

		//open stream to api
		try {
			url = new URL(urlString);
			input = url.openConnection().getInputStream();
		}
		catch(IOException e) {
			throw new APINotRespondingException(e);
		}

		//create rootObject from API JSON
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		JsonObject rootObject = new Gson().fromJson(reader, JsonObject.class);

		if(rootObject == null)
			throw new APINotRespondingException();

		//get response (not always available), throw Exception if error
		if(rootObject.has("Response") && rootObject.getAsJsonPrimitive("Response").getAsString().equals("Error"))
			throw new APINotRespondingException(rootObject.get("Message").getAsString());

		//Logger.info("API Response: " + rootObject.get("Response"));

		return rootObject;
	}

}