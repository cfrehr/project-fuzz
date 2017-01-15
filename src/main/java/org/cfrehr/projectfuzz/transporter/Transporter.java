package org.cfrehr.projectfuzz.transporter;

// A Transporter is a pipeline that has a ID and Description
public class Transporter {
	
	// fields
	private int ID;
	private String pipeline;
	private String description;
	
	// constructor
	public Transporter(int ID, String pipeline, String description) {
		this.ID = ID;
		this.pipeline = pipeline;
		this.description = description;
	}
	
	// getters
	public int getID() {
		return this.ID;
	}
	
	public String getPipeline() {
		return this.pipeline;
	}
	
	public String getDescription() {
		return this.description;
	}
	
}