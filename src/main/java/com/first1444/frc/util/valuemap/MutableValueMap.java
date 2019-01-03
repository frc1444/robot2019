package com.first1444.frc.util.valuemap;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;

public class MutableValueMap<T extends Enum<T> & ValueKey> extends ValueMapBase<T>{
	private final Class<T> clazz;
	MutableValueMap(Class<T> enumClass) {
		super(new EnumMap<>(enumClass), Collections.unmodifiableSet(EnumSet.allOf(enumClass)));
		this.clazz = enumClass;

		for(T key : valueKeys){
			Object defaultValue = key.getDefaultValue();
			if(key.getValueType() == ValueType.DOUBLE){
				defaultValue = ((Number) defaultValue).doubleValue(); // make it a double
			}
			map.put(key, defaultValue);
		}
	}
	public MutableValueMap<T> setBoolean(T key, boolean value){
		checkKey(key, ValueType.BOOLEAN);
		map.put(key, value);
		return this;
	}
	public MutableValueMap<T> setDouble(T key, double value){
		checkKey(key, ValueType.DOUBLE);
		map.put(key, value);
		return this;
	}
	public MutableValueMap<T> setString(T key, String value){
		checkKey(key, ValueType.STRING);
		map.put(key, value);
		return this;
	}

	public ValueMap<T> build(){
		return new ImmutableValueMap<>(clazz, (EnumMap<T, Object>) map);
	}
}
