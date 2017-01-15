package org.cfrehr.projectfuzz;

import org.cfrehr.projectfuzz.importrecord.*;
import org.cfrehr.projectfuzz.transporter.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class Matcher {
	
	// main method
	public static void main(String args[]) {
		
		// read training data to create transporter database
		TransporterDB transporterDB = new TransporterDB();
		try {
			File train = new File(ClassLoader.getSystemResource("train.csv").getFile());
			Scanner in = new Scanner(train);
			in.useDelimiter(",");
			in.nextLine();	// skip file header
			while(in.hasNextLine()) {
				String currLine = in.nextLine();
				String[] tFields = currLine.split(",");
				transporterDB.addTransporter(new Transporter(Integer.parseInt(tFields[0]), tFields[1], tFields[2]));
			}
			in.close();
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// read testing data to create database of new records
		ImportRecordDB recordDB = new ImportRecordDB();
		try {
			File test = new File(ClassLoader.getSystemResource("test.csv").getFile());
			Scanner in = new Scanner(test);
			in.useDelimiter(",");
			in.nextLine();	// skip file header
			while(in.hasNextLine()) {
				String currLine = in.nextLine();
				String[] iFields = currLine.split(",");
				recordDB.addRecord(new ImportRecord(Integer.parseInt(iFields[0]), Integer.parseInt(iFields[1]), iFields[2]));
			}
			in.close();
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// perform matching
		
		/* TEST: WORKS
		String testA = "hello";
		String testB = "hell";
		String testC = "hello kitty";
		String testD = "marshmallow";
		int one = lev(testA,testB);
		int two = lev(testA,testC);
		int three = lev(testA,testD);
		System.out.println("Distance between '" + testA + "' and '" + testB + "' is:  " + Integer.toString(one));
		System.out.println("Distance between '" + testA + "' and '" + testC + "' is:  " + Integer.toString(two));
		System.out.println("Distance between '" + testA + "' and '" + testD + "' is:  " + Integer.toString(three));
		*/
		
		// for each ImportRecord in ImportRecordDB, compare pipeline name to each known pipeline in TransporterDB
		int recordCount = recordDB.getSize();
		for(int i=1; i<=recordCount; i++) {
			ImportRecord currRecord = recordDB.getRecord(i);
			Transporter closestTransporter = transporterDB.getTransporter(1);
			int shortestDistance = 100; // initialize to some value sufficiently large enough that it will be replaced
			int transporterCount = transporterDB.getSize();
			Transporter currTransporter = null;
			for(int j=1; j<=transporterCount; j++) {
				currTransporter = transporterDB.getTransporter(j);
				// compute distance
				int distance = lev(currRecord.getPipeline(),currTransporter.getPipeline());
				// update closest transporter and distance 
				if(distance < shortestDistance) {
					shortestDistance = distance;
					closestTransporter = currTransporter;
				}
			}
			// predict the transporter for currRecord and note their distance
			currRecord.predictTransporter(closestTransporter.getID());
			currRecord.setDistance(shortestDistance);
		}
		
		// print matching accuracy results
		double recordAccuracy = accuracy(recordDB);
		System.out.println("\n" + "Overall Record Matching Accuracy: " + recordAccuracy);
		// get correct and incorrect matches
		ArrayList<ImportRecord> correctMatches = new ArrayList<ImportRecord>();
		ArrayList<ImportRecord> incorrectMatches = new ArrayList<ImportRecord>();
		String prevPipeline = "";
		for(int i=1; i<=recordCount; i++) {
			ImportRecord currRecord = recordDB.getRecord(i);
			String currPipeline = currRecord.getPipeline();
			// if repeat record, do nothing
			if(currPipeline.equals(prevPipeline)) {
				// do nothing
			// else if correctly matched, add to correct matches
			} else if(currRecord.getPredictedTransporterID() == currRecord.getActualTransporterID()) {
				correctMatches.add(currRecord);
			// else, add to incorrect matches
			} else {
				incorrectMatches.add(currRecord);
			}
			prevPipeline = currPipeline;
		}
		int cSize = correctMatches.size();
		int iSize = incorrectMatches.size();
		double pipeAccuracy = ((double) cSize) / (cSize + iSize);
		System.out.println("Pipeline Name Matching Accuracy:  " + pipeAccuracy + "\n");
		// print correct and incorrect matches
		System.out.println(cSize + " Correctly Mapped Pipelines Names:" + "\n");
		for(int i=0; i<cSize; i++) {
			ImportRecord currRecord = correctMatches.get(i);
			System.out.println(currRecord.getPipeline() + "  :  " 
					+ transporterDB.getTransporter(currRecord.getPredictedTransporterID()).getPipeline() 
					+ "  :  " + currRecord.getDistance());
		}
		System.out.println("\n" + iSize + " Incorrectly Mapped Pipeline Names:" + "\n");
		for(int i=0; i<iSize; i++) {
			ImportRecord currRecord = incorrectMatches.get(i);
			System.out.println(currRecord.getPipeline() + "  :  " 
					+ transporterDB.getTransporter(currRecord.getPredictedTransporterID()).getPipeline() 
					+ "  :  " + currRecord.getDistance());
		}
	}
	
	
	// Levenshtein Distance
	// in actual implementation, may want to uppercase all chars and trim spaces
	// limitations: length of string. Similarly sized strings are more likely to match. May need an ensemble method of algos and then learn the weights
	public static int lev(String a, String b) {
		
		// NULL CHECK
		if (a==null || b==null) {
			return -1;
		}
		
		// EMPTY STRING CHECK
		int I = a.length();
		int J = b.length();
		if (I==0 || J==0) {
			return -2;
		}
		
		// CALCULATE LEVENSHTEIN DISTANCE
		// create string arrays
		char[] aChars = a.toCharArray();
		char[] bChars = b.toCharArray();
		// initialize table
		int[][] levTable = new int[I+1][J+1];
		for(int i=0; i<=I; i++) {
			levTable[i][0] = i;
		}
		for(int j=0; j<=J; j++) {
			levTable[0][j] = j;
		}
		// calculate each distance for levTable[i][j]
		for(int i=1; i<=I; i++) {
			for (int j=1; j<=J; j++) {
				// find minimum surrounding distance
				int left = levTable[i][j-1];
				int diag = levTable[i-1][j-1];
				int up = levTable[i-1][j];
				int minimum;
				if(left<=diag && left<=up) {
					minimum = left;
				} else if(diag<=left && diag<=up) {
					minimum = diag;
				} else {
					minimum = up;
				}
				// calculate final distance
				if(aChars[i-1]==bChars[j-1]) {
					levTable[i][j] = minimum;
				} else {
					levTable[i][j] = minimum + 1;
				}
				
			}
		}
		// return overall distance between strings
		return levTable[I][J];
	}
	
	// calculate predictive accuracy
	public static double accuracy(ImportRecordDB recordDB) {
		int recordCount = recordDB.getSize();
		int correctMappingCount = 0;
		for(int i=1; i<=recordCount; i++) {
			if(recordDB.getRecord(i).getPredictedTransporterID() == recordDB.getRecord(i).getActualTransporterID()) { 
				correctMappingCount++;
			}
		}
		return ((double) correctMappingCount) / recordCount;
	}
}