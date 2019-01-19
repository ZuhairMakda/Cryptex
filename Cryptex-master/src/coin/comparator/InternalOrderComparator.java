package coin.comparator;

import java.util.Comparator;

import coin.Coin;

/**
 * Comparator to compare internal order
 * @author Somar Aani
 *
 */
public class InternalOrderComparator implements Comparator<Coin> {
	@Override
	public int compare(Coin c1, Coin c2) {
		if(c1.getInternalSortOrder() > c2.getInternalSortOrder())
			return 1; 
		else if (c2.getInternalSortOrder() > c1.getInternalSortOrder())
			return -1;
		else
			return 0;
	}
}
