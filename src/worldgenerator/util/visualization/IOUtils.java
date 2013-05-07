package worldgenerator.util.visualization;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


/**
 * Contains utilities for input and output.
 * 
 * @author Felix Dietrich
 */
public class IOUtils {
	/**
	 * Prints a given string to a file with a given name.
	 * @param filename
	 * @param data
	 * @throws IOException if something goes wrong with the file.
	 */
	public static void printDataFile(String filename, String data) throws IOException {
		
		// add the date to the end of the file name
		//DateFormat dateformat = new SimpleDateFormat("d.M.y HHmmss");
		String datestring = ""; //dateformat.format(new Date());
		String filepath = String.format("%s\\%s%s.txt", System.getProperty("user.dir"), filename, datestring);
		
		// create, write and close the file
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filepath)));
		
		bw.write(data);
		bw.close();
	}
	
	/**
	 * Scales an image to the given size.
	 * @param image
	 * @param width
	 * @param height
	 * @return
	 * @throws IOException
	 */
	public static BufferedImage getScaledImage(BufferedImage image, int width, int height) throws IOException {
	    int imageWidth  = image.getWidth();
	    int imageHeight = image.getHeight();

	    double scaleX = (double)width/imageWidth;
	    double scaleY = (double)height/imageHeight;
	    AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
	    AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);

	    return bilinearScaleOp.filter(
	        image,
	        new BufferedImage(width, height, image.getType()));
	}
	

    private static final int filePathMaxLenDefault = 30;

    /**
     * Writes a given string to a given file.
     * @param filepath
     * @param xml
     * @throws IOException
     */
	public static void writeXMLFile(String filepath, String xml) throws IOException {
        BufferedWriter writer = new BufferedWriter( new FileWriter( filepath ) );
        
        writer.write(xml);
        writer.close();
	}
	
	/**
	 * Converts a given xml node to a string.
	 * @param xml
	 * @return the xml node represented as a string
	 * @throws TransformerException
	 */
	public static String xmlToString(Node xml) throws TransformerException
	{
		StringWriter stringWriter = new StringWriter();
		Transformer transformer = TransformerFactory.newInstance().newTransformer(); 
        transformer.transform(new DOMSource(xml.getFirstChild()), new StreamResult(stringWriter)); 
        String strFileContent = stringWriter.toString(); //This is string data of xml file
        return strFileContent;
	}
    
    /**
     * Reads the file and parses it into an XML Document.
     * @param filepath
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
	public static Document readXMLFile(String filepath) throws ParserConfigurationException, SAXException, IOException
	{
        DocumentBuilder         documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document                document        = documentBuilder.parse( filepath );
		return document;
	}

	public static String readTextFile(String filePath) throws IOException {
		Path path = Paths.get(filePath);
		List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<lines.size(); i++)
		{
			sb.append(lines.get(i));
			if(i < lines.size()-1)
			{
				sb.append(System.lineSeparator());
			}
		}
		return sb.toString();
	}

	/** Runs file selector with given title using given subdirectory. */
    public static String chooseXMLFile( String title, String subdir ) {
        JFileChooser    fileChooser = new JFileChooser();
        FileFilter      filter      = new FileNameExtensionFilter("XML file", "xml");
        
        fileChooser.setDialogTitle( title );
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setCurrentDirectory( new File( System.getProperty("user.dir") + subdir ) );
        
        int returnVal = fileChooser.showOpenDialog( null );
        
        if( returnVal == JFileChooser.APPROVE_OPTION )
            return fileChooser.getCurrentDirectory() + "/" + fileChooser.getSelectedFile().getName();
        else
            return "";
    }
    
    /** Runs file selector with given title using given subdirectory. */
    public static String chooseXMLFileSave( String title, String subdir ) {
        JFileChooser    fileChooser = new JFileChooser();
        FileFilter      filter      = new FileNameExtensionFilter("XML file", "xml");
        
        fileChooser.setDialogTitle( title );
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setCurrentDirectory( new File( System.getProperty("user.dir") + subdir ) );
        
        int returnVal = fileChooser.showSaveDialog(null);
        
        if( returnVal == JFileChooser.APPROVE_OPTION )
            return fileChooser.getCurrentDirectory() + "/" + fileChooser.getSelectedFile().getName();
        else
            return "";
    }
    
    public static void errorBox(String infoMessage, String title)
    {
        JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + title, JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Opens a yes-no-cancel message box and returns its result (as JOptionPane.OPTION, i.e. int)
     * @param infoMessage
     * @param title
     * @return
     */
    public static int chooseYesNoCancel(String infoMessage, String title)
    {
    	return JOptionPane.showConfirmDialog(null, infoMessage, title, JOptionPane.YES_NO_CANCEL_OPTION);
    }

	public static void setPathLabel(String scenarioFilePathStr, JLabel label, int maxLen) {
		// cut off some chars
		if(scenarioFilePathStr.length() > maxLen)
		{
			scenarioFilePathStr = "..." + scenarioFilePathStr.substring(scenarioFilePathStr.length()-maxLen-2, scenarioFilePathStr.length());
		}
		
		label.setText(scenarioFilePathStr);
	}

	public static void setPathLabel(String filePathStr, JLabel label) {
		setPathLabel(filePathStr, label, filePathMaxLenDefault);
	}

	public static String escapeXML(String xmltext) {
		// first, escape the escape char
		xmltext = xmltext.replace("&", "&amp;");
		// then, escape the < and > chars
		xmltext = xmltext.replace("<", "&lt;");
		xmltext = xmltext.replace(">", "&gt;");
		
		return xmltext;
	}

	public static String unescapeXML(String xmltext) {
		// first, un-escape the < and > chars
		xmltext = xmltext.replace("&lbr;", "<");
		xmltext = xmltext.replace("&rbr;", ">");
		// then, un-escape the escape char
		xmltext = xmltext.replace("&amp;", "&");
		
		return xmltext;
	}
}
