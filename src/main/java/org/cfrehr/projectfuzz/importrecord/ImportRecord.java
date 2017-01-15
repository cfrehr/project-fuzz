package org.cfrehr.projectfuzz.importrecord;

// An ImportRecord is a single row from a data import that has a RecordID and needs its predictedTransporterID to be mapped
public class ImportRecord {
	
	// fields
	private int recordID;
	private int actualTransporterID;
	private int predictedTransporterID;
	private int distance;
	private String pipeline = null;
	
	// constructor
	public ImportRecord(int recordID, int transporterID, String pipeline) {
		this.recordID = recordID;
		this.actualTransporterID = transporterID;
		this.predictedTransporterID = -1;
		this.distance = -1;
		this.pipeline = pipeline;
	}
	
	// getters
	public int getID() {
		return this.recordID;
	}
	
	public int getActualTransporterID() {
		return this.actualTransporterID;
	}
	
	public int getPredictedTransporterID() {
		return this.predictedTransporterID;
	}
	
	public int getDistance() {
		return this.distance;
	}
	
	public String getPipeline() {
		return this.pipeline;
	}
	
	public boolean isMatch() {		
		if(this.actualTransporterID == this.predictedTransporterID) {
			return true;
		} else {
			return false;
		}
	}
	
	// setters
	public void predictTransporter(int transporterID) {
		this.predictedTransporterID = transporterID;
	}
	
	public void setDistance(int distance) {
		this.distance = distance;
	}
}