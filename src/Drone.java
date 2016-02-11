import java.util.ArrayList;

/**
 * Created by victor on 11/02/2016.
 */
public class Drone {
    ArrayList<Integer> inventory;
    int column;
    int row;

    public Drone()  {
        column = 0;
        row = 0;
        inventory = new ArrayList<Integer>();
    }

    public boolean loadItem(int id, int quantity, Warehouse wh)   {
        if(wh.checkAvailable(id, quantity)) {
            wh.removeItems(id, quantity);
            inventory.add(id, quantity);
            return true;
        }
        else    {
            return false;
        }
    }

    public Integer unloadItem(int id, int quantity, Warehouse wh)   {
        wh.addItems(id, quantity);
        inventory.add(id, inventory.get(id)-quantity);
        return inventory.get(id);
    }
}
