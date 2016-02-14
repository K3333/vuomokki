package huju.mcu.comm;

import huju.mcu.datatypes.MCUData;

public interface CommunicationDataListener 
{
	public void dataReceived(MCUData data);
}
