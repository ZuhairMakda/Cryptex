package util.search;

import java.util.ArrayList;
import java.util.Comparator;

import coin.Coin;
import coin.CoinList;
import coin.SortOrder;
import coin.comparator.NameComparator;
import util.APINotRespondingException;
import util.sort.QuickSort;

/**
 * Class to handle binary search
 * @author Juwon
 *
 */
public class Search {

	/**
	 * Will search the given coin list using a String 
	 * @param String s the substring to search for
	 * @param list the coinList which to search against
	 * @return a list of Coin Objects that contain String s as a substring (used for popup)
	 */
	public static Coin[] search(Coin[] list, String symbol) {
		ArrayList<Coin> coins = new ArrayList<Coin>(); //Search results from Name
		//ArrayList<Coin> coins2 = new ArrayList<Coin>(); // Search results from Code
		
		int i = binSearch(list,0,list.length, symbol);
		//int k = binSearch2(list, 0, list.length, symbol);
		
		if(i != -1) {
			//shows up to a max of 6, the next entries containing user's search. (List is pre-sorted alphabetically)
			for (int j = i;; j++) {
				if (list[j].getName().toLowerCase().contains(symbol.toLowerCase()))
					coins.add(list[j]);
				else {
					break;
				}
			}
			
			for (int j = i-1;; j--) {
				if (list[j].getName().toLowerCase().contains(symbol.toLowerCase()))
					coins.add(list[j]);
				else {
					break;
				}
			}
		}
		
		/*if(k != -1) {
			for (int j = k;; j++) {
				if (list[j].getCode().toLowerCase().contains(symbol.toLowerCase()))
					coins.add(list[j]);
				else {
					break;
				}
			}
			
			for (int j = i-1;; j--) {
				if (list[j].getCode().toLowerCase().contains(symbol.toLowerCase()))
					coins.add(list[j]);
				else {
					break;
				}
			}
		}*/
		
		//So now i have a list of COINS from searching by name, and another list of COINS searching by Code
		// Going to Remove all duplicates coins, join them, and sort them.
		//coins.removeAll(coins2);
		//coins.addAll(coins2);
		
		Coin[] arr = new Coin[coins.size()];
		arr = coins.toArray(arr);
		QuickSort.sort(arr, new NameComparator());
		
		return arr;
	}
	
	/**
	 * The second search that returns just 1 Coin Object
	 * @param list
	 * @param symbol
	 * @return a List containing Coins that are similar to the Name/Code searched
	 */
	public static Coin searchCoin(String symbol) {
		Coin[] coin;
		coin = search(CoinList.getAlphabeticalList(),symbol);
		return coin[0];
	}
	
	
	private static int binSearch(Coin[] list ,int start, int end, String s) {
		s = s.toLowerCase();
		int low = start;
		int high = end;
		while (low <= high) {
			int mid = low + (high - low)/2;
			String midVal = list[mid].getName();
			
			if(midVal.toLowerCase().contains(s.toLowerCase())) {
				return mid; 
			}
			else {
				if (midVal.compareToIgnoreCase(s) < 0) {
					low = mid + 1;
				}
				else {
					high = mid -1;
				}
			}
		}
		return -1; // cant find it
		
	}
	
	//This one is for the Coin Code
	private static int binSearch2(Coin[] list ,int start, int end, String s) {
		int low = start;
		int high = end;
		while (low <= high) {
			int mid = (low+high)/2;
			String midVal = list[mid].getCode();
			
			if (midVal.compareToIgnoreCase(s) < 0) {
				low = mid + 1;
			}
			else if(midVal.compareToIgnoreCase(s) > 0) {
				high = mid -1;
			}
			else {
				return mid; //index of found key
			}
		}
		return -1; // index of insertion point
		
	}
	
	
	//add private helped / sorting methods here
}

