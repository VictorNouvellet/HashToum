import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Main {

        public static void main(String[] args) {

			// Lecture du fichier
            Parser.parseInput("./in/busy_day.in");
            //Parser.parseInput("./in/mother_of_all_warehouses.in");
            //Parser.parseInput("./in/redundancy.in");

            //Parser.displayDrones();
            //Parser.displayOrders();
            //Parser.displayWarehouses();
        	
        	//Construction des livraisons
            ArrayList<String> commands = getLivraisons();
            try(  PrintWriter out = new PrintWriter( "busy_day.out" )  ){
                out.println(commands.size());
                for (String command:commands
                     ) {
                    out.println( command );
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        //Get Livraisons
        private static ArrayList<String> getLivraisons() {
            ArrayList<Drone> drones = Parser.getDrones();
            ArrayList<Order> orders = Parser.getOrders();
            ArrayList<Warehouse> warehouses = Parser.getWarehouses();
            int maxPL = Parser.maxPayload;
            ArrayList<Integer> weights= Parser.weights;

            ArrayList<String> commands = new ArrayList<String>();
            int droneUsed = 0;

            //Orders
            for (int o=0; o<orders.size(); o++) {
                ArrayList<Integer> items = orders.get(o).items;

                //For each product type of the order
                for (int i = 0; i < items.size(); i++) {
                    //Choose a WH where a drone can load some items
                    for (int wh = 0; wh < warehouses.size(); wh++) {
                        Warehouse currentWH = warehouses.get(wh);
                        int availableItemsAtWH = currentWH.items.get(i);
                        if (availableItemsAtWH >= items.get(i)) {
                            int numOfItems = items.get(i);
                            while(numOfItems*weights.get(i)>maxPL) {
                                numOfItems--;
                            }
                            if (numOfItems==0)  {
                                continue;
                            }
                            currentWH.items.set(i, currentWH.items.get(i)-numOfItems);
                            items.set(i, items.get(i)-numOfItems);
                            commands.add(droneUsed + " L " + wh + " " + i + " " + (numOfItems));
                            commands.add(droneUsed + " D " + o + " " + i + " " + (numOfItems));

                            droneUsed++;
                            if (droneUsed >= drones.size())
                                return commands;
                        }
                    }
                }
            }

            return commands;
        }

        //Get Livraisons
        private static ArrayList<Livraison> getLivraisonsExtended() {
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

        //Dispatch Drones work
/*        private static ArrayList<String> dispatch(ArrayList<Livraison> livraisons, ArrayList<Drone> drones, ArrayList<int> poids, int maxTime, int chargementMax) {
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
        }*/
        
}
