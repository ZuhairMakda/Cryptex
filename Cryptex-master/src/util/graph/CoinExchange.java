package util.graph;

import coin.Coin;
import coin.CoinList;
import util.APINotRespondingException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

/**
 * Class to create the Graph Object
 * Used Help from Algorithms 4th edition - SedgeWick
 * @author Zuhair Makda
 *
 */
public class CoinExchange {

	private static final int INFINITY = Integer.MAX_VALUE;
    private HashMap<Coin, Boolean> marked;  // marked
    private HashMap<Coin, Coin> edgeTo;      // edgeTo = previous edge on shortest s-v path
    private HashMap<Coin, Integer> distTo;      // distTo= number of edges shortest s-v path

    /**
     * Computes the shortest path between the source vertex Coin s
     * and every other vertex in the CoinGraph G.
     * @param G the graph
     * @param Coin s - the Coin to start computing paths from
     */
    public CoinExchange(CoinGraph G, Coin s) {
    	this.marked = new HashMap<Coin, Boolean>();
    	this.edgeTo = new HashMap<Coin, Coin>();
    	this.distTo = new HashMap<Coin, Integer>();
    	
    	for (Coin x : CoinList.getAlphabeticalList()) {
    		this.marked.put(x, false);
    		this.distTo.put(x, INFINITY);
    	}
    	
    	bfs(G, s);
    }


    /**
     * Function to handle breadth-first search from a single source
     * @param G - Graph of all the coin vertices available to trade
     * @param s - source vertex to find paths
     */
    private void bfs(CoinGraph G, Coin s) {
        LinkedList<Coin> q = new LinkedList<Coin>();
        /*for (int v = 0; v < G.V(); v++)
            distTo[v] = INFINITY;
        */
        this.distTo.put(s, 0);
        this.marked.put(s, true);
        q.add(s);

        while (!q.isEmpty()) {
            Coin v;
			v = q.poll(); 
			
			if (G.adj(v) != null) {
	            for (Coin w : G.adj(v)) {
	                if (!this.marked.get(w)) {
	                	this.edgeTo.put(w, v);
	                	this.distTo.put(w, this.distTo.get(v)+1);
	                	this.marked.put(w, true);
	                    q.add(w);
	                }
	            	
	            }
            } 
        }
    }


    /**
     * Is there a path between the source vertex  Coin s (From constructor) and Coin v
     * @param v the Coin to check if I can trade with source Coin s
     * @return true if there is a path, and false otherwise
     */
    public boolean hasPathTo(Coin v) {
        return this.marked.get(v);
    }

    /**
     * Returns the number of edges in a path between the source vertex Coin s
     * and vertex Coin v
     * @param v the Coin to find distance from source
     * @return the number of edges in a shortest path
     */
    public int distTo(int v) {
        return this.distTo.get(v);
    }

    /**
     * This function returns a Stack of all Coins in a path from Source Coin s to Coin v
     * All the possible tradeable coins in the path
     * @param Coin v to find the path
     * @return Stack of all coins in path
     */
    public Iterable<Coin> tradesTo(Coin v) {
        if (!hasPathTo(v)) return null;
        Stack<Coin> path = new Stack<Coin>();
        Coin x = null;
        for (x = v; this.distTo.get(x) != 0; x = this.edgeTo.get(x))
            path.push(x);
        path.push(x);
        return path;
    }
    
    /**
     * Takes the iterable stack of the path and returns it as a string
     * @param path - stack of Coins in the path
     * @return - string representation of Path
     */
    public String pathToString(Iterable<Coin> path) {
    	
    	String s = "";
    	if (path != null) {
	    	Stack<Coin> q = new Stack<Coin>();
	    	for (Coin x : path) {
	    		q.push(x);
	    	}
	    	while (!q.isEmpty()) {
					s = s + q.pop().toString() + "->";
	    	}
    	} else {
    		s = s + "Cannot make trade in this exchange";
    	}
    	return s;
    	
    }
    
    /**
     * Unit tests the {@code BreadthFirstPaths} data type.
     *
     * @param args the command-line arguments
     * @throws APINotRespondingException 
     */
    public static void main(String[] args) throws APINotRespondingException {
        CoinGraph G = new CoinGraph();
        System.out.println(G.toString());

        CoinExchange bfs = new CoinExchange(G, CoinList.getByCode("ELP"));

        System.out.println(bfs.hasPathTo(CoinList.getByCode("TAK")));
        System.out.println(bfs.pathToString(bfs.tradesTo(CoinList.getByCode("TAK"))));
    }

}
