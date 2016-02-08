import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


public class Reader {
//------------------------------------------------- ATTRIBUTES	
	// REGISTER X,Y POSITIONS
	protected int[] xFirstPos = new int [14*80];
	protected int[] yFirstPos = new int [14*80];
	protected int[] xSecondPos = new int [14*80];
	protected int[] ySecondPos = new int [14*80];
	protected int nbLines = 0;

//------------------------------------------------- CONSTRUCTORS
	public Reader()
	{
		
	}
		
//------------------------------------------------- METHODS

	public void paint(String file) {
		BufferedReader br = null;
		char c;
		boolean continuousLine = false;
		int count = 0;
		int rowNumber = 0;

		
		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader(file));
			
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
	
	
	public void writeCommands(String fileName)
	{
		try {

			String content = "This is the content to write into file";

			File file = new File(fileName);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}
			
		
	}
}