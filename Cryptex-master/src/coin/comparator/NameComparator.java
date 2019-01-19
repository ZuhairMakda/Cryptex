package coin.comparator;

import java.util.Comparator;

import coin.Coin;

/**
 * Comparator to compare coin name (lexographically)
 * @author Somar Aani
 *
 */
public class NameComparator implements Comparator<Coin>{
	@Override
	public int compare(Coin c1, Coin c2) {
		return c1.getName().compareToIgnoreCase(c2.getName());
	}
}
