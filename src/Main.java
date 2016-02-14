import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class Main {
		// Key : mesure de la qualité. Value : liste des id des orders associées
		private static TreeMap<Integer, ArrayList<Integer>> ordersSorted;
		// Liste des orders dont on est en train de faire la livraison
		private static ArrayList<Integer> ordersForDelivery;

        public static void main(String[] args) {

			// Lecture du fichier
            perFile("./in/busy_day.in", "busy_day.out");
            perFile("./in/mother_of_all_warehouses.in", "mother_of_all_warehouses.out");
            perFile("./in/redundancy.in", "redundancy.out");
        }

        public static void perFile(String filenameIn, String filenameOut) {
            // Lecture du fichier
            Parser.parseInput(filenameIn);
        	
        	//
        	sortWarehouses();
        	
            // Trie les orders 
        	sortOrders();
            
            //Construction des livraisons
            ArrayList<String> commands = dispatch();
            try(  PrintWriter out = new PrintWriter(filenameOut)  ){
                out.println(commands.size());
                for (String command:commands
                        ) {
                    out.println( command );
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        
        private static int distance(int r1, int c1, int r2, int c2) {
        	int rowsDiff = Math.abs(r1 - r2);
        	int columnsDiff = Math.abs(c1 - c2);
        	return (int) Math.ceil(Math.sqrt((rowsDiff*rowsDiff)+(columnsDiff*columnsDiff)));
        }
        
        // TODO : Ne pas seulement regarder la proximité avec les wh mais aussi si ils contiennent de quoi compléter l'order
        private static void sortOrders() {
        	ordersSorted = new TreeMap<Integer, ArrayList<Integer>>();
        	ArrayList<Order> orders = Parser.getOrders();
            ArrayList<Warehouse> warehouses = Parser.getWarehouses();
            ArrayList<Integer> weights = Parser.weights;
            int maxPayload = Parser.getMaxPayload();
            int cost;
            int nbRoundTrip;
        	for(int o = 0; o < orders.size(); o++) {
        		nbRoundTrip = (int) Math.ceil(orders.get(o).getTotalWeight(weights) / maxPayload);
        		cost = nbRoundTrip*2*orders.get(o).getWarehousesByProximity().firstKey();
        		if(ordersSorted.containsKey(cost)) {
        			ordersSorted.get(cost).add(o);
        		}
        		else {
        			ArrayList<Integer> a = new ArrayList<Integer>();
        			a.add(o);
        			ordersSorted.put(cost, a);
        		}
        	}     
        }
        
        // Sort for each order the warehouses by proximity
        private static void sortWarehouses() {
            ArrayList<Order> orders = Parser.getOrders();
            ArrayList<Warehouse> warehouses = Parser.getWarehouses();
            TreeMap<Integer, ArrayList<Integer>> warehousesByProximity;
            int distance;
        	for (int o = 0; o < orders.size(); o++) {
        		warehousesByProximity = new TreeMap<Integer, ArrayList<Integer>>();
        		for (int wh = 0; wh < warehouses.size(); wh++) {
        			distance = distance(orders.get(o).getRow(), orders.get(o).getColumn(), warehouses.get(wh).getRow(), warehouses.get(wh).getColumn());
        			if(warehousesByProximity.containsKey(distance)) {
        				warehousesByProximity.get(distance).add(wh);
            		}
            		else {
            			ArrayList<Integer> a = new ArrayList<Integer>();
            			a.add(wh);
            			warehousesByProximity.put(distance, a);
            		}
        		}
        		orders.get(o).setWarehousesByProximity(warehousesByProximity);
        	}
        }
        
        //Dispatch Drones work
        private static ArrayList<String> dispatch() {
            ArrayList<Drone> drones = Parser.getDrones();
            ArrayList<Order> orders = Parser.getOrders();
            ArrayList<Warehouse> warehouses = Parser.getWarehouses();
            int maxPayload = Parser.getMaxPayload();
            int maxTurns = Parser.getTurns();
            ArrayList<Integer> weights = Parser.weights;

            ArrayList<String> commands = new ArrayList<String>();
            ordersForDelivery = new ArrayList<Integer>();
            
        	int turn = 0;
        	// Pour chaque tour
        	while (turn < maxTurns) {
	        	boolean allStoped = true;
        		// Pour chaque drone
        		for(int d = 0; d < drones.size(); d++) {
        			// Si il est disponible
        			if(drones.get(d).getTurnsBusy() == 0) {
        				if(!ordersForDelivery.isEmpty() || !ordersSorted.isEmpty()) {
        					//// Remplissage de la liste d'orders dont on réalise la livraison
        					while (ordersForDelivery.size() < 10 && !ordersSorted.isEmpty()) {
	        					Entry<Integer, ArrayList<Integer>> firstEntry = ordersSorted.firstEntry();
	        					// On prend les id des premiers meilleurs orders
	            				int indexOrder = firstEntry.getValue().get(0);
	            				ordersForDelivery.add(indexOrder);
								firstEntry.getValue().remove(0);
								if(firstEntry.getValue().size() == 0) {
									ordersSorted.remove(firstEntry.getKey());
								}
        					}
							
	        		        //// Choix du warehouse pour chaque order en livraison
	        		        /*
	        		         * ArrayList<ArrayList<Integer>> embeddedProductsPerWh
	        		         * présélectionner ceux dont le trajet + 2 ne prendra pas trop longtemps
	        		         * pour tt les wh (en commencant par les plus proches ??) ?? 
	        		         * ou seleument les quelques proches jusqu'a ce qu'on puisse faire un trajet
	        		         * Chargement des produits les plus lourds d'abord, puis des petits pour chaque wh 
	        		         * calcul d'un score pour chaque wh
	        		         * on garde le meilleur
	        		         */
        					int indexChosenOrder = 0;
        					int indexChosenWarehouse = 0;
        					int minDistanceToWh = Integer.MAX_VALUE;
        					ArrayList<Integer> necessaryProducts;
        					Warehouse warehouse;
        					for (int o = 0; o < ordersForDelivery.size(); o++) {
        						necessaryProducts = orders.get(ordersForDelivery.get(o)).getItems();
		        		        TreeMap<Integer, ArrayList<Integer>> warehousesByProximity = orders.get(ordersForDelivery.get(o)).getWarehousesByProximity();
		        		        int key = warehousesByProximity.firstKey();
		        		        int indexInnerArray = 0;
		        		        boolean whFound = false;
		        		        int wh;
		        		        // TODO : améliorer le critère pour valider le wh
		        		        while(!whFound) {
		        		        	wh = warehousesByProximity.get(key).get(indexInnerArray);
		        		        	warehouse = warehouses.get(wh);
			        		        for (int n = 0; n < necessaryProducts.size() && !whFound; n++) {
			        		        	if(necessaryProducts.get(n) > 0 && warehouse.items.get(n) > 0) {
			        		        		whFound = true;
			        		        		// TODO : améliorer le calcul du cout, distance + ???
			        		        		int distanceToWh = distance(drones.get(d).getRow(), drones.get(d).getColumn(), warehouse.getRow(), warehouse.getColumn());
			        		        		if(distanceToWh < minDistanceToWh) {
			        		        			minDistanceToWh = distanceToWh;
			        		        			indexChosenOrder = ordersForDelivery.get(o);
			        		        			indexChosenWarehouse = wh;
			        		        		}
			        		        	}
			        		        }
			        		        // Parcours des warehousesByProximity
			        		        if(!whFound) {
			        		        	if(++indexInnerArray == warehousesByProximity.get(key).size()) {
			        		        		indexInnerArray = 0;
			        		        		key = warehousesByProximity.higherKey(key);
			        		        	}
			        		        }
		        		        }
        					}
        					
        					warehouse = warehouses.get(indexChosenWarehouse);
	        				necessaryProducts = orders.get(indexChosenOrder).getItems();
	        				ArrayList<Integer> embeddedProducts = new ArrayList<Integer>();
	        		        for (int i = 0; i < necessaryProducts.size(); i++) {
	        		        	embeddedProducts.add(0);
	        		        }
	        		        
	        		        //// Choix des produits a prendre dans le wh choisi
	        		        int actualWeight = 0;
	        		        int nbProductType = 0;
	        		        // TODO : Prendre les gros d'abords ?? Pas sur que ca soit mieux
        					for	(int n = 0; n < necessaryProducts.size(); n++) {
        						int howMany = necessaryProducts.get(n);
        						while(howMany*weights.get(n)+actualWeight > maxPayload || howMany > warehouse.items.get(n)) {
        							howMany--;
        						}
        						if(howMany > 0) {
        							embeddedProducts.set(n, howMany);
        							nbProductType++;
        							actualWeight += howMany*weights.get(n);
        						}
        					}
	        				
	        				//// Chargement déchargement
	        				// Calcul du temps nécessaire pour réaliser l'opération
	        				int nbTurn = 0;
	        				nbTurn += distance(drones.get(d).getRow(), drones.get(d).getColumn(), warehouse.getRow(), warehouse.getColumn());
	        				nbTurn += distance(warehouse.getRow(), warehouse.getColumn(), orders.get(indexChosenOrder).getRow(), orders.get(indexChosenOrder).getColumn());
	        				nbTurn += 2*nbProductType; // Plusieurs (dé)chargements éventuels
	        				// Verification que le drone aura le temps de finir sa tache
	        				if(turn + nbTurn < maxTurns) {
	        				  // Enregistrement des opérations du drone
	        					for(int e = 0; e < embeddedProducts.size(); e++) {
	        						if(embeddedProducts.get(e) > 0) {
	        							warehouse.items.set(e, warehouse.items.get(e) - embeddedProducts.get(e));
			        				    necessaryProducts.set(e, necessaryProducts.get(e) - embeddedProducts.get(e));
			        				    commands.add(d + " L " + indexChosenWarehouse + " " + e + " " + embeddedProducts.get(e));
			        				    //System.out.println(d + " L " + indexChosenWarehouse + " " + e + " " + embeddedProducts.get(e));
	        						}
	        					}
	        					for(int e = 0; e < embeddedProducts.size(); e++) {
	        						if(embeddedProducts.get(e) > 0) {
	        							commands.add(d + " D " + indexChosenOrder + " " + e + " " + embeddedProducts.get(e));
	        						}
	        					}
	        				    // déplacement du drone a la zeub
	        				    drones.get(d).setRow(orders.get(indexChosenOrder).getRow());
	        				    drones.get(d).setColumn(orders.get(indexChosenOrder).getColumn());
	        				    // check si on a pas fini l'order
	        				    if(orders.get(indexChosenOrder).isEmpty()) {
	        				      ordersForDelivery.remove((Integer)indexChosenOrder);
	        				    }
	        				    //drone occupé mnt
	        				    drones.get(d).setTurnsBusy(nbTurn);
	        				    allStoped = false;
	        				//drones.get(d).loadItem(idProduit, nbProduits, Warehouse wh)
	        				}
        				}
        				else {
        					//System.out.println("pas trouvé d'order");
        				}
        			}
        			else allStoped = false;
        		}
        		// Si plus aucun drone n'est actif c'est fini
        		if(allStoped && turn > 0){
        			return commands;
        		}
        		updateDrones();
        		turn++;
        	}
        	return commands;
        }
        
        private static void updateDrones() {
        	ArrayList<Drone> drones = Parser.getDrones();
        	for(int i = 0; i < drones.size(); i++) {
        		drones.get(i).decrementTurnsBusy();
        	}
        }
        
}
