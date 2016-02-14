package huju.mcu.comm;

import com.pi4j.io.serial.Serial;
import huju.mcu.ServiceProvider;

import huju.mcu.comm.pi4j.PI4JSerialCommunicator;

public class SerialCommunicatorProvider 
{
	public static String RASBPERRY_PI = "RASPI";
	public static String WINDOWS = "Windows";
	
	/**
	 * Return platform specific SerialCommunicator.
	 * 
	 * @param platform String as platform, RASBPERRY_PI or WINDOWS
	 * @return SerialCommunicator instance.
	 */
	public static SerialCommunicator getSerialCommunicator(String platform)
	{
		if (ServiceProvider.RASBPERRY_PI.equals(platform)) {
			return new PI4JSerialCommunicator();
		} 
		throw new IllegalArgumentException("Unsupported platform: "+platform);
	}
	
	public static String getDefaultPort(String platform) {
		if (ServiceProvider.RASBPERRY_PI.equals(platform)) {
			return Serial.DEFAULT_COM_PORT;
		} 

               
		throw new IllegalArgumentException("Unsupported platform: "+platform);
	}
}
