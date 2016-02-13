import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class Main {
		// Key : mesure de la qualité. Value : liste des id des orders associées
		private static TreeMap<Integer, ArrayList<Integer>> ordersInOrder;

        public static void main(String[] args) {

			// Lecture du fichier
            perFile("./in/busy_day.in", "busy_day.out");
            perFile("./in/mother_of_all_warehouses.in", "mother_of_all_warehouses.out");
            perFile("./in/redundancy.in", "redundancy.out");
        }

        public static void perFile(String filenameIn, String filenameOut) {
            // Lecture du fichier
            Parser.parseInput(filenameIn);

            // Ordonne les orders 
        	orderOrders();
            
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
        
        private static void orderOrders() {
        	ordersInOrder = new TreeMap<Integer, ArrayList<Integer>>();
        	ArrayList<Order> orders = Parser.getOrders();
            ArrayList<Warehouse> warehouses = Parser.getWarehouses();
            ArrayList<Integer> weights = Parser.weights;
            int maxPayload = Parser.getMaxPayload();
            int minCost;
            int cost;
            int roundTripNb;
        	for(int o = 0; o < orders.size(); o++) {
        		minCost = Integer.MAX_VALUE;
        		for(int wh = 0; wh < warehouses.size(); wh++) {
        			roundTripNb = (int) Math.ceil(orders.get(o).getTotalWeight(weights) / maxPayload);
        			// Mouais
        			cost = roundTripNb*(2*distance(warehouses.get(wh).getRow(), warehouses.get(wh).getColumn(), orders.get(o).getRow(), orders.get(o).getColumn()));
        			if(cost < minCost) {
        				minCost = cost;
        			}
        		}
        		if(ordersInOrder.containsKey(minCost)) {
        			ordersInOrder.get(minCost).add(o);
        		}
        		else {
        			ArrayList<Integer> a = new ArrayList<Integer>();
        			a.add(o);
        			ordersInOrder.put(minCost, a);
        		}
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
            
        	int turn = 0;
        	// Pour chaque tour
        	while (turn < maxTurns) {
	        	boolean allStoped = true;
        		// Pour chaque drone
        		for(int d = 0; d < drones.size(); d++) {
        			// Si il est disponible
        			if(drones.get(d).getTurnsBusy() == 0) {
        				if(!ordersInOrder.isEmpty()) {
        					Entry<Integer, ArrayList<Integer>> firstEntry = ordersInOrder.firstEntry();
        					// On prend le premier id des meilleurs orders
            				int o = firstEntry.getValue().get(0);
	        				ArrayList<Integer> necessaryProducts = orders.get(o).getItems();
	        				ArrayList<Integer> embeddedProducts = new ArrayList<Integer>();
	        		        for (int i = 0; i < necessaryProducts.size(); i++) {
	        		        	embeddedProducts.add(0);
	        		        }
	        		        //// Choix du warehouse
	        		        /*
	        		         * ArrayList<ArrayList<Integer>> embeddedProductsPerWh
	        		         * présélectionner ceux dont le trajet + 2 ne prendra pas trop longtemps
	        		         * pour tt les wh (en commencant par les plus proches ??) ?? 
	        		         * ou seleument les quelques proches jusqu'a ce qu'on puisse faire un trajet
	        		         * Chargement des produits les plus lourds d'abord, puis des petits pour chaque wh 
	        		         * calcul d'un score pour chaque wh
	        		         * on garde le meilleur
	        		         */
	        		        // Version pas terrible :
	        		        int wh = 0;
	        		        boolean whFound = false;
	        		        Warehouse currentWarehouse = warehouses.get(0);
	        		        while(!whFound) {
	        		        	currentWarehouse = warehouses.get(wh);
		        		        for (int n = 0; n < necessaryProducts.size(); n++) {
		        		        	if(necessaryProducts.get(n) > 0 && warehouses.get(wh).items.get(n) > 0) {
		        		        	//if(necessaryProducts.get(n) <= currentWarehouse.items.get(n)) {
		        		        		whFound = true;
		        		        	}
		        		        }
		        		        if(!whFound) wh++;
	        		        }
	        		        
	        		        //// Choix des produits a prendre dans le wh choisi
	        		        int actualWeight = 0;
	        		        int nbProductType = 0;
	        		        //boolean cannotAddMore = false;
	        				//while(!cannotAddMore) {
	        				//cannotAddMore = true;
	        					for	(int n = 0; n < necessaryProducts.size(); n++) {
	        						int howMany = necessaryProducts.get(n);
	        						while(howMany*weights.get(n)+actualWeight > maxPayload || howMany > currentWarehouse.items.get(n)) {
	        							howMany--;
	        						}
	        						if(howMany > 0) {
	        							embeddedProducts.set(n, howMany);
	        							nbProductType++;
	        							actualWeight += howMany*weights.get(n);
	        						}
	        					}
	        				//}
	        				
	        				//// Chargement déchargement
	        				// Calcul du temps nécessaire pour réaliser l'opération
	        				int nbTurn = 0;
	        				nbTurn += distance(drones.get(d).getRow(), drones.get(d).getColumn(), currentWarehouse.getRow(), currentWarehouse.getColumn());
	        				nbTurn += distance(currentWarehouse.getRow(), currentWarehouse.getColumn(), orders.get(o).getRow(), orders.get(o).getColumn());
	        				nbTurn += 2*nbProductType; //plusieur (dé)chargements éventuels
	        				// Verification que le drone aura le temps de finir sa tache
	        				if(turn + nbTurn < maxTurns) {
	        				  // enregistre les opérations du drone
	        					for(int e = 0; e < embeddedProducts.size(); e++) {
	        						if(embeddedProducts.get(e) > 0) {
			        				    currentWarehouse.items.set(e, currentWarehouse.items.get(e) - embeddedProducts.get(e));
			        				    necessaryProducts.set(e, necessaryProducts.get(e) - embeddedProducts.get(e));
			        				    commands.add(d + " L " + wh + " " + e + " " + embeddedProducts.get(e));
	        						}
	        					}
	        					for(int e = 0; e < embeddedProducts.size(); e++) {
	        						if(embeddedProducts.get(e) > 0) {
	        							commands.add(d + " D " + o + " " + e + " " + embeddedProducts.get(e));
	        						}
	        					}
	        				    
	        				    // déplacement du drone a la zeub
	        				    drones.get(d).setRow(orders.get(o).getRow());
	        				    drones.get(d).setColumn(orders.get(o).getColumn());
	        					
	        				    // check si on a pas fini l'order
	        				    if(orders.get(o).isEmpty()) {
	        				      firstEntry.getValue().remove(0);
	        				      if(firstEntry.getValue().size() == 0) {
	        				        ordersInOrder.remove(firstEntry.getKey());
	        				      }
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
