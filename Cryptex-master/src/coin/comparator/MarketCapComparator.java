package coin.comparator;

import java.util.Comparator;

import coin.Coin;


/**
 * Comparator to compare coin market cap
 * @author Somar Aani
 *
 */
public class MarketCapComparator implements Comparator<Coin> {
	@Override
	public int compare(Coin c1, Coin c2) {
		if(c1.getMarketCap() < c2.getMarketCap())
			return 1; 
		else if (c2.getMarketCap() < c1.getMarketCap())
			return -1;
		else
			return 0;
	}

}
