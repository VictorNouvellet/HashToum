import java.util.ArrayList;

/**
 * Created by victor on 11/02/2016.
 */
public class Order {
    int column;
    int row;
	ArrayList<Integer> items;

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
    
}
