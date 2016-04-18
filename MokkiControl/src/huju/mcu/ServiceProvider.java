package huju.mcu;

import huju.mcu.comm.SerialCommunicator;
import huju.mcu.comm.SerialCommunicatorProvider;
import huju.mcu.service.MCUDataServiceImpl;

public class ServiceProvider
{
	public static String RASBPERRY_PI = "RASPI";
	public static String WINDOWS = "Windows";
        public static String DEFAULT_PLATFORM = RASBPERRY_PI;
	
	private static MCUDataService dataService;
	
	public  static MCUDataService getDataService(String platform) 
	{
		if (dataService == null) {
			SerialCommunicator comm = SerialCommunicatorProvider.getSerialCommunicator(platform);
			if (comm.openPort(SerialCommunicatorProvider.getDefaultPort(platform), 115200)) {
				System.out.println("Serial port opened successfully");
			} else {
				System.out.println("Failed to open comm!");
			}
			dataService = new MCUDataServiceImpl(comm);
		}

		return dataService;
	}

}
