package com.first1444.frc.util;

import edu.wpi.first.networktables.NetworkTableEntry;

import java.util.function.Supplier;

/**
 * @deprecated untested class
 */
@Deprecated
public class DynamicEntry {
	private final Supplier<NetworkTableEntry> entrySupplier;
	private NetworkTableEntry entry = null;

	public DynamicEntry(Supplier<NetworkTableEntry> entrySupplier){
		this.entrySupplier = entrySupplier;
	}
	public void show(){
		if(entry == null){
			entry = entrySupplier.get();
		}
	}
	public void hide(){
		if(entry != null){
			entry.delete();
		}
	}
	public void reset(){
		boolean shown = getEntry() != null;
		if(entry != null){
			entry.delete();
			entry = null;
		}
		if(shown){
			show();
		}
	}
	public NetworkTableEntry getEntry(){
		if(!entry.exists() || !entry.isValid()){
			entry = null;
		}
		return entry;
	}
}
