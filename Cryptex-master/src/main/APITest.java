package main;

import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import coin.Coin;
import coin.CoinList;
import util.APIHandler;
import util.APINotRespondingException;
import util.Logger;
import util.APIHandler.CallType;

public class APITest {
	public static void main(String args[]) {
		try {
			//inialize coinlist
			CoinList.init();

			//this code here prints all of the pairs 
			JsonObject mainObj = APIHandler.request(CallType.TRADING_PAIRS);
			JsonObject pairs = mainObj.getAsJsonObject("Cryptsy");
			for(Entry<String, JsonElement> e : pairs.entrySet()) {
				System.out.print(e.getKey() + ": ");
				JsonArray coins = e.getValue().getAsJsonArray();
				for(JsonElement k : coins)
					System.out.print(k.getAsString() + ", ");
				System.out.println();
			}
			

			
		} catch (APINotRespondingException e) {
			Logger.error("API Not responding");
			e.printStackTrace();
		}
	}

}
