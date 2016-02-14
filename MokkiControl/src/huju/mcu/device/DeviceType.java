package huju.mcu.device;

/**
 * Enumeration of DeviceType.
 * 
 * DeviceType field defines an actual implementation class of the MCUData. 
 */
public enum DeviceType 
{
	UNDEFINED(0),
	TEMPERATURE_HUMIDITY_SENSOR (1),
	TEMPERATURE_SENSOR (2),
	MOISTURE_SENSOR (3),
	MOTION_DETECTOR (4),
	DISTANCE_SENSOR (5),
	ELECTRICITY_PULSE_DETECTOR (6),
	UNKNOWN (999);
	
	private final int deviceTypeCode;
	
	DeviceType(int deviceTypeCode) {
        this.deviceTypeCode = deviceTypeCode;
    }
    
    public int getDeviceTypeCode() {
    	return this.deviceTypeCode;
    }	
    
    public static DeviceType getDeviceType(String name) 
    {
    	for (DeviceType dt : values()) {
    		if (dt.name().equals(name)) {
    			return dt;
    		}
    	}
    	return UNDEFINED;
    }	
	
    public String toString() {
    	if (deviceTypeCode == TEMPERATURE_HUMIDITY_SENSOR.getDeviceTypeCode()) 
    			return "Humidity/Temperture sensor";
    	if (deviceTypeCode == TEMPERATURE_SENSOR.getDeviceTypeCode()) 
    			return "Temperture sensor";
    	if (deviceTypeCode == MOISTURE_SENSOR.getDeviceTypeCode()) 
    			return "Moisture sensor";
    	if (deviceTypeCode == MOTION_DETECTOR.getDeviceTypeCode()) 
    			return "Motion detector";
    	if (deviceTypeCode == DISTANCE_SENSOR.DISTANCE_SENSOR.getDeviceTypeCode()) 
    			return "Distance sensor";
    	if (deviceTypeCode == ELECTRICITY_PULSE_DETECTOR.getDeviceTypeCode())
    		return "Electricity meter";
    	return "Unknown type";
    }
    
    public static DeviceType getDeviceType(int deviceTypeCode) 
    {
	if (deviceTypeCode == TEMPERATURE_HUMIDITY_SENSOR.getDeviceTypeCode()) 
			return DeviceType.TEMPERATURE_HUMIDITY_SENSOR;
    	if (deviceTypeCode == TEMPERATURE_SENSOR.getDeviceTypeCode()) 
    			return TEMPERATURE_SENSOR;
    	if (deviceTypeCode == MOISTURE_SENSOR.getDeviceTypeCode()) 
    			return MOISTURE_SENSOR;
    	if (deviceTypeCode ==MOTION_DETECTOR.getDeviceTypeCode()) 
    			return MOTION_DETECTOR;
    	if (deviceTypeCode == DISTANCE_SENSOR.DISTANCE_SENSOR.getDeviceTypeCode()) 
    			return DISTANCE_SENSOR;
    	if (deviceTypeCode == ELECTRICITY_PULSE_DETECTOR.getDeviceTypeCode())
    		return DeviceType.ELECTRICITY_PULSE_DETECTOR;
		return DeviceType.UNKNOWN;
    }
}
