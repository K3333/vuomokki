package huju.mcu.datatypes;

import huju.mcu.device.ActionType;
import huju.mcu.device.DeviceId;
import huju.mcu.device.DeviceType;
import huju.mcu.device.SourceBus;
import huju.mcu.schemas.DisplayElement;
import java.util.List;

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
	private long dataStoreInterval = -1;
	private long cacheUpdateInterval = -1;
	private long singleTriggerDelay = -1;
	
	private List<DisplayElement> displayData;
	
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

	/**
	 * @return the displayData
	 */
	public List<DisplayElement> getDisplayData()
	{
		return displayData;
	}

	/**
	 * @param displayData the displayData to set
	 */
	public void setDisplayData(List<DisplayElement> displayData)
	{
		this.displayData = displayData;
	}

	/**
	 * @return the dataStoreInterval
	 */
	public long getDataStoreInterval()
	{
		return dataStoreInterval;
	}

	/**
	 * @param dataStoreInterval the dataStoreInterval to set
	 */
	public void setDataStoreInterval(long dataStoreInterval)
	{
		this.dataStoreInterval = dataStoreInterval;
	}

	/**
	 * @return the cacheUpdateInterval
	 */
	public long getCacheUpdateInterval()
	{
		return cacheUpdateInterval;
	}

	/**
	 * @param cacheUpdateInterval the cacheUpdateInterval to set
	 */
	public void setCacheUpdateInterval(long cacheUpdateInterval)
	{
		this.cacheUpdateInterval = cacheUpdateInterval;
	}

	/**
	 * @return the singleTriggerDelay
	 */
	public long getSingleTriggerDelay()
	{
		return singleTriggerDelay;
	}

	/**
	 * @param singleTriggerDelay the singleTriggerDelay to set
	 */
	public void setSingleTriggerDelay(long singleTriggerDelay)
	{
		this.singleTriggerDelay = singleTriggerDelay;
	}
	
	
}
