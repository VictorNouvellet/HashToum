import java.util.ArrayList;
import java.util.Iterator;

public class Main {

        public static void main(String[] args) {
            // Lecture du fichier
        	//Parser.parseInput(path to the file);
        	
        	//Construction des livraisons
        	getLivraisons();
        	
        	//Construction des trajets
        	
        }
        
        private static ArrayList<Livraison> getLivraisons() {
        	ArrayList<Livraison> livraisons = new ArrayList<Livraison>();
        	
        	ArrayList<Order> orders = Parser.getOrders();
        	ArrayList<Warehouse> warehouses = Parser.getWarehouses();
        	
        	Iterator<Order> itO = orders.iterator();
        	Iterator<Warehouse> itW = warehouses.iterator();
        	
        	while (itO.hasNext()) {
        		Order currentOrder = itO.next();
        		Livraison livraison = new Livraison();
        		ArrayList<Integer> distancesLivraison = livraison.getSortedDistances();
        		ArrayList<Warehouse> warehousesLivraison = livraison.getSortedWarehouses();
        		
        		while (itW.hasNext()) {
        			Warehouse currentWarehouse = itW.next();
        			int distance = getDistance(currentOrder.getRow(), currentOrder.getColumn(), currentWarehouse.getRow(), currentWarehouse.getColumn());
        			
        			Iterator<Integer> itD = distancesLivraison.iterator();
        			
        			if (!itD.hasNext()) {
    					distancesLivraison.add(0, distance);
    					warehousesLivraison.add(0, currentWarehouse);
    					itD.next();
        			}
        			
        			while(itD.hasNext()) {
        				Integer currentDistance = itD.next();
        				if (distance <= currentDistance) {
        					int id = distancesLivraison.indexOf(currentDistance);
        					distancesLivraison.add(id, distance);
        					warehousesLivraison.add(id, currentWarehouse);
        				} else {
        					
        				}
        			}
        		}
        	}
        	
        	return livraisons;
        }
        
        private static int getDistance(int r1, int c1, int r2, int c2) {
        	int valAbsRow = Math.abs(r1 - r2);
        	int valAbsCol = Math.abs(c1 - c2);
        	
        	return (int) Math.ceil(Math.sqrt((valAbsRow*valAbsRow)+(valAbsCol*valAbsCol)));
        }
}
