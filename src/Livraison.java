import java.util.ArrayList;
import java.util.HashMap;

public class Livraison {
	public Order order;
	//key : 
	private ArrayList<Warehouse> sortedWarehouses;
	private ArrayList<Integer> sortedDistances;
	//HashMap<idWarehouse, HashMap<ProductType, nbItem>>
	private HashMap<Integer, HashMap<Integer, Integer>> itemsToPick;
	
	public Livraison(Order order) {
		this.order = order;
		sortedWarehouses = new ArrayList<Warehouse>();
		sortedDistances = new ArrayList<Integer>();
		itemsToPick = new HashMap<Integer, HashMap<Integer, Integer>>();
	}
	
	public ArrayList<Warehouse> getSortedWarehouses() {
		return sortedWarehouses;
	}
	public ArrayList<Integer> getSortedDistances() {
		return sortedDistances;
	}
	public void setSortedWarehouses(ArrayList<Warehouse> sortedWarehouses) {
		this.sortedWarehouses = sortedWarehouses;
	}
	public void setSortedDistances(ArrayList<Integer> sortedDistances) {
		this.sortedDistances = sortedDistances;
	}
	public HashMap<Integer, HashMap<Integer, Integer>> getItemsToPick() {
		return itemsToPick;
	}
	public void setItemsToPick(HashMap<Integer, HashMap<Integer, Integer>> itemsToPick) {
		this.itemsToPick = itemsToPick;
	}
}
