package com.first1444.frc.util;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class DynamicSendableChooser<V> extends SendableBase {
	private static final String DEFAULT = "default";
	private static final String SELECTED = "selected";
	private static final String ACTIVE = "active";
	private static final String OPTIONS = "options";
	private static final String INSTANCE = ".instance";

	private static final AtomicInteger instanceCounter = new AtomicInteger();

	private final int instanceNumber;
	private final Map<String, V> map = new LinkedHashMap<>();
	private String defaultChoiceKey = "";
	private String selectedKey;

	private final List<NetworkTableEntry> activeEntries = new ArrayList<>();
	private final ReentrantLock mutex = new ReentrantLock();

	public DynamicSendableChooser() {
		super(false);
		instanceNumber = instanceCounter.getAndIncrement();
	}

	public void addOption(String name, V object) {
		map.put(name, object);
	}

	/**
	 * Adds the given object to the list of options and marks it as the default. Functionally, this is
	 * very close to {@link #addOption(String, Object)} except that it will use this as the default
	 * option if none other is explicitly selected.
	 *
	 * @param name	 the name of the option
	 * @param object the option
	 */
	public void setDefaultOption(String name, V object) {
		Objects.requireNonNull(name, "Provided name was null");

		defaultChoiceKey = name;
		addOption(name, object);
	}

	public void removeOption(String name){
		map.remove(name);
	}
	public void reset(){
		defaultChoiceKey = "";
		selectedKey = null;
		map.clear();
	}


	/**
	 * Returns the selected option. If there is none selected, it will return the default. If there is
	 * none selected and no default, then it will return {@code null}.
	 *
	 * @return the option selected
	 */
	public V getSelected() {
		mutex.lock();
		try {
			if (selectedKey != null) {
				return map.get(selectedKey);
			} else {
				if(defaultChoiceKey == null){
					return null;
				}
				return map.get(defaultChoiceKey);
			}
		} finally {
			mutex.unlock();
		}
	}


	@Override
	public void initSendable(SendableBuilder builder) {
		builder.setSmartDashboardType("String Chooser");
		builder.getEntry(INSTANCE).setDouble(instanceNumber);
		builder.addStringProperty(DEFAULT, () -> defaultChoiceKey, null);
		builder.addStringArrayProperty(OPTIONS, () -> map.keySet().toArray(new String[0]), null);
		builder.addStringProperty(ACTIVE, () -> {
			mutex.lock();
			try {
				if (selectedKey != null) {
					return selectedKey;
				} else {
					return defaultChoiceKey;
				}
			} finally {
				mutex.unlock();
			}
		}, null);
		mutex.lock();
		try {
			activeEntries.add(builder.getEntry(ACTIVE));
		} finally {
			mutex.unlock();
		}
		builder.addStringProperty(SELECTED, null, val -> {
			mutex.lock();
			try {
				selectedKey = val;
				for (NetworkTableEntry entry : activeEntries) {
					entry.setString(val);
				}
			} finally {
				mutex.unlock();
			}
		});
	}
}
