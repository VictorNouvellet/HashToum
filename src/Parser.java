import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {
    public static ArrayList<Integer> weights;

    private static ArrayList<Warehouse> warehouses;
    private static ArrayList<Order> orders;
    private static ArrayList<Drone> drones;

    private static int maxPayload;
    private static int turns;

	public static void parseInput(String filename) {

		BufferedReader br = null;

		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader(filename));

			sCurrentLine = br.readLine();

            //First Line
            String[] firstParams = sCurrentLine.split(" ");
            int rows = Integer.parseInt(firstParams[0]);
            int columns = Integer.parseInt(firstParams[1]);
            int dronesNum = Integer.parseInt(firstParams[2]);
            turns = Integer.parseInt(firstParams[3]);
            maxPayload = Integer.parseInt(firstParams[4]);

            sCurrentLine = br.readLine();
            //Number of product types
            int numProdTypes = Integer.parseInt(sCurrentLine.split(" ")[0]);

            sCurrentLine = br.readLine();
            //product weights

            String[] prodWeights = sCurrentLine.split(" ");
            weights = new ArrayList<Integer>();
            for(int i=0; i<prodWeights.length; i++){
                weights.add(Integer.parseInt(prodWeights[i]));
            }

            sCurrentLine = br.readLine();
            //Number Warehouse
            int numWH = Integer.parseInt(sCurrentLine.split(" ")[0]);

            //Warehouses creation
            warehouses = new ArrayList<Warehouse>(numWH);
            for (int whid=0; whid< numWH; whid++)   {
                //Create WH with coords
                sCurrentLine = br.readLine();
                int row = Integer.parseInt(sCurrentLine.split(" ")[0]);
                int column = Integer.parseInt(sCurrentLine.split(" ")[1]);
                Warehouse wh = new Warehouse(row, column, numProdTypes);

                //Add items to WH
                sCurrentLine = br.readLine();
                String[] itemsString = sCurrentLine.split(" ");
                for(int i=0; i<numProdTypes; i++){
                    wh.setItems(i,Integer.parseInt(itemsString[i]));
                }

                warehouses.add(whid, wh);
            }

            sCurrentLine = br.readLine();
            //Orders number
            int numOrders = Integer.parseInt(sCurrentLine.split(" ")[0]);

            //Order creation
            orders = new ArrayList<Order>();
            for (int orderId=0; orderId<numOrders; orderId++) {
                //Coords
                sCurrentLine = br.readLine();
                int row = Integer.parseInt(sCurrentLine.split(" ")[0]);
                int column = Integer.parseInt(sCurrentLine.split(" ")[1]);

                //Num Items in Order
                sCurrentLine = br.readLine();
                int numOrderItems = Integer.parseInt(sCurrentLine.split(" ")[0]);

                //Types of Items
                sCurrentLine = br.readLine();
                String[] itemsString = sCurrentLine.split(" ");
                ArrayList<Integer> items = new ArrayList<Integer>();
                for(int i=0; i<numProdTypes; i++){
                    items.add(i,0);
                }
                for(int i=0; i<numOrderItems; i++){
                    int itemId = Integer.parseInt(itemsString[i]);
                    items.set(itemId, items.get(itemId)+1);
                }

                Order order = new Order(row, column, items);
                orders.add(order);
            }


            //Init Drones
            drones = new ArrayList<Drone>();
            for (int idDrone = 0; idDrone<dronesNum; idDrone++) {
                drones.add(new Drone(warehouses.get(0).getRow(), warehouses.get(0).getColumn()));
            }

			while ((sCurrentLine = br.readLine()) != null) {
				System.out.println("ERROR : input line not used -> "+sCurrentLine);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

    public static void displayWarehouses() {
        System.out.println("Number of warehouses : "+warehouses.size());
        for(int i=0; i<warehouses.size(); i++)  {
            System.out.println(warehouses.get(i).toString());
        }
    }

    public static void displayOrders() {
        System.out.println("Number of orders : "+orders.size());
        for(int i=0; i<orders.size(); i++)  {
            System.out.println(orders.get(i).toString());
        }
    }

    public static void displayDrones() {
        System.out.println("Number of drones : "+drones.size());
        for(int i=0; i<drones.size(); i++)  {
            System.out.println(drones.get(i).toString());
        }
    }

    public static ArrayList<Warehouse> getWarehouses()  {
        return warehouses;
    }

    public static ArrayList<Order> getOrders()  {
        return orders;
    }

    public static ArrayList<Drone> getDrones()  {
        return drones;
    }
    
	public static int getMaxPayload() {
		return maxPayload;
	}

	public static int getTurns() {
		return turns;
	}

}