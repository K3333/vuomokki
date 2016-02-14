package test;

import huju.mcu.DeviceConfigException;
import huju.mcu.DeviceNotFoundException;
import huju.mcu.MCUDataService;
import huju.mcu.ServiceListener;
import huju.mcu.ServiceProvider;
import huju.mcu.datatypes.HumidityTemperature;
import huju.mcu.datatypes.MCUData;
import huju.mcu.datatypes.MCUDevice;
import huju.mcu.device.DeviceId;
import huju.mcu.device.DeviceType;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestDataService
{
	public TestDataService() {
		MCUDataService service = ServiceProvider.getDataService(ServiceProvider.DEFAULT_PLATFORM);
		MCUDevice[] devices;
		try {
			
			devices = service.getDevices();
		} catch (DeviceConfigException ex) {
			Logger.getLogger(TestDataService.class.getName()).log(Level.SEVERE, null, ex);
			return;
		}
		for (MCUDevice device : devices) {
			System.out.println("------");
			System.out.println(device.toString());
			
		}	
			
		service.addServiceListener(new ServiceListener() {
			@Override
			public void dataReceived(MCUData data) {
				if (data.getDeviceType() == DeviceType.MOTION_DETECTOR) {
					
				}
				System.out.println("[TestDataService] GOT:"+data);
			}
		});
		try {
			MCUDevice device = service.getDevice(DeviceId.HUM_TEM_SENSOR_1);
			MCUData request = device.getDefautReadRequest();
			while (true) {
				//System.out.println("\"[TestDataService] GOT:\"");
				//service.sendData(DeviceId.HUM_TEM_SENSOR_1, request);
                                MCUData data = service.readDeviceValue(DeviceId.HUM_TEM_SENSOR_1, 1000);
				System.out.println("SYNC:"+data);
				Thread.sleep(5000);
			}
			
		} catch (DeviceConfigException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DeviceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		new TestDataService();
	}

}
