package coin;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import coin.comparator.DailyChangeComparator;
import coin.comparator.InternalOrderComparator;
import coin.comparator.MarketCapComparator;
import coin.comparator.NameComparator;
import coin.comparator.PriceComparator;
import util.APIHandler;
import util.Logger;
import util.search.Search;
import util.APIHandler.CallType;
import util.sort.QuickSort;
import util.APINotRespondingException;

/**
 * Coin List that stores all coins avaiable
 * @author Somar Aani
 */

public class CoinList{

	/** Max number of coins that can be initialized with a single API call **/
	public static final int MAX_MARKET_INPUT = 60;

	private static SortOrder sortOrder = SortOrder.NOT_SORTED; //sort order currently used

	private static Coin[] list; //stores the actual coinlist
	private static Coin[] alphabetical; //stores the coins in alphabetical order (for searching)

	private static boolean isInit = false; //if coinlist is initialized (coin objects with names and code)
	private static boolean allMarketLoaded = false; //if all coins added to market data

	private static int marketLoadedTill = 0; //number of coins that have loaded market data
	
	private static HashMap<String, Coin> coinST; 
	
	/**
	 * Initializes the coin list by making an API call
	 * @throws APINotRespondingException if API does not respond or responds with an error
	 */
	public static void init() throws APINotRespondingException {
		Logger.info("Initializing coin list ...");

		//gets coin list as JsonObject via API call
		JsonObject mainObj = APIHandler.request(CallType.COIN_LIST).get("Data").getAsJsonObject();

		Set<Entry<String, JsonElement>> dataMap = mainObj.entrySet();
		coinST = new HashMap<>();

		list = new Coin[dataMap.size()];
		alphabetical = new Coin[dataMap.size()];

		//initializes all coins in list using entry set
		int i = 0;
		for(Entry<String, JsonElement> e : dataMap) {
			Coin c = new Coin(e.getValue().getAsJsonObject());
			list[i] = c;
			alphabetical[i++] = c;
			coinST.put(c.getCode(), c);
		}

		isInit = true;
		Logger.info("Coin list successfully initialized - " + list.length + " coins");

		QuickSort.sort(list, new InternalOrderComparator());
		sortOrder = SortOrder.INTERNAL_ID;

		QuickSort.sort(alphabetical, new NameComparator());
	}

	/**
	 * Checks whether the coin list has been successfully initialized
	 * @return if coin list was successfully created
	 */
	public static boolean isInitialized() {
		return isInit;
	}

	/**
	 * Whether the entire list has been initialized with the market data
	 * @return if list is all market data is initialized
	 */
	public static boolean marketDataFullyLoaded() {
		return allMarketLoaded;
	}

	/**
	 * Resets market data, so it can be loaded again
	 */
	public static void resetMarketData() {
		marketLoadedTill = 0;
		allMarketLoaded = false;
	}

	/**
	 * Loads market data for all coins in the list, relating to relCoinCode
	 * @param relCoinCode coin symbol
	 * @throws APINotRespondingException if API does not respond or responds with an error
	 */
	public static void loadAllMarketData(String relCoinCode) throws APINotRespondingException {

		//loads market data MAX_MARKET_INPUT coins at a time, to speed up the process

		if(!isInit)
			throw new IllegalStateException("CoinList must be initialized!");

		Logger.info("Loading market data relative to " + relCoinCode);

		String param = "";
		int i = 0;

		//divide into MAX_MARKET_INPUT length blocks
		for(; i < list.length/MAX_MARKET_INPUT; i++) {

			//create input parameter for block
			for(int j = i * MAX_MARKET_INPUT; j < MAX_MARKET_INPUT * (i + 1); j++) {
				param += list[j].getCode() + ",";
			}

			//set coin data for the block
			setCoinMarketData(i * MAX_MARKET_INPUT, MAX_MARKET_INPUT * (i + 1), param, relCoinCode);
			param = "";
		}

		//creates parameter for any left over coins
		for(int j = i * MAX_MARKET_INPUT; j < list.length; j++)
			param += list[j].getCode() + ",";

		//set data for left over coins
		setCoinMarketData(i * MAX_MARKET_INPUT, list.length, param, relCoinCode);
		Logger.info("Market data successfully loaded");
	}

