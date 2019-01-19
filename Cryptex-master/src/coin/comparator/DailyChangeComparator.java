package coin.comparator;

import java.util.Comparator;

import coin.Coin;

/**
 * Comparator to compare daily change percent
 * @author Somar Aani
 *
 */
public class DailyChangeComparator implements Comparator<Coin> {
	@Override
	public int compare(Coin c1, Coin c2) {
		if(c1.getDailyChangePercent() < c2.getDailyChangePercent())
			return 1; 
		else if (c2.getDailyChangePercent() < c1.getDailyChangePercent())
			return -1;
		else
			return 0;
	}
}
