package huju.mcu;

import huju.mcu.datatypes.HumidityTemperature;
import huju.mcu.datatypes.MCUData;
import huju.mcu.datatypes.MCUDevice;
import huju.mcu.device.DeviceId;
import huju.mcu.comm.CommunicationDataListener;
import huju.mcu.service.MCUDataFilter;
import java.io.Reader;

public interface MCUDataService 
{	
	public void setDeviceConfig(Reader configXml);
	
	public MCUDevice getDevice(DeviceId id) throws DeviceConfigException;
	
	public MCUDevice[] getDevices() throws DeviceConfigException;;
	
	public void sendData(DeviceId id, MCUData request) throws DeviceNotFoundException;
	
        /**
	 * Read e device data synchronously.
	 * @param device
	 * @return
	 */
	public MCUData readDeviceValue(DeviceId id, int timeout) throws DeviceNotFoundException;
	
	public void addServiceListener(ServiceListener listener);
	
	public void removeServiceListener(ServiceListener listener);
	
	public void addDataFilter(MCUDataFilter filter);
	
	public void removeDataFilter(MCUDataFilter filter);
}
