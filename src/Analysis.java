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
		    String outFileName1 = finalDir+"1_Accessions.xls";	    
		    writeAccessionsFile(outFileName1,accessionsList);
		    
		    // Write file AccessionsStatistics.xls
		    String outFileName2 = finalDir+"2_AccessionsStatistics1.xls";
		    writeAccessionsStatistics01File(outFileName2,accessionsList);
		    
		    // Write file AccessionsStatistics.xls
		    String outFileName3 = finalDir+"3_AccessionsStatistics2.xls";
		    writeAccessionsStatistics02File(outFileName3,accessionsList);
		    
		    // Write file AccessionsHighLow.xls
		    String outFileName4 = finalDir+"4_AccessionsTempFile01.csv";
		    writeTempFile1(outFileName4,accessionsList);		
		    
		    File inFile1 = new File(finalDir+"4_AccessionsTempFile01.csv");
		    List<AccessionMeans> accessionMeansList = new ArrayList<AccessionMeans>();
		    accessionMeansList = getAccessionMeansList(inFile1);	    
		    String outFileName5 = finalDir+"5_AccessionsTempFile02.csv";
		    writeTempFile2(outFileName5,accessionMeansList);
		   
		    File inFile2 = new File(finalDir+"5_AccessionsTempFile02.csv");
		    List<AccessionMeans> globalAccessionMeans = new ArrayList<AccessionMeans>();	    
		    globalAccessionMeans = getGlobalAccessionMeansList(inFile2);    
		    String outFileName6 = finalDir+"6_AccessionsHighsLows.csv";
		    writeAccessionHighsLowsFile(outFileName6,globalAccessionMeans);
		    
		    File inFile3 = new File(finalDir+"6_AccessionsHighsLows.csv");
		    String outFileName7 = finalDir+"7_AccessionsHighsLows.xls";
		    writeAccessionHighsLowsXLSFile(inFile3,outFileName7);
		    
		}				
	}

	//------------------------------------------------------------------------------------------------------------------
	private static void writeAccessionHighsLowsXLSFile(File inFile,String outFileName) throws IOException{
		
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;
		
		// variables to store the data
		String accession;
		Double LPRmeanlow=0.0;
		Double NLRmeanlow=0.0;
		Double SLRLmeanlow=0.0;
		Double meanLRLmeanlow=0.0;
		Double DLRZ1meanlow=0.0;
		Double DLRZ2meanlow=0.0;
		Double LPRmeanhigh=0.0;
		Double NLRmeanhigh=0.0;
		Double SLRLmeanhigh=0.0;
		Double meanLRLmeanhigh=0.0;
		Double DLRZ1meanhigh=0.0;
		Double DLRZ2meanhigh=0.0;
		
		WritableWorkbook workbook = Workbook.createWorkbook(new File(outFileName));
		WritableSheet sheet = workbook.createSheet("Accessions Highs Lows", 0);
		WritableFont headerInformationFont = new WritableFont(WritableFont.createFont("CALIBRI"), 10, WritableFont.BOLD);
		WritableCellFormat headerInformationFormat = new WritableCellFormat(headerInformationFont);
		WritableFont InformationFont = new WritableFont(WritableFont.createFont("CALIBRI"), 10, WritableFont.NO_BOLD);
		WritableCellFormat InformationFormat = new WritableCellFormat(InformationFont);
		WritableCellFormat cf2 = new WritableCellFormat(InformationFont,NumberFormats.FLOAT);
		WritableCellFormat intg = new WritableCellFormat (InformationFont, NumberFormats.INTEGER);
		
		try {
			sheet.addCell(new Label(0, 0, "Accession", headerInformationFormat));
			sheet.addCell(new Label(1, 0, "LPR (Low)", headerInformationFormat));
			sheet.addCell(new Label(2, 0, "LPR (High)", headerInformationFormat));
			sheet.addCell(new Label(3, 0, "NLR (Low)", headerInformationFormat));
			sheet.addCell(new Label(4, 0, "NLR (High)", headerInformationFormat));
			sheet.addCell(new Label(5, 0, "SLRL (Low)", headerInformationFormat));		
			sheet.addCell(new Label(6, 0, "SLRL (High)", headerInformationFormat));
			sheet.addCell(new Label(7, 0, "Mean LRL (Low)", headerInformationFormat));
			sheet.addCell(new Label(8, 0, "Mean LRL (High)", headerInformationFormat));
			sheet.addCell(new Label(9, 0, "DLRZ1 (Low)", headerInformationFormat));
			sheet.addCell(new Label(10, 0, "DLRZ1 (High)", headerInformationFormat));
			sheet.addCell(new Label(11, 0, "DLRZ2 (Low)", headerInformationFormat));
			sheet.addCell(new Label(12, 0, "DLRZ2 (High)", headerInformationFormat));
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		try {
			fis = new FileInputStream(inFile);
		    bis = new BufferedInputStream(fis);
		    dis = new DataInputStream(bis);
		    
		    String line = dis.readLine();
		    int l = 1;
		    
		    while (dis.available() != 0) {
		    	   	
		    	line = dis.readLine();
		    	
		    	accession = getStringLineItem(line,0,";");
		    	LPRmeanlow = getDoubleLineItem(line,1,";");
		    	LPRmeanhigh = getDoubleLineItem(line,2,";");
		    	NLRmeanlow = getDoubleLineItem(line,3,";");
		    	NLRmeanhigh = getDoubleLineItem(line,4,";");
		    	SLRLmeanlow = getDoubleLineItem(line,5,";");
		    	SLRLmeanhigh = getDoubleLineItem(line,6,";");
		    	meanLRLmeanlow = getDoubleLineItem(line,7,";");
		    	meanLRLmeanhigh = getDoubleLineItem(line,8,";");
		    	DLRZ1meanlow = getDoubleLineItem(line,9,";");
		    	DLRZ1meanhigh = getDoubleLineItem(line,10,";");
		    	DLRZ2meanlow = getDoubleLineItem(line,11,";");
		    	DLRZ2meanhigh = getDoubleLineItem(line,12,";");	

				try {
					sheet.addCell(new Label(0,l,accession,InformationFormat));
					sheet.addCell(new Number(1,l,LPRmeanlow,cf2));
					sheet.addCell(new Number(2,l,LPRmeanhigh,cf2));
					sheet.addCell(new Number(3,l,NLRmeanlow,cf2));
					sheet.addCell(new Number(4,l,NLRmeanhigh,cf2));
					sheet.addCell(new Number(5,l,SLRLmeanlow,cf2));
					sheet.addCell(new Number(6,l,SLRLmeanhigh,cf2));
					sheet.addCell(new Number(7,l,meanLRLmeanlow,cf2));
					sheet.addCell(new Number(8,l,meanLRLmeanhigh,cf2));	
					sheet.addCell(new Number(9,l,DLRZ1meanlow,cf2));
					sheet.addCell(new Number(10,l,DLRZ1meanhigh,cf2));
					sheet.addCell(new Number(11,l,DLRZ2meanlow,cf2));
					sheet.addCell(new Number(12,l,DLRZ2meanhigh,cf2));
				} catch (RowsExceededException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WriteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		    	l=l+1;
		    }
		    
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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

	
	//------------------------------------------------------------------------------------------------------------------
	private static void writeAccessionHighsLowsFile(String outputfilename,List<AccessionMeans> globalAccessionMeans) throws IOException{
		
		List<Accession> myAccessionNamesList = new ArrayList<Accession>();
		String currentName="";
		String name="";
		
		for (int j = 0; j < globalAccessionMeans.size(); j++ ){
			name = globalAccessionMeans.get(j).getName();
			if (!(name.equals(currentName))) {
				Accession myAccessionNames = new Accession();
				myAccessionNames.setName(name);
				myAccessionNamesList.add(myAccessionNames);
				currentName=name;
			}
		}
			
//		for (int j = 0; j < myAccessionNamesList.size(); j++ ){
//			System.out.println(myAccessionNamesList.get(j).getName());
//		}
			
		FileWriter f1 = new FileWriter(outputfilename);
		String source="";
		String LOW = "10µM";
		String HIGH = "10mM";
		

		List<AccessionHighsLows> toSaveAccessionHighsLowsList = new ArrayList<AccessionHighsLows>();
			
		for (int j = 0; j < myAccessionNamesList.size(); j++ ){
			String currentAccessionName = myAccessionNamesList.get(j).getName();
			AccessionHighsLows myAccessionHighsLows = new AccessionHighsLows();
			myAccessionHighsLows.setName(currentAccessionName);
			for (int k = 0; k < globalAccessionMeans.size(); k++ ){
				String accessionMeansName = globalAccessionMeans.get(k).getName();
				String concentration = globalAccessionMeans.get(k).getConcentration();
				if (currentAccessionName.equals(accessionMeansName)){
					if (concentration.equals(LOW)) {
						myAccessionHighsLows.setLPRmeanlow(globalAccessionMeans.get(k).getLPRmean());
						myAccessionHighsLows.setNLRmeanlow(globalAccessionMeans.get(k).getNLRmean());
						myAccessionHighsLows.setSLRLmeanlow(globalAccessionMeans.get(k).getSLRLmean());
						myAccessionHighsLows.setMeanLRLmeanlow(globalAccessionMeans.get(k).getMeanLRLmean());
						myAccessionHighsLows.setDLRZ1meanlow(globalAccessionMeans.get(k).getDLRZ1mean());
						myAccessionHighsLows.setDLRZ2meanlow(globalAccessionMeans.get(k).getDLRZ2mean());
					} else {
						myAccessionHighsLows.setLPRmeanhigh(globalAccessionMeans.get(k).getLPRmean());
						myAccessionHighsLows.setNLRmeanhigh(globalAccessionMeans.get(k).getNLRmean());
						myAccessionHighsLows.setSLRLmeanhigh(globalAccessionMeans.get(k).getSLRLmean());
						myAccessionHighsLows.setMeanLRLmeanhigh(globalAccessionMeans.get(k).getMeanLRLmean());
						myAccessionHighsLows.setDLRZ1meanhigh(globalAccessionMeans.get(k).getDLRZ1mean());
						myAccessionHighsLows.setDLRZ2meanhigh(globalAccessionMeans.get(k).getDLRZ2mean());
					}
				}
			}
			
			toSaveAccessionHighsLowsList.add(myAccessionHighsLows);
		}
		
		// Write first line with the columns titles
		source = "Accession"+";"+
				 "LPR (Low)"+";"+
				 "LPR (High)"+";"+
				 "NLR (Low)"+";"+
				 "NLR (High)"+";"+
				 "SLRL (Low)"+";"+
				 "SLRL (High)"+";"+
				 "Mean LRL (Low)"+";"+
				 "Mean LRL (High)"+";"+
				 "Density LR Z1 (Low)"+";"+
				 "Density LR Z1 (High)"+";"+
				 "Density LR Z2 (Low)"+";"+
				 "Density LR Z2 (High)"+"\r\n";	
		
		f1.write(source);
						
		for (int j = 0; j < toSaveAccessionHighsLowsList.size(); j++ ){
			
			source = toSaveAccessionHighsLowsList.get(j).getName()+";"+
					 roundDouble(toSaveAccessionHighsLowsList.get(j).getLPRmeanlow(),"#.##")+";"+
					 roundDouble(toSaveAccessionHighsLowsList.get(j).getLPRmeanhigh(),"#.##")+";"+
					 roundDouble(toSaveAccessionHighsLowsList.get(j).getNLRmeanlow(),"#.##")+";"+
					 roundDouble(toSaveAccessionHighsLowsList.get(j).getNLRmeanhigh(),"#.##")+";"+
					 roundDouble(toSaveAccessionHighsLowsList.get(j).getSLRLmeanlow(),"#.##")+";"+
					 roundDouble(toSaveAccessionHighsLowsList.get(j).getSLRLmeanhigh(),"#.##")+";"+
					 roundDouble(toSaveAccessionHighsLowsList.get(j).getMeanLRLmeanlow(),"#.##")+";"+
					 roundDouble(toSaveAccessionHighsLowsList.get(j).getMeanLRLmeanhigh(),"#.##")+";"+
					 roundDouble(toSaveAccessionHighsLowsList.get(j).getDLRZ1meanlow(),"#.##")+";"+
					 roundDouble(toSaveAccessionHighsLowsList.get(j).getDLRZ2meanlow(),"#.##")+";"+
					 roundDouble(toSaveAccessionHighsLowsList.get(j).getDLRZ1meanhigh(),"#.##")+";"+
					 roundDouble(toSaveAccessionHighsLowsList.get(j).getDLRZ2meanhigh(),"#.##")+"\r\n";

			// Just to make sure the numbers are OK for Excel
			String newSource = source.replace(".", ",");			    
			f1.write(newSource);
		}

		f1.close();
		
		
		
		
		
		
	}	
	
	//------------------------------------------------------------------------------------------------------------------
	private static List<AccessionMeans> getGlobalAccessionMeansList(File infile){
		
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;
		
		// variables to store the data
		String accession;
		String concentration;
		Double LPRmean=0.0;
		Double NLRmean=0.0;
		Double SLRLmean=0.0;
		Double meanLRLmean=0.0;
		Double DLRZ1mean=0.0;
		Double DLRZ2mean=0.0;
		
		List<AccessionMeans> myAccessionMeansList = new ArrayList<AccessionMeans>();
		
		try {
			fis = new FileInputStream(infile);
		    bis = new BufferedInputStream(fis);
		    dis = new DataInputStream(bis);
		    
		    while (dis.available() != 0) {
		    	   	
		    	String line = dis.readLine();
		    	
		    	accession = getStringLineItem(line,0,";");
		    	concentration = getStringLineItem(line,1,";");
		    	LPRmean = getDoubleLineItem(line,2,";");
		    	NLRmean = getDoubleLineItem(line,3,";");
		    	SLRLmean = getDoubleLineItem(line,4,";");
		    	meanLRLmean = getDoubleLineItem(line,5,";");
		    	DLRZ1mean = getDoubleLineItem(line,6,";");
		    	DLRZ2mean = getDoubleLineItem(line,7,";");
		    	
		    	AccessionMeans myAccessionMeans = new AccessionMeans();
		    	
		    	myAccessionMeans.setName(accession);
		    	myAccessionMeans.setConcentration(concentration);
		    	myAccessionMeans.setLPRmean(LPRmean);
		    	myAccessionMeans.setNLRmean(NLRmean);
		    	myAccessionMeans.setSLRLmean(SLRLmean);
		    	myAccessionMeans.setMeanLRLmean(SLRLmean);
		    	myAccessionMeans.setDLRZ1mean(DLRZ1mean);
		    	myAccessionMeans.setDLRZ2mean(DLRZ2mean);
		    	
		    	myAccessionMeansList.add(myAccessionMeans);	 
		    		    	
		    }
		    
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	

		return myAccessionMeansList;
	}	
	
	//-----------------------------------------------------------------------------------------------------------------------
	private static void writeTempFile2(String outputfilename,List<AccessionMeans> accessionMeansList) throws IOException{	
		
		List<AccessionMeans> accessionMeansList_A  = new ArrayList<AccessionMeans>();
		List<AccessionMeans> accessionMeansList_B  = new ArrayList<AccessionMeans>();
		List<AccessionMeans> accessionMeansList_C  = new ArrayList<AccessionMeans>();
		List<AccessionMeans> accessionMeansList_D  = new ArrayList<AccessionMeans>();
		List<AccessionMeans> accessionMeansList_E  = new ArrayList<AccessionMeans>();
		List<AccessionMeans> accessionMeansList_F  = new ArrayList<AccessionMeans>();
		List<AccessionMeans> accessionMeansList_G  = new ArrayList<AccessionMeans>();
		List<AccessionMeans> accessionMeansList_H  = new ArrayList<AccessionMeans>();
		List<AccessionMeans> accessionMeansList_I  = new ArrayList<AccessionMeans>();
		
		List<AccessionMeans> finalAccessionMeansList  = new ArrayList<AccessionMeans>();
			
		// Get the list of all unique accessions names ------------------------------------------------------------------
		List<Accession> accessionNamesList = new ArrayList<Accession>();
		String currentAccessionName="";
		String accessionName="";

		for (int j = 0; j < accessionMeansList.size(); j++ ){
			accessionName = accessionMeansList.get(j).getName();
			if (!(accessionName.equals(currentAccessionName))) {
				Accession myAccessionName = new Accession();
				myAccessionName.setName(accessionName);
				accessionNamesList.add(myAccessionName);
				currentAccessionName=accessionName;
			}
		}
		
//		// Debug
//	    for (int l = 0; l < accessionNamesList.size(); l++ ){
//	    	System.out.println(accessionNamesList.get(l).getName());
//	    }	
	    
	    // Set the list of concentrations -------------------------------------------------------------------------------
	    List<Concentration> concentrationList = new ArrayList<Concentration>();
		Concentration concentration_10mM = new Concentration("10mM");
		concentrationList.add(concentration_10mM);
		Concentration concentration_10µM = new Concentration("10µM");
		concentrationList.add(concentration_10µM);
		
//		// Debug
//	    for (int l = 0; l < concentrationList.size(); l++ ){
//	    	System.out.println(concentrationList.get(l).getName());
//	    }
	    
		String name= "";
		String concentration="";
		String box="";
		int nbofplants=0;
		Double LPRmean=0.0;
		Double NLRmean=0.0;
		Double SLRLmean=0.0;
		Double meanLRLmean=0.0;
		Double DLRZ1mean=0.0;
		Double DLRZ2mean=0.0;
			
		// Sort the accessions per boxes - A,B,C,D,E,F,G,H,I -------------------------------------------------------------
		for (int j = 0; j < accessionMeansList.size(); j++ ){
			
			name = accessionMeansList.get(j).getName();
			concentration = accessionMeansList.get(j).getConcentration();
			box = accessionMeansList.get(j).getBox();
			nbofplants = accessionMeansList.get(j).getN();
			LPRmean = accessionMeansList.get(j).getLPRmean();
			NLRmean = accessionMeansList.get(j).getNLRmean();
			SLRLmean = accessionMeansList.get(j).getSLRLmean();
			meanLRLmean = accessionMeansList.get(j).getMeanLRLmean();
			DLRZ1mean = accessionMeansList.get(j).getDLRZ1mean();
			DLRZ2mean = accessionMeansList.get(j).getDLRZ2mean();
			
			if (box.equals("A")) {
				AccessionMeans myAccessionMeans_A = new AccessionMeans();
				myAccessionMeans_A.setName(name);
				myAccessionMeans_A.setBox(box);
				myAccessionMeans_A.setConcentration(concentration);
				myAccessionMeans_A.setN(nbofplants);
				myAccessionMeans_A.setLPRmean(LPRmean);
				myAccessionMeans_A.setNLRmean(NLRmean);
				myAccessionMeans_A.setSLRLmean(SLRLmean);
				myAccessionMeans_A.setMeanLRLmean(meanLRLmean);
				myAccessionMeans_A.setDLRZ1mean(DLRZ1mean);
				myAccessionMeans_A.setDLRZ2mean(DLRZ2mean);
				accessionMeansList_A.add(myAccessionMeans_A);
			}
			if (box.equals("B")) {
				AccessionMeans myAccessionMeans_B = new AccessionMeans();
				myAccessionMeans_B.setName(name);
				myAccessionMeans_B.setBox(box);
				myAccessionMeans_B.setConcentration(concentration);
				myAccessionMeans_B.setN(nbofplants);
				myAccessionMeans_B.setLPRmean(LPRmean);
				myAccessionMeans_B.setNLRmean(NLRmean);
				myAccessionMeans_B.setSLRLmean(SLRLmean);
				myAccessionMeans_B.setMeanLRLmean(meanLRLmean);
				myAccessionMeans_B.setDLRZ1mean(DLRZ1mean);
				myAccessionMeans_B.setDLRZ2mean(DLRZ2mean);
				accessionMeansList_B.add(myAccessionMeans_B);
			}
			if (box.equals("C")) {
				AccessionMeans myAccessionMeans_C = new AccessionMeans();
				myAccessionMeans_C.setName(name);
				myAccessionMeans_C.setBox(box);
				myAccessionMeans_C.setConcentration(concentration);
				myAccessionMeans_C.setN(nbofplants);
				myAccessionMeans_C.setLPRmean(LPRmean);
				myAccessionMeans_C.setNLRmean(NLRmean);
				myAccessionMeans_C.setSLRLmean(SLRLmean);
				myAccessionMeans_C.setMeanLRLmean(meanLRLmean);
				myAccessionMeans_C.setDLRZ1mean(DLRZ1mean);
				myAccessionMeans_C.setDLRZ2mean(DLRZ2mean);
				accessionMeansList_C.add(myAccessionMeans_C);
			}
			if (box.equals("D")) {
				AccessionMeans myAccessionMeans_D = new AccessionMeans();
				myAccessionMeans_D.setName(name);
				myAccessionMeans_D.setBox(box);
				myAccessionMeans_D.setConcentration(concentration);
				myAccessionMeans_D.setN(nbofplants);
				myAccessionMeans_D.setLPRmean(LPRmean);
				myAccessionMeans_D.setNLRmean(NLRmean);
				myAccessionMeans_D.setSLRLmean(SLRLmean);
				myAccessionMeans_D.setMeanLRLmean(meanLRLmean);
				myAccessionMeans_D.setDLRZ1mean(DLRZ1mean);
				myAccessionMeans_D.setDLRZ2mean(DLRZ2mean);
				accessionMeansList_D.add(myAccessionMeans_D);
			}
			if (box.equals("E")) {
				AccessionMeans myAccessionMeans_E = new AccessionMeans();
				myAccessionMeans_E.setName(name);
				myAccessionMeans_E.setBox(box);
				myAccessionMeans_E.setConcentration(concentration);
				myAccessionMeans_E.setN(nbofplants);
				myAccessionMeans_E.setLPRmean(LPRmean);
				myAccessionMeans_E.setNLRmean(NLRmean);
				myAccessionMeans_E.setSLRLmean(SLRLmean);
				myAccessionMeans_E.setMeanLRLmean(meanLRLmean);
				myAccessionMeans_E.setDLRZ1mean(DLRZ1mean);
				myAccessionMeans_E.setDLRZ2mean(DLRZ2mean);
				accessionMeansList_E.add(myAccessionMeans_E);
			}
			if (box.equals("F")) {
				AccessionMeans myAccessionMeans_F = new AccessionMeans();
				myAccessionMeans_F.setName(name);
				myAccessionMeans_F.setBox(box);
				myAccessionMeans_F.setConcentration(concentration);
				myAccessionMeans_F.setN(nbofplants);
				myAccessionMeans_F.setLPRmean(LPRmean);
				myAccessionMeans_F.setNLRmean(NLRmean);
				myAccessionMeans_F.setSLRLmean(SLRLmean);
				myAccessionMeans_F.setMeanLRLmean(meanLRLmean);
				myAccessionMeans_F.setDLRZ1mean(DLRZ1mean);
				myAccessionMeans_F.setDLRZ2mean(DLRZ2mean);
				accessionMeansList_F.add(myAccessionMeans_F);
			}
			if (box.equals("G")) {
				AccessionMeans myAccessionMeans_G = new AccessionMeans();
				myAccessionMeans_G.setName(name);
				myAccessionMeans_G.setBox(box);
				myAccessionMeans_G.setConcentration(concentration);
				myAccessionMeans_G.setN(nbofplants);
				myAccessionMeans_G.setLPRmean(LPRmean);
				myAccessionMeans_G.setNLRmean(NLRmean);
				myAccessionMeans_G.setSLRLmean(SLRLmean);
				myAccessionMeans_G.setMeanLRLmean(meanLRLmean);
				myAccessionMeans_G.setDLRZ1mean(DLRZ1mean);
				myAccessionMeans_G.setDLRZ2mean(DLRZ2mean);
				accessionMeansList_G.add(myAccessionMeans_G);
			}
			if (box.equals("H")) {
				AccessionMeans myAccessionMeans_H = new AccessionMeans();
				myAccessionMeans_H.setName(name);
				myAccessionMeans_H.setBox(box);
				myAccessionMeans_H.setConcentration(concentration);
				myAccessionMeans_H.setN(nbofplants);
				myAccessionMeans_H.setLPRmean(LPRmean);
				myAccessionMeans_H.setNLRmean(NLRmean);
				myAccessionMeans_H.setSLRLmean(SLRLmean);
				myAccessionMeans_H.setMeanLRLmean(meanLRLmean);
				myAccessionMeans_H.setDLRZ1mean(DLRZ1mean);
				myAccessionMeans_H.setDLRZ2mean(DLRZ2mean);
				accessionMeansList_H.add(myAccessionMeans_H);
			}
			if (box.equals("I")) {
				AccessionMeans myAccessionMeans_I = new AccessionMeans();
				myAccessionMeans_I.setName(name);
				myAccessionMeans_I.setBox(box);
				myAccessionMeans_I.setConcentration(concentration);
				myAccessionMeans_I.setN(nbofplants);
				myAccessionMeans_I.setLPRmean(LPRmean);
				myAccessionMeans_I.setNLRmean(NLRmean);
				myAccessionMeans_I.setSLRLmean(SLRLmean);
				myAccessionMeans_I.setMeanLRLmean(meanLRLmean);
				myAccessionMeans_I.setDLRZ1mean(DLRZ1mean);
				myAccessionMeans_I.setDLRZ2mean(DLRZ2mean);
				accessionMeansList_I.add(myAccessionMeans_I);
			}
		}	
		
		//Debug
//		System.out.println(accessionMeansList_A.size()+" "+
//				accessionMeansList_B.size()+" "+
//				accessionMeansList_C.size()+" "+
//				accessionMeansList_D.size()+" "+
//				accessionMeansList_E.size()+" "+
//				accessionMeansList_F.size()+" "+
//				accessionMeansList_G.size()+" "+
//				accessionMeansList_H.size()+" "+
//				accessionMeansList_I.size());
		
		
		// Loop in the unique accessions names, and by concentration combine the data ------------------------------------
			
		for (int i = 0; i < accessionNamesList.size(); i++ ){
		
			name = accessionNamesList.get(i).getName();
			
			for (int j = 0; j < concentrationList.size(); j++ ){
				
				int totalNbOfPlants=0;
				int nbOfPlantsA=0;
				int nbOfPlantsB=0;
				int nbOfPlantsC=0;
				int nbOfPlantsD=0;				
				int nbOfPlantsE=0;
				int nbOfPlantsF=0;
				int nbOfPlantsG=0;
				int nbOfPlantsH=0;
				int nbOfPlantsI=0;
				
				Double LPRmeanA=0.0;
				Double LPRmeanB=0.0;
				Double LPRmeanC=0.0;
				Double LPRmeanD=0.0;
				Double LPRmeanE=0.0;
				Double LPRmeanF=0.0;
				Double LPRmeanG=0.0;
				Double LPRmeanH=0.0;
				Double LPRmeanI=0.0;
				
				Double NLRmeanA=0.0;
				Double NLRmeanB=0.0;
				Double NLRmeanC=0.0;
				Double NLRmeanD=0.0;
				Double NLRmeanE=0.0;
				Double NLRmeanF=0.0;
				Double NLRmeanG=0.0;
				Double NLRmeanH=0.0;
				Double NLRmeanI=0.0;
				
				Double SLRLmeanA=0.0;
				Double SLRLmeanB=0.0;
				Double SLRLmeanC=0.0;
				Double SLRLmeanD=0.0;
				Double SLRLmeanE=0.0;
				Double SLRLmeanF=0.0;
				Double SLRLmeanG=0.0;
				Double SLRLmeanH=0.0;
				Double SLRLmeanI=0.0;
				
				Double meanLRLmeanA=0.0;
				Double meanLRLmeanB=0.0;
				Double meanLRLmeanC=0.0;
				Double meanLRLmeanD=0.0;
				Double meanLRLmeanE=0.0;
				Double meanLRLmeanF=0.0;
				Double meanLRLmeanG=0.0;
				Double meanLRLmeanH=0.0;
				Double meanLRLmeanI=0.0;
				
				Double DLRZ1meanA=0.0;
				Double DLRZ1meanB=0.0;
				Double DLRZ1meanC=0.0;
				Double DLRZ1meanD=0.0;
				Double DLRZ1meanE=0.0;
				Double DLRZ1meanF=0.0;
				Double DLRZ1meanG=0.0;
				Double DLRZ1meanH=0.0;
				Double DLRZ1meanI=0.0;
				
				Double DLRZ2meanA=0.0;
				Double DLRZ2meanB=0.0;
				Double DLRZ2meanC=0.0;
				Double DLRZ2meanD=0.0;
				Double DLRZ2meanE=0.0;
				Double DLRZ2meanF=0.0;
				Double DLRZ2meanG=0.0;
				Double DLRZ2meanH=0.0;
				Double DLRZ2meanI=0.0;
				
				concentration = concentrationList.get(j).getName();
				
				for (int k = 0; k < accessionMeansList_A.size(); k++ ){
					if (accessionMeansList_A.get(k).getName().equals(name)) {
						if (accessionMeansList_A.get(k).getConcentration().equals(concentration)) {
							nbOfPlantsA = accessionMeansList_A.get(k).getN();
							LPRmeanA = LPRmeanA + accessionMeansList_A.get(k).getLPRmean()*nbOfPlantsA;
							NLRmeanA = NLRmeanA + accessionMeansList_A.get(k).getNLRmean()*nbOfPlantsA;
							SLRLmeanA = SLRLmeanA + accessionMeansList_A.get(k).getSLRLmean()*nbOfPlantsA;
							meanLRLmeanA = meanLRLmeanA + accessionMeansList_A.get(k).getMeanLRLmean()*nbOfPlantsA;
							DLRZ1meanA = DLRZ1meanA + accessionMeansList_A.get(k).getDLRZ1mean()*nbOfPlantsA;
							DLRZ2meanA = DLRZ2meanA + accessionMeansList_A.get(k).getDLRZ2mean()*nbOfPlantsA;
							
							//Debug
							System.out.println(name+" "+
											   concentration+" "+
											   nbOfPlantsA+" "+
											   LPRmeanA+" "+
											   NLRmeanA+" "+
											   SLRLmeanA+" "+
											   meanLRLmeanA+" "+
											   DLRZ1meanA+" "+
											   DLRZ2meanA);
						}
					}
				}
				
				for (int k = 0; k < accessionMeansList_B.size(); k++ ){
					if (accessionMeansList_B.get(k).getName().equals(name)) {
						if (accessionMeansList_B.get(k).getConcentration().equals(concentration)) {
							nbOfPlantsB = accessionMeansList_B.get(k).getN();
							LPRmeanB = LPRmeanB + accessionMeansList_B.get(k).getLPRmean()*nbOfPlantsB;
							NLRmeanB = NLRmeanB + accessionMeansList_B.get(k).getNLRmean()*nbOfPlantsB;
							SLRLmeanB = SLRLmeanB + accessionMeansList_B.get(k).getSLRLmean()*nbOfPlantsB;
							meanLRLmeanB = meanLRLmeanB + accessionMeansList_B.get(k).getMeanLRLmean()*nbOfPlantsB;
							DLRZ1meanB = DLRZ1meanB + accessionMeansList_B.get(k).getDLRZ1mean()*nbOfPlantsB;
							DLRZ2meanB = DLRZ2meanB + accessionMeansList_B.get(k).getDLRZ2mean()*nbOfPlantsB;
							
							//Debug
							System.out.println(name+" "+
											   concentration+" "+
											   nbOfPlantsB+" "+
											   LPRmeanB+" "+
											   NLRmeanB+" "+
											   SLRLmeanB+" "+
											   meanLRLmeanB+" "+
											   DLRZ1meanB+" "+
											   DLRZ2meanB);
						}
					}
				}
				
				for (int k = 0; k < accessionMeansList_C.size(); k++ ){
					if (accessionMeansList_C.get(k).getName().equals(name)) {
						if (accessionMeansList_C.get(k).getConcentration().equals(concentration)) {
							nbOfPlantsC = accessionMeansList_C.get(k).getN();
							LPRmeanC = LPRmeanC + accessionMeansList_C.get(k).getLPRmean()*nbOfPlantsC;
							NLRmeanC = NLRmeanC + accessionMeansList_C.get(k).getNLRmean()*nbOfPlantsC;
							SLRLmeanC = SLRLmeanC + accessionMeansList_C.get(k).getSLRLmean()*nbOfPlantsC;
							meanLRLmeanC = meanLRLmeanC + accessionMeansList_C.get(k).getMeanLRLmean()*nbOfPlantsC;
							DLRZ1meanC = DLRZ1meanC + accessionMeansList_C.get(k).getDLRZ1mean()*nbOfPlantsC;
							DLRZ2meanC = DLRZ2meanC + accessionMeansList_C.get(k).getDLRZ2mean()*nbOfPlantsC;
							
							//Debug
							System.out.println(name+" "+
											   concentration+" "+
											   nbOfPlantsC+" "+
											   LPRmeanC+" "+
											   NLRmeanC+" "+
											   SLRLmeanC+" "+
											   meanLRLmeanC+" "+
											   DLRZ1meanC+" "+
											   DLRZ2meanC);
						}
					}
				}
				
				for (int k = 0; k < accessionMeansList_D.size(); k++ ){
					if (accessionMeansList_D.get(k).getName().equals(name)) {
						if (accessionMeansList_D.get(k).getConcentration().equals(concentration)) {
							nbOfPlantsD = accessionMeansList_D.get(k).getN();
							LPRmeanD = LPRmeanD + accessionMeansList_D.get(k).getLPRmean()*nbOfPlantsD;
							NLRmeanD = NLRmeanD + accessionMeansList_D.get(k).getNLRmean()*nbOfPlantsD;
							SLRLmeanD = SLRLmeanD + accessionMeansList_D.get(k).getSLRLmean()*nbOfPlantsD;
							meanLRLmeanD = meanLRLmeanD + accessionMeansList_D.get(k).getMeanLRLmean()*nbOfPlantsD;
							DLRZ1meanD = DLRZ1meanD + accessionMeansList_D.get(k).getDLRZ1mean()*nbOfPlantsD;
							DLRZ2meanD = DLRZ2meanD + accessionMeansList_D.get(k).getDLRZ2mean()*nbOfPlantsD;
							
							//Debug
							System.out.println(name+" "+
											   concentration+" "+
											   nbOfPlantsD+" "+
											   LPRmeanD+" "+
											   NLRmeanD+" "+
											   SLRLmeanD+" "+
											   meanLRLmeanD+" "+
											   DLRZ1meanD+" "+
											   DLRZ2meanD);
						}
					}
				}
				
				for (int k = 0; k < accessionMeansList_E.size(); k++ ){
					if (accessionMeansList_E.get(k).getName().equals(name)) {
						if (accessionMeansList_E.get(k).getConcentration().equals(concentration)) {
							nbOfPlantsE = accessionMeansList_E.get(k).getN();
							LPRmeanE = LPRmeanE + accessionMeansList_E.get(k).getLPRmean()*nbOfPlantsE;
							NLRmeanE = NLRmeanE + accessionMeansList_E.get(k).getNLRmean()*nbOfPlantsE;
							SLRLmeanE = SLRLmeanE + accessionMeansList_E.get(k).getSLRLmean()*nbOfPlantsE;
							meanLRLmeanE = meanLRLmeanE + accessionMeansList_E.get(k).getMeanLRLmean()*nbOfPlantsE;
							DLRZ1meanE = DLRZ1meanE + accessionMeansList_E.get(k).getDLRZ1mean()*nbOfPlantsE;
							DLRZ2meanE = DLRZ2meanE + accessionMeansList_E.get(k).getDLRZ2mean()*nbOfPlantsE;
							
							//Debug
							System.out.println(name+" "+
											   concentration+" "+
											   nbOfPlantsE+" "+
											   LPRmeanE+" "+
											   NLRmeanE+" "+
											   SLRLmeanE+" "+
											   meanLRLmeanE+" "+
											   DLRZ1meanE+" "+
											   DLRZ2meanE);
						}
					}
				}
				
				for (int k = 0; k < accessionMeansList_F.size(); k++ ){
					if (accessionMeansList_F.get(k).getName().equals(name)) {
						if (accessionMeansList_F.get(k).getConcentration().equals(concentration)) {
							nbOfPlantsF = accessionMeansList_F.get(k).getN();
							LPRmeanF = LPRmeanF + accessionMeansList_F.get(k).getLPRmean()*nbOfPlantsF;
							NLRmeanF = NLRmeanF + accessionMeansList_F.get(k).getNLRmean()*nbOfPlantsF;
							SLRLmeanF = SLRLmeanF + accessionMeansList_F.get(k).getSLRLmean()*nbOfPlantsF;
							meanLRLmeanF = meanLRLmeanF + accessionMeansList_F.get(k).getMeanLRLmean()*nbOfPlantsF;
							DLRZ1meanF = DLRZ1meanF + accessionMeansList_F.get(k).getDLRZ1mean()*nbOfPlantsF;
							DLRZ2meanF = DLRZ2meanF + accessionMeansList_F.get(k).getDLRZ2mean()*nbOfPlantsF;
							
							//Debug
							System.out.println(name+" "+
											   concentration+" "+
											   nbOfPlantsF+" "+
											   LPRmeanF+" "+
											   NLRmeanF+" "+
											   SLRLmeanF+" "+
											   meanLRLmeanF+" "+
											   DLRZ1meanF+" "+
											   DLRZ2meanF);
						}
					}
				}
				
				for (int k = 0; k < accessionMeansList_G.size(); k++ ){
					if (accessionMeansList_G.get(k).getName().equals(name)) {
						if (accessionMeansList_G.get(k).getConcentration().equals(concentration)) {
							nbOfPlantsG = accessionMeansList_G.get(k).getN();
							LPRmeanG = LPRmeanG + accessionMeansList_G.get(k).getLPRmean()*nbOfPlantsG;
							NLRmeanG = NLRmeanG + accessionMeansList_G.get(k).getNLRmean()*nbOfPlantsG;
							SLRLmeanG = SLRLmeanG + accessionMeansList_G.get(k).getSLRLmean()*nbOfPlantsG;
							meanLRLmeanG = meanLRLmeanG + accessionMeansList_G.get(k).getMeanLRLmean()*nbOfPlantsG;
							DLRZ1meanG = DLRZ1meanG + accessionMeansList_G.get(k).getDLRZ1mean()*nbOfPlantsG;
							DLRZ2meanG = DLRZ2meanG + accessionMeansList_G.get(k).getDLRZ2mean()*nbOfPlantsG;
							
							//Debug
							System.out.println(name+" "+
											   concentration+" "+
											   nbOfPlantsG+" "+
											   LPRmeanG+" "+
											   NLRmeanG+" "+
											   SLRLmeanG+" "+
											   meanLRLmeanG+" "+
											   DLRZ1meanG+" "+
											   DLRZ2meanG);
						}
					}
				}
				
				for (int k = 0; k < accessionMeansList_H.size(); k++ ){
					if (accessionMeansList_H.get(k).getName().equals(name)) {
						if (accessionMeansList_H.get(k).getConcentration().equals(concentration)) {
							nbOfPlantsH = accessionMeansList_H.get(k).getN();
							LPRmeanH = LPRmeanH + accessionMeansList_H.get(k).getLPRmean()*nbOfPlantsH;
							NLRmeanH = NLRmeanH + accessionMeansList_H.get(k).getNLRmean()*nbOfPlantsH;
							SLRLmeanH = SLRLmeanH + accessionMeansList_H.get(k).getSLRLmean()*nbOfPlantsH;
							meanLRLmeanH = meanLRLmeanH + accessionMeansList_H.get(k).getMeanLRLmean()*nbOfPlantsH;
							DLRZ1meanH = DLRZ1meanH + accessionMeansList_H.get(k).getDLRZ1mean()*nbOfPlantsH;
							DLRZ2meanH = DLRZ2meanH + accessionMeansList_H.get(k).getDLRZ2mean()*nbOfPlantsH;
							
							//Debug
							System.out.println(name+" "+
											   concentration+" "+
											   nbOfPlantsH+" "+
											   LPRmeanH+" "+
											   NLRmeanH+" "+
											   SLRLmeanH+" "+
											   meanLRLmeanH+" "+
											   DLRZ1meanH+" "+
											   DLRZ2meanH);
						}
					}
				}
				
				for (int k = 0; k < accessionMeansList_I.size(); k++ ){
					if (accessionMeansList_I.get(k).getName().equals(name)) {
						if (accessionMeansList_I.get(k).getConcentration().equals(concentration)) {
							nbOfPlantsI = accessionMeansList_I.get(k).getN();
							LPRmeanI = LPRmeanI + accessionMeansList_I.get(k).getLPRmean()*nbOfPlantsI;
							NLRmeanI = NLRmeanI + accessionMeansList_I.get(k).getNLRmean()*nbOfPlantsI;
							SLRLmeanI = SLRLmeanI + accessionMeansList_I.get(k).getSLRLmean()*nbOfPlantsI;
							meanLRLmeanI = meanLRLmeanI + accessionMeansList_I.get(k).getMeanLRLmean()*nbOfPlantsI;
							DLRZ1meanI = DLRZ1meanI + accessionMeansList_I.get(k).getDLRZ1mean()*nbOfPlantsI;
							DLRZ2meanI = DLRZ2meanI + accessionMeansList_I.get(k).getDLRZ2mean()*nbOfPlantsI;
							
							//Debug
							System.out.println(name+" "+
											   concentration+" "+
											   nbOfPlantsI+" "+
											   LPRmeanI+" "+
											   NLRmeanI+" "+
											   SLRLmeanI+" "+
											   meanLRLmeanI+" "+
											   DLRZ1meanI+" "+
											   DLRZ2meanI);
						}
					}
				}
							
				totalNbOfPlants = nbOfPlantsA + 
								  nbOfPlantsB + 
								  nbOfPlantsC +
								  nbOfPlantsD +
								  nbOfPlantsE +
								  nbOfPlantsF +
								  nbOfPlantsG +
								  nbOfPlantsH +
								  nbOfPlantsI;
				
				System.out.println(totalNbOfPlants);
				
				if (!(totalNbOfPlants==0)) {
					LPRmean = (LPRmeanA+LPRmeanB+LPRmeanC+LPRmeanD+LPRmeanE+LPRmeanF+LPRmeanG+LPRmeanH+LPRmeanI)/totalNbOfPlants;
					NLRmean = (NLRmeanA+NLRmeanB+NLRmeanC+NLRmeanD+NLRmeanE+NLRmeanF+NLRmeanG+NLRmeanH+NLRmeanI)/totalNbOfPlants;
					SLRLmean = (SLRLmeanA+SLRLmeanB+SLRLmeanC+SLRLmeanD+SLRLmeanE+SLRLmeanF+SLRLmeanG+SLRLmeanH+SLRLmeanI)/totalNbOfPlants;
					meanLRLmean = (meanLRLmeanA+meanLRLmeanB+meanLRLmeanC+meanLRLmeanD+meanLRLmeanE+meanLRLmeanF+meanLRLmeanG+meanLRLmeanH+meanLRLmeanI)/totalNbOfPlants;
					DLRZ1mean = (DLRZ1meanA+DLRZ1meanB+DLRZ1meanC+DLRZ1meanD+DLRZ1meanE+DLRZ1meanF+DLRZ1meanG+DLRZ1meanH+DLRZ1meanI)/totalNbOfPlants;
					DLRZ2mean = (DLRZ2meanA+DLRZ2meanB+DLRZ2meanC+DLRZ2meanD+DLRZ2meanE+DLRZ2meanF+DLRZ2meanG+DLRZ2meanH+DLRZ2meanI)/totalNbOfPlants;
					
					//Debug
					System.out.println(name+" "+
									   concentration+" "+
									   totalNbOfPlants+" "+
									   LPRmean+" "+
									   NLRmean+" "+
									   SLRLmean+" "+
									   meanLRLmean+" "+
									   DLRZ1mean+" "+
									   DLRZ2mean);
					
					AccessionMeans finalAccessionMeans = new AccessionMeans();

					finalAccessionMeans.setName(name);
					finalAccessionMeans.setConcentration(concentration);
					finalAccessionMeans.setLPRmean(LPRmean);
					finalAccessionMeans.setNLRmean(NLRmean);
					finalAccessionMeans.setSLRLmean(SLRLmean);
					finalAccessionMeans.setMeanLRLmean(meanLRLmean);
					finalAccessionMeans.setDLRZ1mean(DLRZ1mean);
					finalAccessionMeans.setDLRZ2mean(DLRZ2mean);
					
					finalAccessionMeansList.add(finalAccessionMeans);				
				
				}
			}
		}
		
		// Debug
//		System.out.println(finalAccessionMeansList.size());
//	    for (int l = 0; l < finalAccessionMeansList.size(); l++ ){
//	    	System.out.println(finalAccessionMeansList.get(l).getName()+" "+
//	    			finalAccessionMeansList.get(l).getConcentration());
//	    }
	    
	    // Write the file
	    FileWriter f1 = new FileWriter(outputfilename);
		String source="";
		String currentName="";
		String currentConcentration="";
		Double currentLPRmean=0.0;
		Double currentNLRmean=0.0;
		Double currentSLRLmean=0.0;
		Double currentMeanLRLmean=0.0;
		Double currentDLRZ1mean=0.0;
		Double currentDLRZ2mean=0.0;
		
	    for (int l = 0; l < finalAccessionMeansList.size(); l++ ){
	    	currentName = finalAccessionMeansList.get(l).getName();
	    	currentConcentration = finalAccessionMeansList.get(l).getConcentration();
	    	currentLPRmean = finalAccessionMeansList.get(l).getLPRmean();
	    	currentNLRmean = finalAccessionMeansList.get(l).getNLRmean();
	    	currentSLRLmean = finalAccessionMeansList.get(l).getSLRLmean();
	    	currentMeanLRLmean = finalAccessionMeansList.get(l).getMeanLRLmean();
	    	currentDLRZ1mean = finalAccessionMeansList.get(l).getDLRZ1mean();
	    	currentDLRZ2mean = finalAccessionMeansList.get(l).getDLRZ2mean();
	    	
	    	source = currentName+";"+
	    			 currentConcentration+";"+
					 roundDouble(currentLPRmean,"#.##")+";"+
					 roundDouble(currentNLRmean,"#.##")+";"+
					 roundDouble(currentSLRLmean,"#.##")+";"+
					 roundDouble(currentMeanLRLmean,"#.##")+";"+
					 roundDouble(currentDLRZ1mean,"#.##")+";"+
					 roundDouble(currentDLRZ2mean,"#.##")+"\r\n";

			// Debug
		    System.out.println(currentName+";"+
	    			 currentConcentration+";"+
					 roundDouble(currentLPRmean,"#.##")+";"+
					 roundDouble(currentNLRmean,"#.##")+";"+
					 roundDouble(currentSLRLmean,"#.##")+";"+
					 roundDouble(currentMeanLRLmean,"#.##")+";"+
					 roundDouble(currentDLRZ1mean,"#.##")+";"+
					 roundDouble(currentDLRZ2mean,"#.##"));
			
			String newSource = source.replace(".", ",");
			f1.write(newSource);
	    }

//		//Debug
//    	System.out.println(accessionSummaryList_A.size());
//    	System.out.println(accessionSummaryList_B.size());
//    	System.out.println(accessionSummaryList_C.size());
    	
		f1.close();
	}	
	
	//------------------------------------------------------------------------------------------------------------------
	private static List<AccessionMeans> getAccessionMeansList(File infile){
		
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;
		
		// variables to store the data
		String accession;
		String concentration;
		String box;
		int nbofplants;
		Double LPRmean=0.0;
		Double NLRmean=0.0;
		Double SLRLmean=0.0;
		Double meanLRLmean=0.0;
		Double DLRZ1mean=0.0;
		Double DLRZ2mean=0.0;
		
		List<AccessionMeans> myAccessionMeansList = new ArrayList<AccessionMeans>();
		
		try {
			fis = new FileInputStream(infile);
		    bis = new BufferedInputStream(fis);
		    dis = new DataInputStream(bis);
		    
		    while (dis.available() != 0) {
		    	   	
		    	String line = dis.readLine();
		    	
		    	accession = getStringLineItem(line,0,";");
		    	concentration = getStringLineItem(line,1,";");
		    	nbofplants = getIntegerLineItem(line,2,";");
		    	box = getStringLineItem(line,3,";");
		    	LPRmean = getDoubleLineItem(line,4,";");
		    	NLRmean = getDoubleLineItem(line,5,";");
		    	SLRLmean = getDoubleLineItem(line,6,";");
		    	meanLRLmean = getDoubleLineItem(line,7,";");
		    	DLRZ1mean = getDoubleLineItem(line,8,";");
		    	DLRZ2mean = getDoubleLineItem(line,9,";");
		    	
		    	AccessionMeans myAccessionMeans = new AccessionMeans();
		    	
		    	myAccessionMeans.setName(accession);
		    	myAccessionMeans.setConcentration(concentration);
		    	myAccessionMeans.setBox(box);
		    	myAccessionMeans.setN(nbofplants);
		    	myAccessionMeans.setLPRmean(LPRmean);
		    	myAccessionMeans.setNLRmean(NLRmean);
		    	myAccessionMeans.setSLRLmean(SLRLmean);
		    	myAccessionMeans.setMeanLRLmean(meanLRLmean);
		    	myAccessionMeans.setDLRZ1mean(DLRZ1mean);
		    	myAccessionMeans.setDLRZ2mean(DLRZ2mean);
		    	
		    	myAccessionMeansList.add(myAccessionMeans);	 
		    }
		    
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	

		return myAccessionMeansList;
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
				sheet.addCell(new Label(10, 0, "Z2 Length", headerInformationFormat));
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
				sheet.addCell(new Label(5,j+1, " ± ", InformationFormat));
				sheet.addCell(new Number(6,j+1,accessionsList.get(j).getLPRse(),cf2 ));
				sheet.addCell(new Number(7,j+1,accessionsList.get(j).getNLRmean(),cf2 ));
				sheet.addCell(new Label(8,j+1, " ± ", InformationFormat));
				sheet.addCell(new Number(9,j+1,accessionsList.get(j).getNLRse(),cf2 ));
				sheet.addCell(new Number(10,j+1,accessionsList.get(j).getSLRLmean(),cf2 ));
				sheet.addCell(new Label(11,j+1, " ± ", InformationFormat));
				sheet.addCell(new Number(12,j+1,accessionsList.get(j).getSLRLse(),cf2 ));
				sheet.addCell(new Number(13,j+1,accessionsList.get(j).getMeanLRLmean(),cf2 ));
				sheet.addCell(new Label(14,j+1, " ± ", InformationFormat));
				sheet.addCell(new Number(15,j+1,accessionsList.get(j).getMeanLRLse(),cf2 ));
				sheet.addCell(new Number(16,j+1,accessionsList.get(j).getDLRZ1mean(),cf2 ));
				sheet.addCell(new Label(17,j+1, " ± ", InformationFormat));
				sheet.addCell(new Number(18,j+1,accessionsList.get(j).getDLRZ1se(),cf2 ));
				sheet.addCell(new Number(19,j+1,accessionsList.get(j).getDLRZ2mean(),cf2 ));
				sheet.addCell(new Label(20,j+1, " ± ", InformationFormat));
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