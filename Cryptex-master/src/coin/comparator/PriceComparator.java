package coin.comparator;

import java.util.Comparator;

import coin.Coin;

/**
 * Comparator to compare coin price
 * @author Somar Aani
 *
 */
public class PriceComparator implements Comparator<Coin>{
	@Override
	public int compare(Coin c1, Coin c2) {
		if(c1.getPrice() < c2.getPrice())
			return 1; 
		else if (c2.getPrice() < c1.getPrice())
			return -1;
		else
			return 0;
	}
	
}
