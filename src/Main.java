import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Main {

        public static void main(String[] args) {

			// Lecture du fichier
            Parser.parseInput("./in/busy_day.in");
            //Parser.displayDrones();
            //Parser.displayOrders();
            //Parser.displayWarehouses();
        	
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
        		Livraison livraison = new Livraison(currentOrder);
        		ArrayList<Integer> distancesLivraison = livraison.getSortedDistances();
        		ArrayList<Warehouse> warehousesLivraison = livraison.getSortedWarehouses();
        		
        		while (itW.hasNext()) {
        			Warehouse currentWarehouse = itW.next();
        			int distance = getDistance(currentOrder.getRow(), currentOrder.getColumn(), currentWarehouse.getRow(), currentWarehouse.getColumn());
        			
        			Iterator<Integer> itD = distancesLivraison.iterator();
        			
        			if (!itD.hasNext()) {
    					distancesLivraison.add(distance);
    					warehousesLivraison.add(currentWarehouse);
    					itD.next();
        			}
        			
        			while(itD.hasNext()) {
        				Integer currentDistance = itD.next();
        				if (distance <= currentDistance) {
        					int id = distancesLivraison.indexOf(currentDistance);
        					distancesLivraison.add(id, distance);
        					warehousesLivraison.add(id, currentWarehouse);
        				} else if(!itD.hasNext()) {
        					distancesLivraison.add(distance);
        					warehousesLivraison.add(currentWarehouse);
        				}
        			}	
        		}
        		livraison.setSortedDistances(distancesLivraison);
        		livraison.setSortedWarehouses(warehousesLivraison);
        		
        		//---------------
        		
        		ArrayList<Integer> itemsOrder = currentOrder.getItems();
        		Iterator<Integer> itItemsOrder = itemsOrder.iterator();
        		
        		ArrayList<Warehouse> sortedWarehouses2 = livraison.getSortedWarehouses();
	    		Iterator<Warehouse> itW2 = sortedWarehouses2.iterator();
	    		
	    		HashMap<Integer, HashMap<Integer, Integer>> tempWarehousesList = new HashMap<Integer, HashMap<Integer, Integer>>();
	    		Integer idWarehouse = 0;
	    		while (itW2.hasNext()) {
	    			Warehouse currentWarehouse = itW2.next();
	    			HashMap<Integer, Integer> tempWarehouse = new HashMap<Integer, Integer>();
	    			
	        		Integer idItem = 0;
	        		while(itItemsOrder.hasNext()) {
	        			
	        			Integer currentQtItemOrder = itItemsOrder.next();
	    			
	        			if (currentWarehouse.checkAvailable(idItem, currentQtItemOrder)) {
	        				tempWarehouse.put(idItem, currentQtItemOrder);
	        				currentWarehouse.removeItems(idItem, currentQtItemOrder);
	        				//currentOrder.removeItems(idItem, currentQtItemOrder);
	        				itemsOrder.set(idItem, itemsOrder.get(idItem)-currentQtItemOrder);
	        			}
	        			idItem++;
	        		}
	        		
	        		tempWarehousesList.put(idWarehouse, tempWarehouse);
	        		idWarehouse++;
	        	}
	    		livraison.setItemsToPick(tempWarehousesList);
	    		livraisons.add(livraison);
        	}
        	return livraisons;
        }
        
        private static int getDistance(int r1, int c1, int r2, int c2) {
        	int valAbsRow = Math.abs(r1 - r2);
        	int valAbsCol = Math.abs(c1 - c2);
        	
        	return (int) Math.ceil(Math.sqrt((valAbsRow*valAbsRow)+(valAbsCol*valAbsCol)));
        }
        
        private static ArrayList<String> dispatch(ArrayList<Livraison> livraisons, ArrayList<Drone> drones, ArrayList<int> poids, int maxTime, int chargementMax) {
        	ArrayList<String> res = new ArrayList<String>();
        	int time = 0;
        	while (time < maxTime) {
        		for(int i = 0; i < drones.size(); i++) {
        			if(drones.get(i).isEmpty()) {
        				if(livraisons.size()>0) {
	        				int idProduit = 0;
	        				while(idProduit < livraisons.get(0).order.items.size() && livraisons.get(0).order.items.get(idProduit) == 0) {
	        					idProduit++;
	        				}
	        				if(idProduit < livraisons.get(0).order.items.size()){
	        					int nbProduits = livraisons.get(0).order.items.get(idProduit);
	        					while(nbProduits*poids.get(idProduits) > chargementMax){
	        						nbProduits--;
	        					}
	        					livraisons.get(0).order.items.set(idProduits, livraisons.get(0).order.items.get(idProduit) - nbProduits);
	        					drones.get(i).loadItem(idProduit, nbProduits, Warehouse wh)
	        				}
	        				
	        				
        				}
        			}
        		}
        		//updateDrones();
        		time++;
        	}
        	return res;
        }
        
}
