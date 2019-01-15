package com.first1444.frc.util.valuemap.sendable;

import com.first1444.frc.util.valuemap.MutableValueMap;
import com.first1444.frc.util.valuemap.ValueKey;
import com.first1444.frc.util.valuemap.ValueMap;
import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableValue;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;

import java.util.HashMap;
import java.util.Map;

public class ValueMapLayout<T extends Enum<T> & ValueKey> {
	private final MutableValueMap<T> valueMap;
	private final Map<T, NetworkTableEntry> entryMap;

	public ValueMapLayout(Class<T> clazz, String title, ShuffleboardContainer container){
		valueMap = new MutableValueMap<>(clazz);
		entryMap = new HashMap<>();

		final ShuffleboardLayout layout = container.getLayout(title);
		for(final T key : valueMap.getValueKeys()){
			final NetworkTableEntry entry = layout.add(key.getName(), key.getDefaultValue())
					.getEntry();
			entryMap.put(key, entry);

			entry.addListener(
					entryNotification -> {
						final NetworkTableValue value = entryNotification.value;
						switch(key.getValueType()){
							case STRING:
								valueMap.setString(key, value.getString());
								break;
							case BOOLEAN:
								valueMap.setBoolean(key, value.getBoolean());
								break;
							case DOUBLE:
								valueMap.setDouble(key, value.getDouble());
								break;
						}
					},
					EntryListenerFlags.kUpdate
			);
		}
		valueMap.addListener((key) -> {
			final NetworkTableEntry entry = entryMap.get(key);
			switch(key.getValueType()){
				case STRING:
					entry.setString(valueMap.getString(key));
					break;
				case BOOLEAN:
					entry.setBoolean(valueMap.getBoolean(key));
					break;
				case DOUBLE:
					entry.setDouble(valueMap.getDouble(key));
					break;
			}
		});
	}
	public MutableValueMap<T> getMutableValueMap(){
		return valueMap;
	}
	public ValueMap<T> getImmutableValueMap(){
		return valueMap.build();
	}
}
