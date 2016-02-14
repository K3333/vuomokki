package huju.mcu;

import huju.mcu.datatypes.MCUData;

public interface ServiceListener {
	public void dataReceived(MCUData data);
}
