import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


public class Reader {
//------------------------------------------------- ATTRIBUTES	
	
	// REGISTER X,Y POSITIONS
	protected ArrayList<Integer> xFirstPos = new ArrayList<Integer>();
	protected ArrayList<Integer> yFirstPos = new ArrayList<Integer>();
	protected ArrayList<Integer> xSecondPos = new ArrayList<Integer>();
	protected ArrayList<Integer> ySecondPos = new ArrayList<Integer>();

//------------------------------------------------- CONSTRUCTORS
	public Reader()
	{
		
	}
		
//------------------------------------------------- METHODS
	public void paint(String fileToRead, String FileToWrite) {
		read(fileToRead);
		writeCommands(FileToWrite);
	}
	
	
/**
 * Reads the input file and registers all the 
 * start & end points of horizontal lines of #
 * @param file
 */
	public void read(String file) {
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
				for(int i=0; i<sCurrentLine.length(); i++)
				{
					c = sCurrentLine.charAt(i);
					
					if (c == '#')
					{
						// START OR RESUME LENGTH COUNT
						continuousLine = true;
						count++;
					}
					else 
					{
						if (continuousLine)
						{
							// END OF CONTINUOUS LINE, MUST REGISTER IT
							xFirstPos.add(i-count); //R1
							yFirstPos.add(rowNumber-1); //C1
							
							xSecondPos.add(i-1); //R2
							ySecondPos.add(rowNumber-1); //C2
							
							// RESTART COUNT
							continuousLine = false;
							count = 0;
						}
					}
					
					// END OF LINE, CHECK IF NEED TO BE REGISTERED
					if (i==sCurrentLine.length()-1)
					{
						if (continuousLine)
						{
							// END OF CONTINUOUS LINE, MUST REGISTER IT
							xFirstPos.add(i-count); //C1
							yFirstPos.add(rowNumber-1); //R1
							
							xSecondPos.add(i-1); //C2
							ySecondPos.add(rowNumber-1); //R2
							
							// RESTART COUNT
							continuousLine = false;
							count = 0;
						}
					}
				}
				// COUNT FOR R1=R2 (=rowNumber)
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

			String content = null;

			File file = new File(fileName);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			// INIT : NUMBER OF LINES
			content = xFirstPos.size()+"\n";
			bw.write(content);
			
			// WRITE COMMANDS IN THE FILE
			for(int i=0; i<xFirstPos.size(); i++)
			{
				content = "PAINT_LINE "+xFirstPos.get(i)+" "+yFirstPos.get(i)+" "+xSecondPos.get(i)+" "+ySecondPos.get(i)+"\n";
				bw.write(content);
			}
			
			bw.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}
			
		
	}
}