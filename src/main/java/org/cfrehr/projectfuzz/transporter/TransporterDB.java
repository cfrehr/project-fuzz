package org.cfrehr.projectfuzz.transporter;

import java.util.Map;
import java.util.HashMap;

//TransporterDB is a pipeline dictionary to reference for matching purposes, containing a list of individual Transporters
public class TransporterDB {
	
	// fields
	private Map<Integer,Transporter> transporters = new HashMap<Integer,Transporter>();
	
	// constructor
	public TransporterDB() {
	}
	
	// getters
	public Transporter getTransporter(int transporterID) {
		return transporters.get(transporterID);
	}
	
	public int getSize() {
		return transporters.size();
	}
	
	// setters
	public void addTransporter(Transporter transporter) {
		this.transporters.put(transporter.getID(),transporter);
	}
	
}