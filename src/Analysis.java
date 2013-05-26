import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.List;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import jxl.CellView;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class Analysis {

	public static void main(String[] args) throws IOException {
		
		// Setup the root and the main directory
		// Setup the minimal lateral roots length, below this value we do not consider the lateral or secondary roots
		
		String root = "C:";
		String workDir = "W_2012_03_22-08";
		Double minLateralRootLength = 0.1;
		
		// Setup the working directories inside the main directory
		// 1_Input : directory to place the files to be analyzed
		// 2_Cleanup : directory where the cleaned up files are stored
		// 3_Output : for each input file a CSV file will be created and placed in this directory
		// 4_Final : directory with all the final files
		
		String inputDir = root+"\\"+workDir+"\\1_Input\\";
		String cleanupDir = root+"\\"+workDir+"\\2_Cleanup\\";
		String outputDir = root+"\\"+workDir+"\\3_Output\\";
		String finalDir = root+"\\"+workDir+"\\4_Final\\";
		
		File dir = new File(inputDir);	
		String[] children = dir.list();
	    List<Accession> accessionsList = new ArrayList<Accession>();
		
		if (children == null) {
		} else {
		    for (int i=0; i<children.length; i++) { // Loop in the directory for the files to be treated

		    	// Extract the accession name out of the file name
		    	int pointIndex = children[i].indexOf(".");
			    String accession = children[i].substring(0, pointIndex);
			    //System.out.println(accession);
			    
			    // Build the different file names
			    String inputFileName = inputDir+accession+".bmp.txt";
			    //String inputFileName = inputDir+accession+".txt";
				String cleanupFileName = cleanupDir+accession+".txt";
				String outputFileName = outputDir+accession+".csv";
				
				// Clean up the input file and store it
				File inFile = new File(inputFileName);
				cleanup(inFile,cleanupFileName);
				
				// Parse the file we have cleaned up to extract the required data
				// Build a .csv file for each accession containing the extracted data
				inFile = new File(cleanupFileName);
				Accession myAccession = new Accession();
				myAccession = parse(inFile,outputFileName,minLateralRootLength);
				//System.out.println(myAccession.getAccessionName());
			    accessionsList.add(myAccession);			    
		    }		    
			
		    // Write file Accession.xls
		    String outFileName = finalDir+"Accessions.xls";	    
		    writeAccessionsFile(outFileName,accessionsList);
		    
		    // Write file AccessionsStatistics.xls
		    outFileName = finalDir+"AccessionsStatistics_1.xls";
		    writeAccessionsStatistics01File(outFileName,accessionsList);
		    
		    // Write file AccessionsStatistics.xls
		    outFileName = finalDir+"AccessionsStatistics_2.xls";
		    writeAccessionsStatistics02File(outFileName,accessionsList);
		    
		    // Write file AccessionsHighLow.xls
		    outFileName = finalDir+"AccessionsTempFile01.csv";
		    writeTempFile1(outFileName,accessionsList);			    

		}				
	}

	//------------------------------------------------------------------------------------------------------------------
	private static void writeTempFile1(String outputfilename,List<Accession> accessionlist) throws IOException{	

		FileWriter f1 = new FileWriter(outputfilename);
		String source="";

		for (int j = 0; j < accessionlist.size(); j++ ){

			String name= "";
			String concentration="";
			String box="";
			int nbOfPlants=0;
			Double LPRmean=0.0;
			Double NLRmean=0.0;
			Double SLRLmean=0.0;
			Double meanLRLmean=0.0;
			Double DLRZ1mean=0.0;
			Double DLRZ2mean=0.0;


			name = accessionlist.get(j).getName();
			concentration = accessionlist.get(j).getConcentration();
			box = accessionlist.get(j).getBox();
			nbOfPlants = accessionlist.get(j).getN();
			LPRmean = accessionlist.get(j).getLPRmean();
			NLRmean = accessionlist.get(j).getNLRmean();
			SLRLmean = accessionlist.get(j).getSLRLmean();
			meanLRLmean = accessionlist.get(j).getMeanLRLmean();
			DLRZ1mean = accessionlist.get(j).getDLRZ1mean();
			DLRZ2mean = accessionlist.get(j).getDLRZ2mean();


			source = name+";"+
					 concentration+";"+
					 nbOfPlants+";"+
					 box+";"+
					 roundDouble(LPRmean,"#.##")+";"+
					 roundDouble(NLRmean,"#.##")+";"+
					 roundDouble(SLRLmean,"#.##")+";"+
					 roundDouble(meanLRLmean,"#.##")+";"+
					 roundDouble(DLRZ1mean,"#.##")+";"+
					 roundDouble(DLRZ2mean,"#.##")+"\r\n";

			// Just to make sure the numbers are OK for Excel
			String newSource = source.replace(".", ",");			    
			f1.write(newSource);
		}
		f1.close();
	}

	//--------------------------------------------------------------------------------------------------------------------
	public static void writeAccessionsFile(String outFileName,List<Accession> accessionsList) throws IOException{

		WritableWorkbook workbook = Workbook.createWorkbook(new File(outFileName));
		WritableSheet sheet = workbook.createSheet("Accessions", 0);
		WritableFont headerInformationFont = new WritableFont(WritableFont.createFont("CALIBRI"), 10, WritableFont.BOLD);
		WritableCellFormat headerInformationFormat = new WritableCellFormat(headerInformationFont);
		WritableFont InformationFont = new WritableFont(WritableFont.createFont("CALIBRI"), 10, WritableFont.NO_BOLD);
		WritableCellFormat InformationFormat = new WritableCellFormat(InformationFont);
		WritableCellFormat cf2 = new WritableCellFormat(InformationFont,NumberFormats.FLOAT);
		WritableCellFormat intg = new WritableCellFormat (InformationFont, NumberFormats.INTEGER);


			try {
				sheet.addCell(new Label(0, 0, "Accession", headerInformationFormat));
				sheet.addCell(new Label(1, 0, "Concentration", headerInformationFormat));
				sheet.addCell(new Label(2, 0, "Day", headerInformationFormat));
				sheet.addCell(new Label(3, 0, "Box  ", headerInformationFormat));
				sheet.addCell(new Label(4, 0, "LPR   ", headerInformationFormat));
				sheet.addCell(new Label(5, 0, "NLR   ", headerInformationFormat));		
				sheet.addCell(new Label(6, 0, "SLRL   ", headerInformationFormat));
				sheet.addCell(new Label(7, 0, "Mean LRL   ", headerInformationFormat));
				sheet.addCell(new Label(8, 0, "DLRZ1", headerInformationFormat));
				sheet.addCell(new Label(9, 0, "DLRZ2", headerInformationFormat));
				sheet.addCell(new Label(10, 0, "Length Z2", headerInformationFormat));
			} catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			int delta = 0;
			int offset = 1; 
			for (int j = 0; j < accessionsList.size(); j++ ){
				offset = offset + delta;
				for (int l = 0; l < accessionsList.get(j).getN(); l++ ){
					try {
						sheet.addCell(new Label(0, l+offset, accessionsList.get(j).getName(), InformationFormat));
						sheet.addCell(new Label(1, l+offset, accessionsList.get(j).getConcentration(), InformationFormat));
						sheet.addCell(new Label(3, l+offset, accessionsList.get(j).getBox(), InformationFormat));
						sheet.addCell(new Number(4, l+offset,accessionsList.get(j).getLPR(l),cf2 ));
						sheet.addCell(new Number(5, l+offset,accessionsList.get(j).getNLR(l),cf2 ));
						sheet.addCell(new Number(6, l+offset,accessionsList.get(j).getSLRL(l),cf2 ));
						sheet.addCell(new Number(7, l+offset,accessionsList.get(j).getMeanLRL(l),cf2 ));
						sheet.addCell(new Number(8, l+offset,accessionsList.get(j).getDLRZ1(l),cf2 ));
						sheet.addCell(new Number(9, l+offset,accessionsList.get(j).getDLRZ2(l),cf2 ));	
						sheet.addCell(new Number(10, l+offset,accessionsList.get(j).getP1_Plast(l),cf2 ));
					} catch (RowsExceededException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (WriteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}													    				
				delta = accessionsList.get(j).getN();
			}
			int c = sheet.getColumns();
			for(int x=0;x<c;x++)
			{
			    CellView cell = sheet.getColumnView(x);
			    cell.setAutosize(true);
			    sheet.setColumnView(x, cell);
			}	
			workbook.write();
			try {
				workbook.close();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	}

	//--------------------------------------------------------------------------------------------------------------------
	public static void writeAccessionsStatistics01File(String outFileName,List<Accession> accessionsList) throws IOException{
		
		WritableWorkbook workbook = Workbook.createWorkbook(new File(outFileName));
		WritableSheet sheet = workbook.createSheet("Accessions", 0);
		WritableFont headerInformationFont = new WritableFont(WritableFont.createFont("CALIBRI"), 10, WritableFont.BOLD);
		WritableCellFormat headerInformationFormat = new WritableCellFormat(headerInformationFont);
		WritableFont InformationFont = new WritableFont(WritableFont.createFont("CALIBRI"), 10, WritableFont.NO_BOLD);
		WritableCellFormat InformationFormat = new WritableCellFormat(InformationFont);
		WritableCellFormat cf2 = new WritableCellFormat(InformationFont,NumberFormats.FLOAT);
		WritableCellFormat intg = new WritableCellFormat (InformationFont, NumberFormats.INTEGER);

		try {
			sheet.addCell(new Label(0, 0, "Accession", headerInformationFormat));
			sheet.addCell(new Label(1, 0, "Concentration", headerInformationFormat));
			sheet.addCell(new Label(2, 0, "Box  ", headerInformationFormat));
			sheet.addCell(new Label(3, 0, "Nb of Plants", headerInformationFormat));
			sheet.addCell(new Label(4, 0, "LPR   ", headerInformationFormat));
			sheet.addCell(new Label(5, 0, "NLR   ", headerInformationFormat));		
			sheet.addCell(new Label(6, 0, "SLRL   ", headerInformationFormat));
			sheet.addCell(new Label(7, 0, "Mean LRL   ", headerInformationFormat));
			sheet.addCell(new Label(8, 0, "Density LR Z1   ", headerInformationFormat));			
			sheet.addCell(new Label(9, 0, "Density LR Z2", headerInformationFormat));
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			int delta = 0;
			int offset = 1; 
			for (int j = 0; j < accessionsList.size(); j++ ){
				offset = offset + delta;
				sheet.addCell(new Label(0,j+offset, accessionsList.get(j).getName(), InformationFormat));
				sheet.addCell(new Label(1,j+offset, accessionsList.get(j).getConcentration(), InformationFormat));
				sheet.addCell(new Label(2,j+offset, accessionsList.get(j).getBox(), InformationFormat));
				sheet.addCell(new Number(3,j+offset,accessionsList.get(j).getN(),intg ));
				sheet.addCell(new Number(4,j+offset,accessionsList.get(j).getLPR(0),cf2 ));
				sheet.addCell(new Number(5,j+offset,accessionsList.get(j).getNLR(0),cf2 ));
				sheet.addCell(new Number(6,j+offset,accessionsList.get(j).getSLRL(0),cf2 ));
				sheet.addCell(new Number(7,j+offset,accessionsList.get(j).getMeanLRL(0),cf2 ));
				sheet.addCell(new Number(8,j+offset,accessionsList.get(j).getDLRZ1(0),cf2 ));				
				sheet.addCell(new Number(9,j+offset,accessionsList.get(j).getDLRZ2(0),cf2 ));
				
			    for (int l = 1; l < accessionsList.get(j).getN(); l++ ){
					sheet.addCell(new Number(4,l+j+offset,accessionsList.get(j).getLPR(l),cf2 ));
					sheet.addCell(new Number(5,l+j+offset,accessionsList.get(j).getNLR(l),cf2 ));
					sheet.addCell(new Number(6,l+j+offset,accessionsList.get(j).getSLRL(l),cf2 ));
					sheet.addCell(new Number(7,l+j+offset,accessionsList.get(j).getMeanLRL(l),cf2 ));
					sheet.addCell(new Number(8,l+j+offset,accessionsList.get(j).getDLRZ1(l),cf2 ));
					sheet.addCell(new Number(9,l+j+offset,accessionsList.get(j).getDLRZ2(l),cf2 ));	    	
			    }
						    
				sheet.addCell(new Label(3,j+accessionsList.get(j).getN()+offset, "MEAN", InformationFormat));
				sheet.addCell(new Number(4,j+accessionsList.get(j).getN()+offset,accessionsList.get(j).getLPRmean(),cf2 ));
				sheet.addCell(new Number(5,j+accessionsList.get(j).getN()+offset,accessionsList.get(j).getNLRmean(),cf2 ));
				sheet.addCell(new Number(6,j+accessionsList.get(j).getN()+offset,accessionsList.get(j).getSLRLmean(),cf2 ));
				sheet.addCell(new Number(7,j+accessionsList.get(j).getN()+offset,accessionsList.get(j).getMeanLRLmean(),cf2 ));
				sheet.addCell(new Number(8,j+accessionsList.get(j).getN()+offset,accessionsList.get(j).getDLRZ1mean(),cf2 ));
				sheet.addCell(new Number(9,j+accessionsList.get(j).getN()+offset,accessionsList.get(j).getDLRZ2mean(),cf2 ));
		
				sheet.addCell(new Label(3,j+accessionsList.get(j).getN()+offset+1, "SD", InformationFormat));
				sheet.addCell(new Number(4,j+accessionsList.get(j).getN()+offset+1,accessionsList.get(j).getLPRsd(),cf2 ));
				sheet.addCell(new Number(5,j+accessionsList.get(j).getN()+offset+1,accessionsList.get(j).getNLRsd(),cf2 ));
				sheet.addCell(new Number(6,j+accessionsList.get(j).getN()+offset+1,accessionsList.get(j).getSLRLsd(),cf2 ));
				sheet.addCell(new Number(7,j+accessionsList.get(j).getN()+offset+1,accessionsList.get(j).getMeanLRLsd(),cf2 ));
				sheet.addCell(new Number(8,j+accessionsList.get(j).getN()+offset+1,accessionsList.get(j).getDLRZ1sd(),cf2 ));
				sheet.addCell(new Number(9,j+accessionsList.get(j).getN()+offset+1,accessionsList.get(j).getDLRZ2sd(),cf2 ));
			
				sheet.addCell(new Label(3,j+accessionsList.get(j).getN()+offset+2, "SE", InformationFormat));
				sheet.addCell(new Number(4,j+accessionsList.get(j).getN()+offset+2,accessionsList.get(j).getLPRse(),cf2 ));
				sheet.addCell(new Number(5,j+accessionsList.get(j).getN()+offset+2,accessionsList.get(j).getNLRse(),cf2 ));
				sheet.addCell(new Number(6,j+accessionsList.get(j).getN()+offset+2,accessionsList.get(j).getSLRLse(),cf2 ));
				sheet.addCell(new Number(7,j+accessionsList.get(j).getN()+offset+2,accessionsList.get(j).getMeanLRLse(),cf2 ));
				sheet.addCell(new Number(8,j+accessionsList.get(j).getN()+offset+2,accessionsList.get(j).getDLRZ1se(),cf2 ));
				sheet.addCell(new Number(9,j+accessionsList.get(j).getN()+offset+2,accessionsList.get(j).getDLRZ2se(),cf2 ));
			
				delta = accessionsList.get(j).getN()+2;
			}
			
			int c = sheet.getColumns();
			for(int x=0;x<c;x++)
			{
			    CellView cell = sheet.getColumnView(x);
			    cell.setAutosize(true);
			    sheet.setColumnView(x, cell);
			}
			
			workbook.write();
			workbook.close();

		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	//--------------------------------------------------------------------------------------------------------------------
	public static void writeAccessionsStatistics02File(String outFileName,List<Accession> accessionsList) throws IOException{
		
		WritableWorkbook workbook = Workbook.createWorkbook(new File(outFileName));
		WritableSheet sheet = workbook.createSheet("Accessions", 0);
		WritableFont headerInformationFont = new WritableFont(WritableFont.createFont("CALIBRI"), 10, WritableFont.BOLD);
		WritableCellFormat headerInformationFormat = new WritableCellFormat(headerInformationFont);
		WritableFont InformationFont = new WritableFont(WritableFont.createFont("CALIBRI"), 10, WritableFont.NO_BOLD);
		WritableCellFormat InformationFormat = new WritableCellFormat(InformationFont);
		WritableCellFormat cf2 = new WritableCellFormat(InformationFont,NumberFormats.FLOAT);
		WritableCellFormat intg = new WritableCellFormat (InformationFont, NumberFormats.INTEGER);

		try {
			sheet.addCell(new Label(0, 0, "Accession", headerInformationFormat));
			sheet.addCell(new Label(1, 0, "Concentration", headerInformationFormat));
			sheet.addCell(new Label(2, 0, "Box  ", headerInformationFormat));
			sheet.addCell(new Label(3, 0, "Nb of Plants", headerInformationFormat));
			sheet.addCell(new Label(4, 0, "LPR   ", headerInformationFormat));
			sheet.addCell(new Label(7, 0, "NLR   ", headerInformationFormat));		
			sheet.addCell(new Label(10, 0, "SLRL   ", headerInformationFormat));
			sheet.addCell(new Label(13, 0, "Mean LRL   ", headerInformationFormat));
			sheet.addCell(new Label(16, 0, "Density LR Z1   ", headerInformationFormat));			
			sheet.addCell(new Label(19, 0, "Density LR Z2", headerInformationFormat));
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			for (int j = 0; j < accessionsList.size(); j++ ){
				sheet.addCell(new Label(0,j+1, accessionsList.get(j).getName(), InformationFormat));
				sheet.addCell(new Label(1,j+1, accessionsList.get(j).getConcentration(), InformationFormat));
				sheet.addCell(new Label(2,j+1, accessionsList.get(j).getBox(), InformationFormat));
				sheet.addCell(new Number(3,j+1,accessionsList.get(j).getN(),intg ));
				sheet.addCell(new Number(4,j+1,accessionsList.get(j).getLPRmean(),cf2 ));
				sheet.addCell(new Label(5,j+1, "±", InformationFormat));
				sheet.addCell(new Number(6,j+1,accessionsList.get(j).getLPRse(),cf2 ));
				sheet.addCell(new Number(7,j+1,accessionsList.get(j).getNLRmean(),cf2 ));
				sheet.addCell(new Label(8,j+1, "±", InformationFormat));
				sheet.addCell(new Number(9,j+1,accessionsList.get(j).getNLRse(),cf2 ));
				sheet.addCell(new Number(10,j+1,accessionsList.get(j).getSLRLmean(),cf2 ));
				sheet.addCell(new Label(11,j+1, "±", InformationFormat));
				sheet.addCell(new Number(12,j+1,accessionsList.get(j).getSLRLse(),cf2 ));
				sheet.addCell(new Number(13,j+1,accessionsList.get(j).getMeanLRLmean(),cf2 ));
				sheet.addCell(new Label(14,j+1, "±", InformationFormat));
				sheet.addCell(new Number(15,j+1,accessionsList.get(j).getMeanLRLse(),cf2 ));
				sheet.addCell(new Number(16,j+1,accessionsList.get(j).getDLRZ1mean(),cf2 ));
				sheet.addCell(new Label(17,j+1, "±", InformationFormat));
				sheet.addCell(new Number(18,j+1,accessionsList.get(j).getDLRZ1se(),cf2 ));
				sheet.addCell(new Number(19,j+1,accessionsList.get(j).getDLRZ2mean(),cf2 ));
				sheet.addCell(new Label(20,j+1, "±", InformationFormat));
				sheet.addCell(new Number(21,j+1,accessionsList.get(j).getDLRZ2se(),cf2 ));
			}
			
			int c = sheet.getColumns();
			for(int x=0;x<c;x++)
			{
			    CellView cell = sheet.getColumnView(x);
			    cell.setAutosize(true);
			    sheet.setColumnView(x, cell);
			}
			
			workbook.write();
			workbook.close();

		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//--------------------------------------------------------------------------------------------------------------------
	public static void cleanup(File infile,String cleanupfilename){
		
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;
		
		try {
			fis = new FileInputStream(infile);
		    bis = new BufferedInputStream(fis);
		    dis = new DataInputStream(bis);
		    FileWriter f0 = new FileWriter(cleanupfilename);
		    
		    while (dis.available() != 0) {	    	
		    	String line = dis.readLine();
		    	//System.out.println(line);
		    	String tmpLine1 = line.replace("\t", ";");
		    	//System.out.println(tmpLine1);
		    	String tmpLine2 = tmpLine1.replace(": ", ";");
		    	//System.out.println(tmpLine2);
		    	String tmpLine3 = tmpLine2+"\r\n";
		    	//System.out.println(tmpLine3);
		    	f0.write(tmpLine3);		    	
		    }
		    
			f0.close();			
		    fis.close();
		    bis.close();
		    dis.close();
		    
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//--------------------------------------------------------------------------------------------------------------------
	public static Accession parse(File infile,String outputfilename, Double minlateralrootlength){
		
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;
		
		// variables to store the accessions data
		String fileName;
		String userName; // should be empty
		String experimentName; // should be empty
		String boxName; // should contain a letter for the box name
		String genotype; // used to store the accession name
		String media; // used to store the concentration (10mm or 10µM)
		int nbOfPlants; // self explanatory

	    Accession currentAccession = new Accession();
	    Accession parsedAccession = new Accession();
		
		try {
			fis = new FileInputStream(infile);
		    bis = new BufferedInputStream(fis);
		    dis = new DataInputStream(bis);
		    
		    while (dis.available() != 0) {
		    	
		    	String line = dis.readLine();
		    	fileName = getStringLineItem(line,1,";"); // get filename in the file
		    	
			    line = dis.readLine();
			    userName = getStringLineItem(line,1,";"); // get username in the file
			    
			    line = dis.readLine();
			    experimentName = getStringLineItem(line,1,";"); // get experiment name in the file
		    			    				    
			    line = dis.readLine();
			    boxName = getStringLineItem(line,1,";"); // get box name (A,B, ...) in the file
			    currentAccession.setBox(boxName); // store it 
			     
			    line = dis.readLine();
			    genotype = getStringLineItem(line,1,";"); // get accession name in the file
			    currentAccession.setName(genotype); // store it

			    line = dis.readLine();
			    media = getStringLineItem(line,1,";"); // get concentration value in the file
			    if (media.equals("10uM")) { // sometimes the µ is transformed as a u !!!
			    	media = "10µM";
			    }
			    currentAccession.setConcentration(media); // store it
			    
			    // skip lines with Age of Plants
			    dis.readLine();
			    
			    // Get the accession number of plants
			    line = dis.readLine();
			    nbOfPlants = getIntegerLineItem(line,1,";"); // get the number of plants in the file
			    currentAccession.setN(nbOfPlants); // store it
			    //System.out.println(nbOfPlants);
			    
			    // skip line with scale and 3 blank lines
			    dis.readLine();
			    dis.readLine();
			    dis.readLine();
			    dis.readLine();

			    // We need to extract for each plant in the accession:
			    // - the length of the primary root
			    // - the number of lateral roots
			    // - the sum of all the lateral and their secondary roots length
			    // - the mean of the lateral roots lengths
			    // - the density of the lateral roots in zone 1
			    // - the density of lateral roots in zone 2
			    // (Zone 2 being the length between the position of the first lateral roots and the last
			    //  lateral roots that are >= of the min lateral roots value)
			    
			    Double[] lengthOfPrimaryRoot = new Double[nbOfPlants];
				int[] nbOfLateralRoots = new int[nbOfPlants];
				Double[] sumOfLateralRootsLength = new Double[nbOfPlants];
				Double[] meanOfLateralRootsLength = new Double[nbOfPlants];
				Double[] densityOfLateralRootsZ1 = new Double[nbOfPlants];
				Double[] densityOfLateralRootsZ2 = new Double[nbOfPlants];
				Double rootDeltaLength = 1.0;
			    
			    for (int i = 0; i < nbOfPlants; i++) {
			    	//System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			    	// Skip line with the root identification (Root i)
			    	dis.readLine();
			    	
			    	// Get the length of the primary root
			    	line = dis.readLine();
			    	lengthOfPrimaryRoot[i] = getDoubleLineItem(line,1,";");
				    currentAccession.setLPR(lengthOfPrimaryRoot[i],i);
				    //System.out.println(roundDouble(lengthOfPrimaryRoot[i]));
				    
			    	// Skip lines with Main root vector, Main root angle
				    dis.readLine();
				    dis.readLine();
				    
				    // Get the number of lateral root(s)
				    line = dis.readLine();
				    nbOfLateralRoots[i] = getIntegerLineItem(line,1,";");
				    currentAccession.setNLR(nbOfLateralRoots[i],i);
				    
				    // We will now get the length of each lateral root and of its secondary roots
				    // we will sum those lengths only for roots having a length greater than the minimum limit.
				    // We will compute the density of lateral roots  based on the following algorithm:
				    // - Find the position of the first lateral root that is longer than the minimal value
				    // - Find the position of the last lateral root that is longer than the minimal value
				    // - Compute the delta between these two
				    // - Get the final number of lateral roots
				    // - Compute the density by dividing the number of lateral roots by the delta length
				    
				    Double lateralRootsLenghSum = 0.00;
				    Double tempValue = 0.00;
				    int maxNbOfRoots = nbOfLateralRoots[i];
				    Double rootStartPosition = 0.00;
				    
				    // The number of lines to read is a function of the number of the lateral roots
				    for (int j = 0; j < maxNbOfRoots; j++) {
				    	
				    	// read the line with the lateral root data
				    	// and extract all the fields of that line
				    	line = dis.readLine();
				    	String[] lineFields = getFields(line,";");
				    	int nbOfFields = lineFields.length;
				    	
				    	// check if we are looking at a lateral root
				    	// if yes and the length is below the minimal root length limit
				    	// then we need to decrease the number of lateral roots
				    	
				    	if (nbOfFields == 12){
				    		
					    	// Get the length of the lateral root
					    	for (int k = 0; k < nbOfFields; k++ ){
					    		if (lineFields[k].contains("Length")){
					    			Double rootLength = Double.valueOf(lineFields[k+1].replace(",", "."));
					    			//System.out.println(rootLength);
					    			if (rootLength <= minlateralrootlength){
					    				nbOfLateralRoots[i] = nbOfLateralRoots[i] -1;
					    				currentAccession.setNLR(nbOfLateralRoots[i],i);
					    			}
					    			//System.out.println(nbOfLateralRoots[i]);
					    			if (rootLength > minlateralrootlength){
					    				Double rootPosition = Double.valueOf(lineFields[k+3].replace(",", "."));
					    				//System.out.println("--------------------------------------------------");
					    				//System.out.println(rootPosition);
					    				if (rootStartPosition == 0.00) {
					    					rootStartPosition = rootPosition;
					    				}
					    				rootDeltaLength = rootPosition - rootStartPosition;
					    				//System.out.println(rootDeltaLength);
					    			}					    			
					    		}
					    	}
				    	}
				    	
				    	// Get the Number secondary root(s)
				    	int nbOfSecondaryRoots = Integer.parseInt(lineFields[nbOfFields-1]);
				    	//System.out.println(nbOfSecondaryRoots);
				    	
				    	// if the value of the number of secondary roots is not 0 then 
				    	// we need to loop some extra lines more, one line per secondary root
				    	// so we increase the loop counter limit by the number of secondary roots
				    	
				    	maxNbOfRoots = maxNbOfRoots + nbOfSecondaryRoots;
				    	
				    	// Get the length of the lateral or secondary root
				    	for (int k = 0; k < nbOfFields; k++ ){
				    		if (lineFields[k].contains("Length")){
				    			Double rootLength = Double.valueOf(lineFields[k+1].replace(",", "."));
				    			//System.out.println(rootLength);
				    			if (rootLength > minlateralrootlength){
				    				tempValue = rootLength;
				    			} else {
				    				tempValue = 0.00;
				    			}
				    		}
				    	}
				    	lateralRootsLenghSum = lateralRootsLenghSum + tempValue;
				    	//System.out.println(lateralRootsLenghSum);
				    }
				    
				    // Save the value for the current root
				    sumOfLateralRootsLength[i]=lateralRootsLenghSum;
				    currentAccession.setSLRL(sumOfLateralRootsLength[i],i);
				    //System.out.println(roundDouble(sumOfLatRootsLength[i]));
				    
				    // Save the mean Lateral roots length
				    if (nbOfLateralRoots[i] == 0) {
				    	meanOfLateralRootsLength[i] = 0.00;
				    	currentAccession.setMeanLRL(meanOfLateralRootsLength[i],i);
				    } else {
					    meanOfLateralRootsLength[i] = sumOfLateralRootsLength[i]/nbOfLateralRoots[i];
					    currentAccession.setMeanLRL(meanOfLateralRootsLength[i],i);
					    //System.out.println(roundDouble(meanOfLateralRootsLength[i]));
				    }
				    
				    // Save the density of lateral roots (Zone1)
				    densityOfLateralRootsZ1[i] = nbOfLateralRoots[i]/lengthOfPrimaryRoot[i];
				    currentAccession.setDLRZ1(densityOfLateralRootsZ1[i],i);
				    //System.out.println(roundDouble(densityOfLateralRootsZ1[i]));
				    
				    // Save the length between first and last lateral roots positions
				    currentAccession.setP1_Plast(rootDeltaLength, i);
				    
				    // Save the density of lateral roots (Zone2)
				    //System.out.println(nbOfLateralRoots[i]);
				    if (nbOfLateralRoots[i] == 0) {
				    	densityOfLateralRootsZ2[i] = 0.00; // There are no lateral roots
				    	currentAccession.setDLRZ2(densityOfLateralRootsZ2[i],i);
				    } else if (nbOfLateralRoots[i] == 1) {
				    	densityOfLateralRootsZ2[i] = 1/lengthOfPrimaryRoot[i]; // Only one lateral root
				    	currentAccession.setDLRZ2(densityOfLateralRootsZ2[i],i);
				    } else {
				    	densityOfLateralRootsZ2[i] = nbOfLateralRoots[i]/rootDeltaLength;
				    	currentAccession.setDLRZ2(densityOfLateralRootsZ2[i],i);
				    }
				    //System.out.println(densityOfLateralRootsZ2[i]);
				    
				    // Skip 3 blank lines before the next root
				    dis.readLine();
				    dis.readLine();
				    dis.readLine();
		        }
			    
			    // write the output file		    
			    parsedAccession = writeFile(currentAccession,
			    							outputfilename,
			    		  				    boxName,
			    		  				    genotype,
			    		  				    media,
			    		  				    nbOfPlants,
			    		  				    lengthOfPrimaryRoot,
			    		  				    nbOfLateralRoots,
			    		  				    sumOfLateralRootsLength,
			    		  				    meanOfLateralRootsLength,
			    							densityOfLateralRootsZ1,
			    		  				    densityOfLateralRootsZ2);

		    }
		    
		    fis.close();
		    bis.close();
		    dis.close();
		    
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return parsedAccession;
	}
	
	//--------------------------------------------------------------------------------------------------------------------
	private static Accession writeFile(Accession parsedaccession,
			                           String outputfilename,
							 	  	   String boxname,
							 	  	   String genotype,
							 	  	   String media,
							 	  	   int nbofplants,
							 	  	   Double[] lengthofprimaryroot,
							 	  	   int[] nboflateralroots,
							 	  	   Double[] sumoflateralrootslength,
							 	  	   Double[] meanoflateralrootslength,
							 	  	   Double[] densityoflateralrootsZ1,
							 	  	   Double[] densityoflateralrootsZ2) throws IOException{
		
	    FileWriter f1 = new FileWriter(outputfilename);
	    
	    // Write first line with the columns titles
	    String source = "Accession Name"+";"+
	    				"Box Name"+";"+
	    				"Concentration"+";"+
	    				"Nb of Plants"+";"+
	    				"Length Primary Root"+";"+
	    				"Nb of Lateral Roots"+";"+
	    				"Sum of Lateral Roots Length"+";"+
	    				"Mean of Lateral Roots Lengths"+";"+
	    				"Density Lateral Roots Z1"+";"+
						"Density Lateral Roots Z2"+"\r\n";	    
	    f1.write(source);
	    
	    // Write the second line, for this line we write the accession name, the box name, 
	    // the concentration and the number of plants. This will not be repeated for the next plants.
	    source = genotype+";"+
	    		 boxname+";"+
	    		 media+";"+
	    		 nbofplants+";"+
	    		 roundDouble(lengthofprimaryroot[0],"#.##")+";"+
	    		 nboflateralroots[0]+";"+
	    		 roundDouble(sumoflateralrootslength[0],"#.##")+";"+
	    		 roundDouble(meanoflateralrootslength[0],"#.##")+";"+
	    		 roundDouble(densityoflateralrootsZ1[0],"#.##")+";"+
		 		 roundDouble(densityoflateralrootsZ2[0],"#.##")+"\r\n";
	    
	    // Just to make sure the numbers are OK for Excel
	    String newSource = source.replace(".", ",");			    
	    f1.write(newSource);
	    
	    // we will now write the following lines based on the number of plants
	    for (int l = 1; l < nbofplants; l++ ){
	    	
	    	source = ";"+";"+";"+";"+
					 roundDouble(lengthofprimaryroot[l],"#.##")+";"+
					 nboflateralroots[l]+";"+
					 roundDouble(sumoflateralrootslength[l],"#.##")+";"+
					 roundDouble(meanoflateralrootslength[l],"#.##")+";"+
					 roundDouble(densityoflateralrootsZ1[l],"#.##")+";"+
	    			 roundDouble(densityoflateralrootsZ2[l],"#.##")+"\r\n";
	    	
	    	newSource = source.replace(".", ",");    	
	    	f1.write(newSource);	    	
	    }
	    
	    // Calculate the means
	    Double mainRootLengthMean = meanDouble(lengthofprimaryroot);
	    parsedaccession.setLPRmean(mainRootLengthMean);
	    Double nbOfLateralRootsMean = meanInt(nboflateralroots);
	    parsedaccession.setNLRmean(nbOfLateralRootsMean);
	    Double sumOfLatRootsLengthMean = meanDouble(sumoflateralrootslength);
	    parsedaccession.setSLRLmean(sumOfLatRootsLengthMean);
	    Double meanOfLatRootsLengthMean = meanDouble(meanoflateralrootslength);
	    parsedaccession.setMeanLRLmean(meanOfLatRootsLengthMean);
	    Double rootsDensityLRZ1Mean = meanDouble(densityoflateralrootsZ1);
	    parsedaccession.setDLRZ1mean(rootsDensityLRZ1Mean);	    
	    Double rootsDensityLRZ2Mean = meanDouble(densityoflateralrootsZ2);
	    parsedaccession.setDLRZ2mean(rootsDensityLRZ2Mean);
	    
	    // Calculate the standard deviations
	    Double mainRootLengthSD = sdDouble(lengthofprimaryroot);
	    //System.out.println(mainRootLengthSD);
	    parsedaccession.setLPRsd(mainRootLengthSD);
	    Double nbOfLateralRootsSD = sdInt(nboflateralroots);
	    parsedaccession.setNLRsd(nbOfLateralRootsSD);
	    Double sumOfLatRootsLengthSD = sdDouble(sumoflateralrootslength);
	    parsedaccession.setSLRLsd(sumOfLatRootsLengthSD);
	    Double meanOfLatRootsLengthSD = sdDouble(meanoflateralrootslength);
	    parsedaccession.setMeanLRLsd(meanOfLatRootsLengthSD);
	    Double rootsDensityLRZ1SD = sdDouble(densityoflateralrootsZ1);
	    parsedaccession.setDLRZ1sd(rootsDensityLRZ1SD);	    	    
	    Double rootsDensityLRZ2SD = sdDouble(densityoflateralrootsZ2);
	    parsedaccession.setDLRZ2sd(rootsDensityLRZ2SD);	    
	    
	    // Calculate the standard errors
	    Double mainRootLengthSE = mainRootLengthSD/Math.sqrt(nbofplants-1);
	    parsedaccession.setLPRse(mainRootLengthSE);
	    Double nbOfLateralRootsSE = nbOfLateralRootsSD/Math.sqrt(nbofplants-1);
	    parsedaccession.setNLRse(nbOfLateralRootsSE);
	    Double sumOfLatRootsLengthSE = sumOfLatRootsLengthSD/Math.sqrt(nbofplants-1);
	    parsedaccession.setSLRLse(sumOfLatRootsLengthSE);
	    Double meanOfLatRootsLengthSE = meanOfLatRootsLengthSD/Math.sqrt(nbofplants-1);
	    parsedaccession.setMeanLRLse(meanOfLatRootsLengthSE);
	    Double rootsDensityLRZ1SE = rootsDensityLRZ1SD/Math.sqrt(nbofplants-1);
	    parsedaccession.setDLRZ1se(rootsDensityLRZ1SE);	    
	    Double rootsDensityLRZ2SE = rootsDensityLRZ2SD/Math.sqrt(nbofplants-1);
	    parsedaccession.setDLRZ2se(rootsDensityLRZ2SE);
	    
	    // Write the line with the different mean values
	    source = ";"+";"+";"+"Mean;"+
	    		 roundDouble(mainRootLengthMean,"#.##")+";"+
	    		 roundDouble(nbOfLateralRootsMean,"#.##")+";"+
	    		 roundDouble(sumOfLatRootsLengthMean,"#.##")+";"+
	    		 roundDouble(meanOfLatRootsLengthMean,"#.##")+";"+
	    		 roundDouble(rootsDensityLRZ1Mean,"#.##")+";"+
		 		 roundDouble(rootsDensityLRZ2Mean,"#.##")+"\r\n";
		    
	    newSource = source.replace(".", ",");
	    f1.write(newSource);

	    // Write the line with the different standard deviations
	    source = ";"+";"+";"+"SD;"+
		 		 roundDouble(mainRootLengthSD,"#.##")+";"+
		 		 roundDouble(nbOfLateralRootsSD,"#.##")+";"+
		 		 roundDouble(sumOfLatRootsLengthSD,"#.##")+";"+
		 		 roundDouble(meanOfLatRootsLengthSD,"#.##")+";"+
		 		 roundDouble(rootsDensityLRZ1SD,"#.##")+";"+
		 		 roundDouble(rootsDensityLRZ2SD,"#.##")+"\r\n";
		    
	    newSource = source.replace(".", ",");
	    f1.write(newSource);

	    // Write the line with the different standard errors
	    source = ";"+";"+";"+"SE;"+
		 		 roundDouble(mainRootLengthSE,"#.##")+";"+
		 		 roundDouble(nbOfLateralRootsSE,"#.##")+";"+
		 		 roundDouble(sumOfLatRootsLengthSE,"#.##")+";"+
		 		 roundDouble(meanOfLatRootsLengthSE,"#.##")+";"+
		 		 roundDouble(rootsDensityLRZ1SE,"#.##")+";"+
		 		 roundDouble(rootsDensityLRZ2SE,"#.##")+"\r\n";
		    
	    newSource = source.replace(".", ",");
	    f1.write(newSource);

	    f1.close();
	    
	    return parsedaccession;
	}
	
    //---------------------------------------------------------------------------------------------------------------------
	private static String getStringLineItem(String line, int index, String patternstr) {
    	
    	// This routine takes a string line as input and returns a string based on the index value 	
    	
    	String fieldStr;
    	String[] fields = line.split(patternstr);
    	if (fields.length==1) {
    		fieldStr = "";
    	} else {
    		fieldStr = fields[index];
    	}
    	return fieldStr;
    }
    
	//---------------------------------------------------------------------------------------------------------------------
    private static int getIntegerLineItem(String line, int index, String patternstr) {
 
    	// This routine takes a string line as input and returns an integer based on the index value
   	
    	String[] fields = line.split(patternstr);
    	return Integer.parseInt(fields[index]);
    }

    //---------------------------------------------------------------------------------------------------------------------
    private static Double getDoubleLineItem(String line, int index, String patternstr) {

    	// This routine takes a string line as input and returns a double based on the index value

    	String[] fields = line.split(patternstr);
    	Double value = Double.valueOf(fields[index].replace(",", "."));
    	return value;
    }
    
	//---------------------------------------------------------------------------------------------------------------------
    private static String[] getFields(String line, String patternstr) {
    	
    	// This routine takes a string line as input and returns an array of string based on the split pattern
    	String[] fields = line.split(patternstr);
    	return fields;
    }
    
	//---------------------------------------------------------------------------------------------------------------------
    static Double roundDouble(Double d, String decimalformat) {
    	
    	// This routine takes a double as input an returns a rounded double based on the format
    	//System.out.println(d);
    	
    	DecimalFormat twoDForm = new DecimalFormat(decimalformat);
	return Double.valueOf(twoDForm.format(d).replace(",", "."));
    }
    
	//---------------------------------------------------------------------------------------------------------------------
    static Double[] calculateGlobalMeans(List<Accession> list) {
    	
    	// This routines calculates the global means
    	// It retrieves for each accession the MRLmean, NRLmean, SLRLmean value
    	// It then calculate for each of them the global mean value and return them as an array
    	
    	Double[] calculatedMeans = new Double[4];
    	Double[] MRLmeans = new Double[list.size()];
    	Double[] NLRmeans = new Double[list.size()];
    	Double[] SLRLmeans = new Double[list.size()];
    	Double[] RDmeans = new Double[list.size()];
    	
    	for (int i=0; i<list.size(); i++) {
    		MRLmeans[i] = roundDouble(list.get(i).getLPRmean(),"#.##");
    		NLRmeans[i] = roundDouble(list.get(i).getNLRmean(),"#.##");
    		SLRLmeans[i] = roundDouble(list.get(i).getSLRLmean(),"#.##");
    		RDmeans[i] = roundDouble(list.get(i).getDLRZ2mean(),"#.##");
        }
    	
    	calculatedMeans[0] = roundDouble(meanDouble(MRLmeans),"#.##");
    	calculatedMeans[1] = roundDouble(meanDouble(NLRmeans),"#.##");
    	calculatedMeans[2] = roundDouble(meanDouble(SLRLmeans),"#.##"); 
    	calculatedMeans[3] = roundDouble(meanDouble(RDmeans),"#.##");
    	
        return calculatedMeans;
    }

    //---------------------------------------------------------------------------------------------------------------------
    static Double meanDouble(Double[] p) {

    	// This routine returns the mean for doubles

    	Double sum = 0.00;  // sum of all the elements
        for (int i=0; i<p.length; i++) {
            sum += p[i];
        }
        return sum / p.length;
    }
    
	//---------------------------------------------------------------------------------------------------------------------
    static Double meanInt(int[] p) {
    	
    	// This routine returns the mean for integers
    	
        Double sum = 0.00;  // sum of all the elements
        for (int i=0; i<p.length; i++) {
            sum += p[i];
        }
        return sum / p.length;
    }
    
	//---------------------------------------------------------------------------------------------------------------------
    public static Double sdDouble ( Double[] data )
    {
    // This routine returns the standard deviation for doubles
    // sd is sqrt of sum of (values-mean) squared divided by n - 1
    	
    // Calculate the mean
    Double mean = 0.00;
    final int n = data.length;
    if ( n < 2 )
       {
       return Double.NaN;
       }
    for ( int i=0; i<n; i++ )
       {
       mean += data[i];
       }
    mean /= n;

    // calculate the sum of squares
    Double sum = 0.00;
    for ( int i=0; i<n; i++ )
       {
       final Double v = data[i] - mean;
       sum += v * v;
       }

    // Change to ( n - 1 ) to n if you have complete data instead of a sample.
    return Math.sqrt( sum / ( n - 1 ) );
    }

	//---------------------------------------------------------------------------------------------------------------------
    public static Double sdInt ( int[] data )
    {
    // This routine returns the standard deviation for integers	
    // sd is sqrt of sum of (values-mean) squared divided by n - 1
    	
    // Calculate the mean
    Double mean = 0.00;
    final int n = data.length;
    if ( n < 2 )
       {
       return Double.NaN;
       }
    for ( int i=0; i<n; i++ )
       {
       mean += data[i];
       }
    mean /= n;
    
    // calculate the sum of squares
    Double sum = 0.00;
    for ( int i=0; i<n; i++ )
       {
       final Double v = data[i] - mean;
       sum += v * v;
       }
    
    // Change to ( n - 1 ) to n if you have complete data instead of a sample.
    return Math.sqrt( sum / ( n - 1 ) );
    }

	//---------------------------------------------------------------------------------------------------------------------
    static Double[] moveToArray(Double value1, Double value2, Double value3, Double value4) {
    	
    	// Moves the 4 Doubles received as input into one array
    	// If a value is equal to zero then it is not added in the array
    	// This means the routine can return an array of size 0 !
    	
    	List<Double> myList = new ArrayList<Double>();
    	
    	if (value1 != 0) {
    		myList.add(value1);
    	}
    	if (value2 != 0) {
    		myList.add(value2);
    	}
    	if (value3 != 0) {
    		myList.add(value3);
    	}
    	if (value4 != 0) {
    		myList.add(value4);
    	}

    	Double [] myArray = new Double[myList.size()];
    	
    	for (int i=0;i<myList.size();i++){
    		myArray[i]=myList.get(i);
    	}
    	
        return myArray;
    }
    
}