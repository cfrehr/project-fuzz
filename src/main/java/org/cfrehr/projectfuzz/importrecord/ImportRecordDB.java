package org.cfrehr.projectfuzz.importrecord;

import java.util.Map;
import java.util.HashMap;

// An ImportRecordDB is a database of new ImportRecords
public class ImportRecordDB {
	
	// fields
	private Map<Integer,ImportRecord> records = new HashMap<Integer,ImportRecord>();
	
	// constructor
	public ImportRecordDB() {
	}
	
	// getters
	public ImportRecord getRecord(int recordID) {
		return this.records.get(recordID);
	}
	
	public int getSize() {
		return records.size();
	}
	
	// setters
	public void addRecord(ImportRecord record) {
		this.records.put(record.getID(), record);
	}
	
}