	/**
	 * Loads the next {@code i} coin market data
	 * @param i number of coins to load
	 * @param relCoinCode coin to load market relative to
	 * @throws APINotRespondingException if API does not respond or responds with an error
	 */
	public static void loadNextMarketData(int i, String relCoinCode) throws APINotRespondingException {
		if(!isInit)
			throw new IllegalStateException("CoinList must be initialized!");

		if(marketLoadedTill + i > list.length - 1) {
			i = list.length - marketLoadedTill;
			allMarketLoaded = true;
		}

		String param = "";
		LinkedList<Integer> digitCoins = new LinkedList<>();

		for(int j = marketLoadedTill; j < marketLoadedTill + i; j++){
			if(Character.isDigit(list[j].getCode().charAt(0))) {
				digitCoins.add(j);
			}else
				param += list[j].getCode() + ",";
		}

		//sets most coins
		setCoinMarketData(marketLoadedTill, marketLoadedTill + i + 1, param, relCoinCode);

		//sets coins that start with a digit - they are in a different order
		if(digitCoins.size() > 0)
			setCoinMarketData(digitCoins, relCoinCode);

		marketLoadedTill += i;
	}

	//helper method, sets coin market data for specific coins in coins array
	private static void setCoinMarketData(LinkedList<Integer> coins, String relCoinCode) throws APINotRespondingException {
		String param = "";
		for(int c : coins) {
			param += list[c].getCode() + ",";
		}

		//get root object from API
		JsonObject rootObj = APIHandler.request(CallType.PRICE_MULTI_FULL, "fsyms", param, "tsyms", relCoinCode);

		JsonObject rawObj = null;
		JsonObject dispObj = null;

		try {
			rawObj = rootObj.get("RAW").getAsJsonObject(); //for raw data
			dispObj = rootObj.get("DISPLAY").getAsJsonObject(); //for stylized display data
		}catch(NullPointerException e) {
			for(int c : coins) {
				list[c].setMarketCap(Double.NEGATIVE_INFINITY);
				list[c].setPrice(Double.NEGATIVE_INFINITY);
				list[c].setDailyChangePercent(Double.NEGATIVE_INFINITY);

				list[c].setDisplayDailyChangePercent("-");
				list[c].setDisplayMarketCap("-");
				list[c].setDisplayPrice("-");
			}
			return;
		}

		JsonObject currCoinObj;
		Iterator<Entry<String, JsonElement>> iterRaw = rawObj.entrySet().iterator();
		Iterator<Entry<String, JsonElement>> iterDisp = dispObj.entrySet().iterator();

		Entry<String, JsonElement> currRaw = iterRaw.next();
		Entry<String, JsonElement> currDisp = iterDisp.next();

		while(currRaw != null) {
			Coin c = getByName(currRaw.getKey());
      
			//set raw data
			currCoinObj = currRaw.getValue().getAsJsonObject().getAsJsonObject(relCoinCode);

			c.setMarketCap(currCoinObj.getAsJsonPrimitive("MKTCAP").getAsDouble());
			c.setPrice(currCoinObj.getAsJsonPrimitive("PRICE").getAsDouble());

			if(currCoinObj.get("CHANGEPCT24HOUR").isJsonNull())
				c.setDailyChangePercent(Double.NaN);
			else
				c.setDailyChangePercent(currCoinObj.getAsJsonPrimitive("CHANGEPCT24HOUR").getAsDouble());

			//set disp data
			currCoinObj = currDisp.getValue().getAsJsonObject().getAsJsonObject(relCoinCode);

			c.setDisplayMarketCap(currCoinObj.getAsJsonPrimitive("MKTCAP").getAsString());
			c.setDisplayPrice(currCoinObj.getAsJsonPrimitive("PRICE").getAsString());
			c.setDisplayDailyChangePercent(currCoinObj.getAsJsonPrimitive("CHANGEPCT24HOUR").getAsString() + "%");

			if(iterRaw.hasNext()) {
				currRaw = iterRaw.next();
				currDisp = iterDisp.next();
			}else {
				currRaw = null;
			}
		}

	}

