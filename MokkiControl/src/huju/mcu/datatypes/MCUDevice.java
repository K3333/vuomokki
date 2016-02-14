package huju.mcu.datatypes;

import huju.mcu.device.ActionType;
import huju.mcu.device.DeviceId;
import huju.mcu.device.DeviceType;
import huju.mcu.device.SourceBus;

/**
 * Wrapper to device configured in DeviceConfig xml file. 
 * @author huju
 *
 */
public class MCUDevice 
{
	private int	mcuMaster;
	private DeviceId deviceId;
	private DeviceType deviceType;
	private SourceBus sourceBus;
	private String deviceInfo;
	private String description; 
	
	public int getMcuMaster() 
	{
		return mcuMaster;
	}
	public void setMcuMaster(int mcuMaster) {
		this.mcuMaster = mcuMaster;
	}
	public DeviceId getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(DeviceId deviceId) {
		this.deviceId = deviceId;
	}
	public DeviceType getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}
	public SourceBus getSourceBus() {
		return sourceBus;
	}
	public void setSourceBus(SourceBus sourceBus) {
		this.sourceBus = sourceBus;
	}
	public String getDeviceInfo() {
		return deviceInfo;
	}
	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public MCUData getDefautReadRequest() 
	{
		return new DefaultRequestData(ActionType.COMMAND_REQUEST, deviceType, sourceBus);
	}
	
}
