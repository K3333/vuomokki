package huju.mcu.device;

public enum DeviceId {
	UNDEFINED(0),
	HUM_TEM_SENSOR_1(1),
	HUM_TEM_SENSOR_2(2),
	HUM_TEM_SENSOR_3(3),
	HUM_TEM_SENSOR_4(4),
	MOTION_DETECTOR_1(5);
	private int deviceCode;
	DeviceId(int deviceCode) { this.deviceCode = deviceCode; }
	public int getDeviceCode() {
		return deviceCode;
	}
	
	public static DeviceId getDeviceId(String name) 
    {
    	for (DeviceId id : values()) {
    		if (id.name().equals(name)) {
    			return id;
    		}
    	}
    	return UNDEFINED;
    }
	
	public static DeviceId getDeviceId(int code) 
    {
		for (DeviceId id : values()) {
			if (id.deviceCode == code) {
				return id;
			}
		}
		return UNDEFINED;
    }

}
