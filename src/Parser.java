import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {

    private static ArrayList<Warehouse> warehouses;
    private static ArrayList<Order> orders;
    private static ArrayList<Drone> drones;

	public static void parseInput() {

		BufferedReader br = null;

		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader("/Users/victor/Downloads/busy_day.in"));

			sCurrentLine = br.readLine();

            //First Line
            ArrayList<Integer> firstParams = sCurrentLine.split(" ");
            int rows = firstParams.get(0);
            int columns = firstParams.get(1);
            int drones = firstParams.get(2);
            int turns = firstParams.get(3);
            int maxPayload = firstParams.get(4);



			while ((sCurrentLine = br.readLine()) != null) {
				System.out.println(sCurrentLine);
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

    public static ArrayList<Warehouse> getWareHouses()  {
        return warehouses;
    }

    public static ArrayList<Order> getOrders()  {
        return orders;
    }

    public static ArrayList<Drone> getDrones()  {
        return drones;
    }
}