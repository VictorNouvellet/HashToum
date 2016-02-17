import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class Main {
		// Key : mesure de la qualité. Value : liste des id des orders associées
		private static TreeMap<Integer, ArrayList<Integer>> ordersSorted;
		// Liste des orders dont on est en train de faire la livraison
		private static ArrayList<Integer> indexesOrdersForDelivery;

        public static void main(String[] args) {

			// Lecture du fichier
            perFile("./in/mother_of_all_warehouses.in", "mother_of_all_warehouses.out");
        }

        public static void perFile(String filenameIn, String filenameOut) {
            // Lecture du fichier
            Parser.parseInput(filenameIn);
        	
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
        
        private static void sortOrders() {
        	ordersSorted = new TreeMap<Integer, ArrayList<Integer>>();
        	ArrayList<Order> orders = Parser.getOrders();
            ArrayList<Warehouse> warehouses = Parser.getWarehouses();
            ArrayList<Integer> weights = Parser.weights;
            int maxPayload = Parser.getMaxPayload();
            int score; // Less is best
            int nbRoundTrip; // Number of round trip needed to deliver the whole order
            int distance;
            int weight;
            ArrayList<Integer> necessaryProducts;
            Warehouse warehouse = warehouses.get(0);
        	for(int o = 0; o < orders.size(); o++) {
    			necessaryProducts = orders.get(o).getItems();
    			nbRoundTrip = numberRoundTrip(necessaryProducts, weights, maxPayload );
        		distance = distance(orders.get(o).getRow(), orders.get(o).getColumn(), warehouse.getRow(), warehouse.getColumn());
        		score = nbRoundTrip*2*distance;
        		orders.get(o).setDistanceToWh(distance);
        		orders.get(o).setScore(score);
        		
        		if(ordersSorted.containsKey(score)) {
        			ordersSorted.get(score).add(o);
        		}
        		else {
        			ArrayList<Integer> a = new ArrayList<Integer>();
        			a.add(o);
        			ordersSorted.put(score, a);
        		}
        	}     
        }
        
        private static int numberRoundTrip(ArrayList<Integer> necessaryProducts, ArrayList<Integer> weights, int maxPayload) {
    		int nbRoundTrip = 1;
			int weight = 0;
			for (int i = 0; i < necessaryProducts.size(); i++) {
				for (int p = 0; p < necessaryProducts.get(i); p++) {
					if( maxPayload < weights.get(i) + weight) {
						weight =  weights.get(i);
						nbRoundTrip++;
					}
					else {
						weight +=  weights.get(i);
					}
				}
			}
			return nbRoundTrip;
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
            indexesOrdersForDelivery = new ArrayList<Integer>();
            
            int averageWeight = 0;
            int nbTrip = 0;
            
        	int turn = 0;
        	// Pour chaque tour
        	while (turn < maxTurns) {
	        	boolean allStoped = true;
        		// Pour chaque drone
        		for(int d = 0; d < drones.size(); d++) {
        			// Si il est disponible
        			if(drones.get(d).getTurnsBusy() == 0) {
        				if(!indexesOrdersForDelivery.isEmpty() || !ordersSorted.isEmpty()) {
        					//// Remplissage de la liste d'orders dont on réalise la livraison
        					while (indexesOrdersForDelivery.size() < 1 && !ordersSorted.isEmpty()) {
	        					// On prend les id des premiers meilleurs orders
	        					Entry<Integer, ArrayList<Integer>> firstEntry = ordersSorted.firstEntry();
	            				int indexOrder = firstEntry.getValue().get(0);
	            				indexesOrdersForDelivery.add(indexOrder);
								firstEntry.getValue().remove(0);
								if(firstEntry.getValue().size() == 0) {
									ordersSorted.remove(firstEntry.getKey());
								}
        					}
							
	        		        //// Choix de l'order en livraison a livrer
        					int indexChosenOrder = indexesOrdersForDelivery.get(0);
        					ArrayList<Integer> necessaryProducts = orders.get(indexChosenOrder).getItems();
        					Warehouse warehouse = warehouses.get(0);

        					//// Choix des produits a prendre pour la premiere livraison
        					HashSet<Integer> allProductType = new HashSet<Integer>();
        					ArrayList<Integer> indexesOrdersBeingCared = new ArrayList<Integer>();
	        				ArrayList<ArrayList<Integer>> embeddedProducts = new ArrayList<ArrayList<Integer>>();
	        				ArrayList<Integer> embeddedProductsPerOrder = new ArrayList<Integer>();
	        		        ArrayList<Integer> nbProductTypePerOrder = new ArrayList<Integer>();
	        		        int actualWeight = 0;
	        		        int nbProductType = 0;
	        		        // TODO : Prendre les gros d'abords ?? Pas sur que ca soit mieux
        					indexesOrdersBeingCared.add(indexChosenOrder);
	        				necessaryProducts = orders.get(indexChosenOrder).getItems();
	        		        for (int i = 0; i < necessaryProducts.size(); i++) {
	        		        	embeddedProductsPerOrder.add(0);
	        		        }
        					for	(int n = 0; n < necessaryProducts.size(); n++) {
        						int howMany = necessaryProducts.get(n);
        						while(howMany*weights.get(n)+actualWeight > maxPayload) {
        							howMany--;
        						}
        						if(howMany > 0) {
        							allProductType.add(n);
        							embeddedProductsPerOrder.set(n, howMany);
        							nbProductType++;
        							actualWeight += howMany*weights.get(n);
        						}
        					}
	        		        embeddedProducts.add(embeddedProductsPerOrder);
        					nbProductTypePerOrder.add(nbProductType);
        					
        					//// Recherche livraison proche pour livrer si il reste de la place
        					// Faut regarder dans en cours et en attente
        					// TODO : boucle 
        					float bestScore = Integer.MIN_VALUE; // Si on a un truc pas rentable on le fait pas 
        					float score;
        					int extraDistance;
        					int extraWeight;
        					indexChosenOrder = -1;
        					int indexLastOrder = indexesOrdersBeingCared.get(indexesOrdersBeingCared.size() - 1);
        					// On cherche le meilleur order a livrer a la suite
        					/*for (int o = 0; o < indexesOrdersForDelivery.size(); o++) {
        						int indexOrder = indexesOrdersForDelivery.get(o);
        						if(!indexesOrdersBeingCared.contains(indexOrder)) {
        							// Vérification qu'on peux lui apporter quelque chose
        							extraWeight = 0;
        							necessaryProducts = orders.get(indexOrder).getItems();
        							for (int i = 0; i < necessaryProducts.size(); i++) {
        								int nb = 0;
        								while(necessaryProducts.get(i) > nb && maxPayload >= weights.get(i) + extraWeight + actualWeight) {
        									extraWeight += weights.get(i);
        									nb++;
        								}
        							}
        							// Calcul de la distance supplémentaire a effectuer
    								extraDistance = distance(orders.get(indexLastOrder).getRow(), orders.get(indexLastOrder).getColumn(),
	        								orders.get(indexOrder).getRow(), orders.get(indexOrder).getColumn());
    								extraDistance += distance(orders.get(indexOrder).getRow(), orders.get(indexOrder).getColumn(),
	        								warehouse.getRow(), warehouse.getColumn());
    								extraDistance -= distance(orders.get(indexLastOrder).getRow(), orders.get(indexLastOrder).getColumn(),
    										warehouse.getRow(), warehouse.getColumn());
	        						// TODO : Fixer un rapport limite
    								if (extraDistance == 0) extraDistance++;
    								// SCORE PAS TERRIBLE ??? ...
    								score = (float)extraWeight / (float)extraDistance;
    								if (score > maxScore) {
    									maxScore = score;
	        							indexChosenOrder = indexOrder;
	        						}
        							
        						}
        					}*/
        					
        					if(!ordersSorted.isEmpty()){
	        					Integer key = ordersSorted.firstKey();
		        		        int indexInnerArray = 0;
		        		        boolean finish = false;
		        		        while(!finish) {
		        		        	int indexOrder = ordersSorted.get(key).get(indexInnerArray);
        							// Vérification qu'on peux lui apporter quelque chose
        							extraWeight = 0;
        							necessaryProducts = orders.get(indexOrder).getItems();
        							ArrayList<Integer> necessaryProductsAfterDelivery = new ArrayList<Integer>();
        							for (int i = 0; i < necessaryProducts.size(); i++) {
        								int nb = 0;
        								while(necessaryProducts.get(i) > nb && maxPayload >= weights.get(i) + extraWeight + actualWeight) {
        									extraWeight += weights.get(i);
        									nb++;
        								}
        								necessaryProductsAfterDelivery.add(necessaryProducts.get(i) - nb);
        							}
        							// Calcul de la distance supplémentaire a effectuer
    								extraDistance = distance(orders.get(indexLastOrder).getRow(), orders.get(indexLastOrder).getColumn(),
	        								orders.get(indexOrder).getRow(), orders.get(indexOrder).getColumn());
    								extraDistance += distance(orders.get(indexOrder).getRow(), orders.get(indexOrder).getColumn(),
	        								warehouse.getRow(), warehouse.getColumn());
    								extraDistance -= distance(orders.get(indexLastOrder).getRow(), orders.get(indexLastOrder).getColumn(),
    										warehouse.getRow(), warehouse.getColumn());
    								// SCORE PAS TERRIBLE ??? ...
    								int diffNbRoundTrip = numberRoundTrip( necessaryProducts, weights, maxPayload) - numberRoundTrip( necessaryProductsAfterDelivery, weights, maxPayload);
    								score = 2*orders.get(indexOrder).getDistanceToWh()*diffNbRoundTrip - extraDistance*extraDistance;
    								
    								if (score > bestScore) {
    									bestScore = score;
	        							indexChosenOrder = indexOrder;
	        						}
	        		        		// Test si on a tout parcouru
		        		        	if (++indexInnerArray == ordersSorted.get(key).size()) {
		        		        		indexInnerArray = 0;
		        		        		key = ordersSorted.higherKey(key);
		        		        		finish = key == null;
		        		        		if( finish)System.out.println(bestScore);
		        		        	}
		        		        }
		        		        
        					}
        					
	        		        if(indexChosenOrder != -1) {
		        		        // Recherche des produits a charger pour la seconde livraison
		        				embeddedProductsPerOrder = new ArrayList<Integer>();
		        		        nbProductType = 0;
		        		        // TODO : Prendre les gros d'abords ?? Pas sur que ca soit mieux
	        					indexesOrdersBeingCared.add(indexChosenOrder);
		        				necessaryProducts = orders.get(indexChosenOrder).getItems();
		        		        for (int i = 0; i < necessaryProducts.size(); i++) {
		        		        	embeddedProductsPerOrder.add(0);
		        		        }
	        					for	(int n = 0; n < necessaryProducts.size(); n++) {
	        						int howMany = necessaryProducts.get(n);
	        						while(howMany*weights.get(n)+actualWeight > maxPayload) {
	        							howMany--;
	        						}
	        						if(howMany > 0) {
	        							allProductType.add(n);
	        							embeddedProductsPerOrder.set(n, howMany);
	        							nbProductType++;
	        							actualWeight += howMany*weights.get(n);
	        						}
	        					}
		        		        embeddedProducts.add(embeddedProductsPerOrder);
	        					nbProductTypePerOrder.add(nbProductType);
	        		        }
	        		        
	        				int totalNbProductType = allProductType.size();
	        					
	        				//// Chargement déchargement
	        				// Calcul du temps nécessaire pour réaliser l'opération
	        				int nbTurns = 0;
	        				nbTurns += distance(drones.get(d).getRow(), drones.get(d).getColumn(), warehouse.getRow(), warehouse.getColumn());
	        				nbTurns += totalNbProductType; // Chargement des produits
        					nbTurns += distance(warehouse.getRow(), warehouse.getColumn(), 
        							orders.get(indexesOrdersBeingCared.get(0)).getRow(), orders.get(indexesOrdersBeingCared.get(0)).getColumn());
        					nbTurns += nbProductTypePerOrder.get(0); // Depot des produits pour la première livraison
        					for (int i = 0; i + 1 < indexesOrdersBeingCared.size(); i++) {
	        					nbTurns += distance(orders.get(indexesOrdersBeingCared.get(i)).getRow(), orders.get(indexesOrdersBeingCared.get(i)).getColumn(),
	        							orders.get(indexesOrdersBeingCared.get(i + 1)).getRow(), orders.get(indexesOrdersBeingCared.get(i + 1)).getColumn());
	        					nbTurns += nbProductTypePerOrder.get(i+1);
	        				}
	        				// Verification que le drone aura le temps de finir sa tache
	        				if(turn + nbTurns < maxTurns) {
	        					// Enregistrement des opérations du drone
	        					int totalNbProduct;
	        					for(int p = 0; p < embeddedProducts.get(0).size(); p++) {
	        						totalNbProduct = 0;
	        						for(int o = 0; o < embeddedProducts.size(); o++) {
	        							totalNbProduct += embeddedProducts.get(o).get(p);
	        						}
	        						if(totalNbProduct > 0) {
	        							warehouse.items.set(p, warehouse.items.get(p) - totalNbProduct);
			        				    commands.add(d + " L " + 0 + " " + p + " " + totalNbProduct);
	        						}
	        					}
	        					for(int o = 0; o < embeddedProducts.size(); o++) {
	        						int indexOrder = indexesOrdersBeingCared.get(o);
	        						necessaryProducts = orders.get(indexOrder).getItems();
	        						for(int p = 0; p < necessaryProducts.size(); p++) {
	        							if(embeddedProducts.get(o).get(p) > 0) {
		        							necessaryProducts.set(p, necessaryProducts.get(p) - embeddedProducts.get(o).get(p));
		        							commands.add(d + " D " + indexOrder + " " + p + " " + embeddedProducts.get(o).get(p));
	        							}
	        						}
	        						// Mise a jour de la note de la commande
	        						if(o > 0) {
	        							int key = orders.get(indexOrder).getScore();
	        							ordersSorted.get(key).remove((Integer)indexOrder);
	        							if(ordersSorted.get(key).isEmpty()) {
		        		        			ordersSorted.remove(key);
		        		        		}
	        							int nbRoundTrip = numberRoundTrip(necessaryProducts, weights, maxPayload);
	        							int newScore = nbRoundTrip*2*orders.get(indexOrder).getDistanceToWh();
	        							orders.get(indexOrder).setScore(newScore);
	        			        		if(ordersSorted.containsKey(newScore)) {
	        			        			ordersSorted.get(newScore).add(indexOrder);
	        			        		}
	        			        		else {
	        			        			ArrayList<Integer> a = new ArrayList<Integer>();
	        			        			a.add(indexOrder);
	        			        			ordersSorted.put(newScore, a);
	        			        		}
	        						}
	        						
	        						// check si on a pas fini l'order
		        				    if(orders.get(indexOrder).isEmpty()) {
		        				    	if(!indexesOrdersForDelivery.remove((Integer)indexOrder)) {
		        				    		Integer key = ordersSorted.firstKey();
					        		        boolean found = false;
					        		        while(!found) {
					        		        	found = ordersSorted.get(key).contains(indexOrder);
					        		        	if(found) {
					        		        		ordersSorted.get(key).remove((Integer)indexOrder);
					        		        		if(ordersSorted.get(key).isEmpty()) {
					        		        			ordersSorted.remove(key);
					        		        		}
					        		        	}
					        		        	else {
					        		        		key = ordersSorted.higherKey(key);					        		        
					        		        	}
					        		        }
		        				    	}
		        				    }
        						}
	        					
	        					
	        					// TODO : Sert a rien de faire un détour si au final on réduit pas d'un aller retour
	        					
	        					
	        					//System.out.println(actualWeight);
	        					averageWeight += actualWeight;
	        					nbTrip++;
	        					
	        				    // déplacement du drone a la zeub
	        				    drones.get(d).setRow(orders.get(indexesOrdersBeingCared.get(indexesOrdersBeingCared.size() - 1)).getRow());
	        				    drones.get(d).setColumn(orders.get(indexesOrdersBeingCared.get(indexesOrdersBeingCared.size() - 1)).getColumn());
	        				   
	        				    //drone occupé mnt
	        				    drones.get(d).setTurnsBusy(nbTurns);
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
        			System.out.println(averageWeight / nbTrip);
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
