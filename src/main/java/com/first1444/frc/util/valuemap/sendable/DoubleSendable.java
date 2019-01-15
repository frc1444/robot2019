package com.first1444.frc.util.valuemap.sendable;

import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.WidgetType;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

/**
 * @deprecated untested
 */
@Deprecated
class DoubleSendable extends SendableBase {

	private final DoubleSupplier doubleSupplier;
	private final DoubleConsumer doubleConsumer;
	private final WidgetType widgetType;

	public DoubleSendable(DoubleSupplier doubleSupplier, DoubleConsumer doubleConsumer, WidgetType widgetType) {
		this.doubleSupplier = doubleSupplier;
		this.doubleConsumer = doubleConsumer;
		this.widgetType = widgetType;
	}
	public DoubleSendable(DoubleSupplier doubleSupplier, DoubleConsumer doubleConsumer){
		this(doubleSupplier, doubleConsumer, BuiltInWidgets.kNumberSlider);
	}

	@Override
	public void initSendable(SendableBuilder builder) {
		builder.setSmartDashboardType(widgetType.getWidgetName());
		builder.addDoubleProperty("Value", doubleSupplier, doubleConsumer);
	}
}