	//helper method, assigns all coin data from start to end in the coin list
	private static void setCoinMarketData(int start, int end, String param, String relCoinCode) throws APINotRespondingException {
		//get root object from API
		JsonObject rootObj = APIHandler.request(CallType.PRICE_MULTI_FULL, "fsyms", param, "tsyms", relCoinCode);


		JsonObject rawObj = null;
		JsonObject dispObj = null;

		try {
			rawObj= rootObj.get("RAW").getAsJsonObject(); //for raw data
			dispObj= rootObj.get("DISPLAY").getAsJsonObject(); //for stylized display data
		}catch(NullPointerException e) {
			for(int i = start; i < end - 1; i++) {
				list[i].setMarketCap(Double.NEGATIVE_INFINITY);
				list[i].setPrice(Double.NEGATIVE_INFINITY);
				list[i].setDailyChangePercent(Double.NEGATIVE_INFINITY);

				list[i].setDisplayDailyChangePercent("-");
				list[i].setDisplayMarketCap("-");
				list[i].setDisplayPrice("-");
			}
			return;
		}

		JsonObject currCoinObj;
		Iterator<Entry<String, JsonElement>> iterRaw = rawObj.entrySet().iterator();
		Iterator<Entry<String, JsonElement>> iterDisp = dispObj.entrySet().iterator();

		Entry<String, JsonElement> currRaw = iterRaw.next();
		Entry<String, JsonElement> currDisp = iterDisp.next();

		for(int i = start; i < end - 1; i++) {
			if(list[i].getCode().equals(currRaw.getKey())) {

				//set raw data
				currCoinObj = currRaw.getValue().getAsJsonObject().getAsJsonObject(relCoinCode);

				list[i].setMarketCap(currCoinObj.getAsJsonPrimitive("MKTCAP").getAsDouble());
				list[i].setPrice(currCoinObj.getAsJsonPrimitive("PRICE").getAsDouble());

				if(currCoinObj.get("CHANGEPCT24HOUR").isJsonNull())
					list[i].setDailyChangePercent(Double.NEGATIVE_INFINITY);
				else
					list[i].setDailyChangePercent(currCoinObj.getAsJsonPrimitive("CHANGEPCT24HOUR").getAsDouble());


				//set disp data
				currCoinObj = currDisp.getValue().getAsJsonObject().getAsJsonObject(relCoinCode);

				list[i].setDisplayMarketCap(currCoinObj.getAsJsonPrimitive("MKTCAP").getAsString());
				list[i].setDisplayPrice(currCoinObj.getAsJsonPrimitive("PRICE").getAsString());
				list[i].setDisplayDailyChangePercent(currCoinObj.getAsJsonPrimitive("CHANGEPCT24HOUR").getAsString() + "%");

				if(iterRaw.hasNext()) {
					currRaw = iterRaw.next();
					currDisp = iterDisp.next();
				}
			} else {
				list[i].setMarketCap(Double.NEGATIVE_INFINITY);
				list[i].setPrice(Double.NEGATIVE_INFINITY);
				list[i].setDailyChangePercent(Double.NEGATIVE_INFINITY);

				list[i].setDisplayDailyChangePercent("-");
				list[i].setDisplayMarketCap("-");
				list[i].setDisplayPrice("-");
			}
		}
	}

	/**
	 * Finds a specific coin using its name
	 * @param code 3 character code associated with coin
	 * @return Coin object representing the specific cryptocurrency
	 */
	public static Coin getByName(String code) {		
		if(!isInit)
			throw new IllegalStateException("CoinList must be initialized!");
		
		return Search.searchCoin(code);
	}
	
		/**
	 * Finds a specific coin using its code
	 * @param code code of coin to find
	 * @return coin with code {@code code}
	 */
	public static Coin getByCode(String code) {
		if(!isInit)
			throw new IllegalStateException("CoinList must be initialized!");

		return coinST.get(code);
	}

	/**
	 * Gets reference to the coin list
	 * @return coin list
	 */
	public static Coin[] getList() {
		if(!isInit)
			throw new IllegalStateException("CoinList must be initialized!");

		return list;
	}

	/**
	 * Gets reference to a coin list lexographically sorted by coin name
	 * @return coin list sorted by name
	 */
	public static Coin[] getAlphabeticalList() {
		if(!isInit)
			throw new IllegalStateException("CoinList must be initialized!");

		return alphabetical;
	}

	/**
	 * Gets the current sort order
	 * @return SortOrder enum of current sorting order
	 */
	public static SortOrder getSortOrder() {
		return CoinList.sortOrder;
	}

	/**
	 * Sets the sort order
	 * @param s SortOrder enum of wanted sort order
	 */
	public static void setSortOrder(SortOrder s) {
		CoinList.sortOrder = s;
	}

	/**
	 * Gets the correct comparator for the given SortOrder
	 * @param s SortOrder to get the comparator for
	 * @return Comparator object for the given SortOrder
	 */
	private static Comparator<Coin> getComparator(SortOrder s) {
		switch(s) {
			case ALPHABETICAL: return new NameComparator();
			case PRICE: return new PriceComparator();
			case MKTCAP: return new MarketCapComparator();
			case CHANGE: return new DailyChangeComparator();
			default:
				return null;
		}
	}

	/**
	 * Sorts list based on SortOrder s
	 * @param s SortOrder enum to specify how to sort
	 */
	public static void sort(SortOrder s) {
		if(s == SortOrder.NOT_SORTED) return;

		QuickSort.sort(list, getComparator(s));
		sortOrder = s;
	}
}