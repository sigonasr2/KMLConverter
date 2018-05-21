package sig.kml;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

public class KMLConverter {
	final static String TEMPLATE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<kml xmlns=\"http://earth.google.com/kml/2.2\">\n"
			+ "<Document>\n"
			+ "{MARKERS}"
			+ "</Document>\n"
			+ "</kml>\n";
	final static String MARKER_TEMPLATE = "<Placemark>\n"
			+ "<Point>\n"
			+ "<coordinates>{COORDINATES}</coordinates>\n"
			+ "</Point>\n"
			+ "<Style>\n"
			+ "<IconStyle>\n"
			+ "<Icon><href>http://maps.google.com/mapfiles/kml/shapes/placemark_circle_highlight.png</href></Icon>\n"
			+ "<hotSpot x=\"0.5\" y=\"0.5\" xunits=\"fraction\" yunits=\"fraction\"/>\n"
			+ "</IconStyle>\n"
			+ "</Style>\n"
			+ "</Placemark>\n";
	
	static KMLWindow window;
	
	KMLConverter(String input_fileloc, String output_fileloc, boolean flip_vals) {
		String markers = "";
		
		File f = new File(input_fileloc);
		Scanner reader;
		int datapoints = 0;
		boolean lastanswer=false;
		try {
			reader = new Scanner(f);
			double numb1,numb2;
			window.addStatusMessage("Reading file from "+input_fileloc+"...");
			boolean flipped = false;
			while (reader.hasNextLine()) {
				String nextLine = reader.nextLine();
				try {
					Point2D.Double vals = SplitLine(nextLine);
					if (flip_vals) {
						double tmp = vals.y;
						vals.y = vals.x;
						vals.x = tmp;
					}
					if (!flipped && !lastanswer && InvalidLatitudeFound(vals.y)) {
							lastanswer=true;
							if (JOptionPane.showOptionDialog(window, "We detected Latitude values greater than 90 or less than -90. This is usually because your Longitude and Latitude values are flipped.\n\nWould you like us to flip the values for you?", "Invalid Latitude Values", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.YES_OPTION)==JOptionPane.YES_OPTION) {
								window.addStatusMessage("Flipping values...");
								flipped=true;
							}
					}
					if (flipped) {
						numb1 = vals.y;
						numb2 = vals.x;
					} else {
						numb1 = vals.x;
						numb2 = vals.y;
					}
					window.addStatusMessage("Read coords ("+(datapoints+1)+"): ["+numb1+","+numb2+"]");
					markers = ((markers.length()>0)?markers+"\n":"")+MARKER_TEMPLATE.replace("{COORDINATES}", numb1+","+numb2+",0")+"\n";
					datapoints++;
				} catch (NumberFormatException e) {}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		String output = TEMPLATE.replace("{MARKERS}", markers);

		window.addStatusMessage("File written to "+output_fileloc+".");
		window.addStatusMessage("Conversion is complete! Translated file with "+datapoints+" coordinates. Copied to clipboard.");
		Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection output_selection = new StringSelection(output);
		c.setContents(output_selection, null);
		File f_out = new File(output_fileloc);
		try {
			FileWriter fw = new FileWriter(f_out);
			fw.write(output);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean InvalidLatitudeFound(double y) {
		return y<-90 || y>90;
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                window = new KMLWindow();
            }
        });
		/*
		
		*/
		
	}
	
	static Point2D.Double SplitLine(String str) throws NumberFormatException{
		double numb1=0,numb2=0;
		Pattern regex = Pattern.compile("(-?\\d+\\.\\d+)[^\\d-]*(-?\\d+\\.\\d+)");
		Matcher m = regex.matcher(str);
		if (m.find()) {
			numb1 = Double.valueOf(m.group(1));
			numb2 = Double.valueOf(m.group(2));
		} else {
			//This is bad.
		}
		return new Point2D.Double(numb1,numb2);
	}
	
	static boolean isNumeric(char c) {
		return c=='-' || c=='.' || (c>='0' && c<='9');
	}
}
