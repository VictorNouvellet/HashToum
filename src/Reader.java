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
					if (i==0){
						System.out.println(c+" ");
					}
					
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
				}
				
				// END OF LINE, CHECK IF NEED TO BE REGISTERED
				if (continuousLine)
				{
					// END OF CONTINUOUS LINE, MUST REGISTER IT
					xFirstPos.add(sCurrentLine.length()-count); //C1
					yFirstPos.add(rowNumber-1); //R1
					
					xSecondPos.add(sCurrentLine.length()-1); //C2
					ySecondPos.add(rowNumber-1); //R2
					
				}
				// RESTART COUNT
				continuousLine = false;
				count = 0;
				
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
				content = "PAINT_LINE "+yFirstPos.get(i)+" "+xFirstPos.get(i)+" "+ySecondPos.get(i)+" "+xSecondPos.get(i)+"\n";
				bw.write(content);
			}
			
			bw.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
/**
 * Draws the painting from the arrays to check the painting
 * @param fileName
 */
	public void checkPainting(String fileName)
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
			
			int xi = 0;
			
			// WRITE COMMANDS IN THE FILE
			for(int yi=0; yi<14; yi++)
			{
				for(xi=0; xi<80; xi++)
				{
					if ()
					{
						
					}
					else
					{
						
					}
					content+=".";
				}
				content = "PAINT_LINE "+yFirstPos.get(i)+" "++" "+ySecondPos.get(i)+" "+xSecondPos.get(i)+"\n";
				bw.write(content);
			}
			
			bw.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
/**
 * Returns true if [xi, yi] is included in one of the lines registered while reading
 * @param xi
 * @param yi
 * @return
 */
	public boolean isPartOf (int xi, int yi)
	{
		for(int i=0; i<xFirstPos.size(); i++)
		{
			if(xi >= xFirstPos.get(i) && xi <= xSecondPos.get(i) && yi == yFirstPos.get(i))
			{
				return true;
			}
		}
		return false;
	}
	
}