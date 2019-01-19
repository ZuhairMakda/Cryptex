package util.graph;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.Iterator;

import coin.Coin;
import coin.CoinList;
import util.APIHandler;
import util.APINotRespondingException;
import util.Logger;
import util.APIHandler.CallType;

/**
 * Class to create the Graph Object
 * @author Zuhair Makda
 *
 */
public class CoinGraph {

    private final int V;
    private int E;
    private HashMap<Coin, HashSet<Coin>> adj;
    
    /**
     * @Brief - Take in the CoinList for trade pairs 
     * @Details - Add Edges for each trade pair
     * @throws APINotRespondingException 
     * Take in CoinList
     *
     */
    public CoinGraph() throws APINotRespondingException {
        this.V = 0;
        this.E = 0;
        adj = new HashMap<Coin, HashSet<Coin>>();
        
        	if (!CoinList.isInitialized()) {
        		CoinList.init();
        	}
			

			JsonObject mainObj = APIHandler.request(CallType.TRADING_PAIRS);
			//JsonObject pairs = mainObj.getAsJsonObject("HitBTC"); 261 Vertices
			JsonObject pairs = mainObj.getAsJsonObject("Cryptsy"); //278 Vertices
			for(Entry<String, JsonElement> e : pairs.entrySet()) {
				addEdges(e);
			}
		
        
    }


    /**
     * Returns the number of vertices in this graph.
     *
     * @return the number of vertices in this graph
     */
    public int V() {
        return this.adj.size();
    }

    /**
     * Returns the number of edges in this graph.
     *
     * @return the number of edges in this graph
     */
    public int E() {
        return E;
    }

    /**
     * Adds the undirected edge from from tradeable pairs in both directions
     *
     * @param  Entry e - Contains CoinFrom: CoinsTo
     */
    private void addEdges(Entry<String, JsonElement> e) {
        
        Coin coinFrom;
        coinFrom = CoinList.getByCode(e.getKey());
        if ( coinFrom != null) {
        	if (!this.adj.containsKey(coinFrom)) {
        		this.adj.put( coinFrom, new HashSet<Coin>());
        	}
		JsonArray coins = e.getValue().getAsJsonArray();
		for(JsonElement k : coins)
			if (CoinList.getByCode(k.getAsString()) != null) {
				this.adj.get(coinFrom).add( CoinList.getByCode(k.getAsString()));
				if (!this.adj.containsKey( CoinList.getByCode(k.getAsString()))) {
	        		this.adj.put(  CoinList.getByCode(k.getAsString()), new HashSet<Coin>());
	        		this.adj(CoinList.getByCode(k.getAsString())).add(coinFrom);
	        	} else {
	        		this.adj(CoinList.getByCode(k.getAsString())).add(coinFrom);
	        	}
				this.E++;
			}
		//System.out.println(coinFrom.getCode() + "--> " + coins.toString());
        }
    }


    /**
     * Returns the Coins adjacent to vertex at Coin v.
     *
     * @param  Coin v to find tradeable coins
     * @return the vertices adjacent to vertex at Coin v
     */
    public HashSet<Coin> adj(Coin v) {
        return this.adj.get(v);
    }

    /**
     * Returns the degree of Coin
     *
     * @param  v the the Coin
     * @return the degree of the Coin v
     */
    public int degree(Coin v) {
        return this.adj.get(v).size();
    }


    /**
     * Returns a string representation of this graph.
     *
     * @return String of Coins with tradeable partner Coins
     */
    public String toString() {
    	String s = "";
    	
    	Iterator<Map.Entry<Coin, HashSet<Coin>>> itr1 = this.adj.entrySet().iterator();

    	while (itr1.hasNext()) {
    	    Map.Entry<Coin, HashSet<Coin>> entry = itr1.next();
    	    s = s + entry.getKey() + "-->";
    	    Iterator<Coin> itr2 = entry.getValue().iterator();

    	    while (itr2.hasNext()) {
    	        s = s + itr2.next() + ", ";
    	    }
    	    s = s + "\n";
    	}
    	
        return s;
    }


    /**
     * Testing Graph Object
     *
     * @param args the command-line arguments
     * @throws APINotRespondingException 
     */
    public static void main(String[] args) throws APINotRespondingException {
        CoinGraph tradeables = new CoinGraph();
        
        System.out.println(tradeables.toString());
        
        Coin LTC = CoinList.getByCode("LTC");
        Coin BTC = CoinList.getByCode("BTC");
        Coin XRP = CoinList.getByCode("XRP");

        
        HashSet<Coin> ltc = tradeables.adj(LTC);
        HashSet<Coin> xrp = tradeables.adj(XRP);
        HashSet<Coin> btc = tradeables.adj(BTC);
        int degXRP = tradeables.degree(XRP);
        int degBTC = tradeables.degree(BTC);
        int degLTC = tradeables.degree(LTC);
        
        System.out.println(LTC.toString() + "-->" + ltc + ", " + degLTC);
        System.out.println(XRP.toString() + "-->" + xrp + ", " + degXRP);
        System.out.println(BTC.toString() + "-->" + btc + ", " + degBTC);
        
        System.out.println(tradeables.V());
    }

}