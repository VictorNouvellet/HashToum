import java.util.ArrayList;

/**
 * Created by victor on 11/02/2016.
 */
public class Order {
    private int column;
    private int row;
	private ArrayList<Integer> items;

    public Order(int row, int column, ArrayList<Integer> items)  {
        this.row = row;
        this.column = column;
        this.items = items;
    }

    public int getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}

	public ArrayList<Integer> getItems() {
		return items;
	}

	public void setItems(ArrayList<Integer> items) {
		this.items = items;
	}
	
	public int removeItems(int id, int numberOfProducts)   {
        items.add(id, items.get(id)-numberOfProducts);

        return this.items.get(id);
    }
    

    @Override
    public String toString() {
        return "Order{" +
                "column=" + column +
                ", row=" + row +
                ", items=" + items +
                '}';
    }
    
	public boolean removeItemsPossible(int itemId, int quantity){
		int somme = items.get(itemId);
		somme -= quantity;
		if (somme < 0)
		{
			return false;
		}
		items.set(itemId, somme);
		return true;
	}
	
	public boolean isEmpty(){
		if (items.isEmpty())
    	{
    		return true;
    	}
    	
    	for (int i = 0; i < items.size(); i++){
    		if (items.get(i) > 0)
    		{
    			return false;
    		}
    	}
    	return true;
	}
	
	public int getTotalWeight(ArrayList<Integer> weights){
		int totalWeight = 0;
		for (int i = 0; i < items.size(); i++){
			totalWeight += items.get(i)*weights.get(i);
    	}
		return totalWeight;
	}
}
