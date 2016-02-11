import java.util.ArrayList;

public class Livraison {
	private Order order;
	//key : 
	private ArrayList<Warehouse> sortedWarehouses;
	private ArrayList<Integer> sortedDistances;
	
	public ArrayList<Warehouse> getSortedWarehouses() {
		return sortedWarehouses;
	}
	public ArrayList<Integer> getSortedDistances() {
		return sortedDistances;
	}
}
