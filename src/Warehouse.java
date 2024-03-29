import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by victor on 11/02/2016.
 */
public class Warehouse {
    int column;
    int row;

    ArrayList<Integer> items;

    public Warehouse()  {

    }

    public Warehouse(int row, int column, int numOfProduct)   {
        items = new ArrayList<Integer>();
        for (int i=0; i<numOfProduct; i++)  {
            items.add(0);
        }
        this.column = column;
        this.row = row;
    }

    public int getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}

	public int addItems(int id, int numberOfProducts)   {
        this.items.set(id, items.get(id) + numberOfProducts);

        return this.items.get(id);
    }

    public int setItems(int id, int numberOfProducts)   {
        this.items.set(id, numberOfProducts);

        return this.items.get(id);
    }

    public int removeItems(int id, int numberOfProducts)   {
        items.set(id, items.get(id)-numberOfProducts);

        return this.items.get(id);
    }

    public boolean checkAvailable(int id, int quantity) {
        return items.get(id)-quantity>=0;
    }

    @Override
    public String toString() {
        return "Warehouse{" +
                "column=" + column +
                ", row=" + row +
                ", items=" + items +
                '}';
    }
}
