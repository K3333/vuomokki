package huju.mcu.datatypes;

import huju.mcu.device.ActionType;
import huju.mcu.device.DeviceType;
import huju.mcu.device.SourceBus;

public class DefaultRequestData extends MCUData 
{
	public DefaultRequestData(ActionType dataType, DeviceType deviceType, SourceBus sourceBus)
	{
		super(dataType, deviceType, sourceBus);
	}

	public static MCUData getDefautReadRequest(MCUDevice mcuDevice)
	{
		return new DefaultRequestData(ActionType.COMMAND_REQUEST, mcuDevice.getDeviceType(), mcuDevice.getSourceBus());
	}
}
