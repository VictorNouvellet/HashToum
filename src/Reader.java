import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;


public class Reader {
//------------------------------------------------- ATTRIBUTES	


//------------------------------------------------- CONSTRUCTORS
	public Reader()
	{
		
	}
		
//------------------------------------------------- METHODS

	public static void main(String[] args) {

		BufferedReader br = null;
		char c;
		boolean continuousLine = false;
		int count = 0;
		int rowNumber = 0;
		
		// REGISTER X,Y POSITIONS
		int[] xFirstPos = new int [14*80];
		int[] yFirstPos = new int [14*80];
		int[] xSecondPos = new int [14*80];
		int[] ySecondPos = new int [14*80];
		int nbLines = 0;
		
		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader("logo.in"));
			
			// FOR EACH LINE
			while ((sCurrentLine = br.readLine()) != null) {
				
				// FOR EACH CHARACTER
				for(int i=0; i<sCurrentLine.length()-1; i++)
				{
					c = sCurrentLine.charAt(i);
					System.out.print(c);
					
					if (c == '#')
					{
						continuousLine = true;
						count++;
					}
					else
					{
						if (continuousLine)
						{
							// END OF CONTINUOUS LINE, MUST REGISTER IT
							xFirstPos[nbLines] = i-count; //R1
							yFirstPos[nbLines] = rowNumber-1; //C1
							
							xSecondPos[nbLines] = i-1; //R2
							ySecondPos[nbLines] = rowNumber-1; //C2
							System.out.print(xFirstPos[nbLines]+" ");
							System.out.print(yFirstPos[nbLines]+" ");
							System.out.print(xSecondPos[nbLines]+" ");
							System.out.print(ySecondPos[nbLines]+" ");
							nbLines++;
							
							// RESTART COUNT
							continuousLine = false;
							count = 0;
						}
					}
				}
				System.out.println();
				rowNumber++;
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
}