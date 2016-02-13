import java.util.ArrayList;

/**
 * Created by victor on 11/02/2016.
 */
public class Drone {
    ArrayList<Integer> inventory;
    private int turnsBusy;
    private int column;
    private int row;

    public Drone(int row, int column)  {
        this.column = column;
        this.row = row;
        turnsBusy = 0;
        inventory = new ArrayList<Integer>();
    }

    public boolean loadItem(int id, int quantity, Warehouse wh)   {
        if(wh.checkAvailable(id, quantity)) {
            wh.removeItems(id, quantity);
            inventory.set(id, inventory.get(id)+quantity);

            return true;
        }
        else    {
            return false;
        }
    }

    public Integer unloadItem(int id, int quantity, Warehouse wh)   {
        wh.addItems(id, quantity);
        inventory.set(id, inventory.get(id)-quantity);
        return inventory.get(id);
    }

    public Integer getItem(int itemId)  {
        return inventory.get(itemId);
    }

    public Integer getWeight(ArrayList<Integer> typeWeights)  {
        int weight = 0;
        for(int i=0; i< typeWeights.size(); i++)    {
            weight += typeWeights.get(i)*inventory.get(i);
        }

        return weight;
    }

    @Override
    public String toString() {
        return "Drone{" +
                "inventory=" + inventory +
                ", column=" + column +
                ", row=" + row +
                '}';
    }
    
    public boolean isEmpty(){
    	if (inventory.isEmpty())
    	{
    		return true;
    	}
    	
    	for (int i=0; i<inventory.size(); i++){
    		if (inventory.get(i)!=0)
    		{
    			return false;
    		}
    	}
    	return true;
    }

	public int getTurnsBusy() {
		return turnsBusy;
	}
	
	public void setTurnsBusy(int turnsBusy) {
		this.turnsBusy = turnsBusy;
	}

	public boolean decrementTurnsBusy() {
		if(this.turnsBusy > 0) {
			this.turnsBusy--;
			return true;
		}
		return false;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}
    
}
