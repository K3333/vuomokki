package huju.mcu.service;

import huju.mcu.datatypes.MCUData;
import huju.mcu.device.DeviceType;
import huju.mcu.device.SourceBus;

/**
 * Interface for data filters.
 * .
 * @author huju
 */
public interface MCUDataFilter 
{
   public DeviceType getDeviceType();
   public SourceBus getSourceBus();
   public boolean processData(MCUData data);
}
