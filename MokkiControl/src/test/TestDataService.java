package test;

import huju.mcu.DeviceConfigException;
import huju.mcu.DeviceNotFoundException;
import huju.mcu.MCUDataService;
import huju.mcu.ServiceListener;
import huju.mcu.ServiceProvider;
import huju.mcu.datatypes.HumidityTemperature;
import huju.mcu.datatypes.MCUData;
import huju.mcu.datatypes.MCUDevice;
import huju.mcu.datatypes.MotionDetect;
import huju.mcu.device.DeviceId;
import huju.mcu.device.DeviceType;
import huju.mcu.service.DisplayService;
import huju.mcu.service.MCUDataFilter;
import huju.mcu.service.SingleTriggerDataFiter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestDataService
{
	public TestDataService() {
	
		try {
			DisplayService ds = new DisplayService();
			
			//for (int i = DisplayService.G; i >= DisplayService.D1; i--) {
			for (int i = DisplayService.D4; i <= DisplayService.G; i++) {
				ds.test(i);
				Thread.currentThread().sleep(50);
			}
			ds.shutdown();
			
			
			//ds.displayString("-");
			double[] data = new double[]{1.23, 12.1, 10.002, 0.22, 0.0};
			//ds.displayDouble(12.34);
/*
			for (double d : data) {
				Thread.currentThread().sleep(2000);
				System.out.println("Show: "+d);
				ds.displayDouble(d); 
		
			}
*/
	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*
		MCUDataService service = ServiceProvider.getDataService(ServiceProvider.DEFAULT_PLATFORM);
		
		ServiceListener listener = new ServiceListener()
		{
			@Override
			public void dataReceived(MCUData data)
			{
				System.out.println("RECV -> "+data);
				if (data.getDeviceType() == DeviceType.MOTION_DETECTOR) {
					SimpleDateFormat tf = new SimpleDateFormat("dd.MM.YYYY HH:mm:ss)");
					Date d = new Date();
					d.setTime(((MotionDetect) data).getTimestamp());
					String time = tf.format(d);
					String onoff = ((MotionDetect) data).getPinLevel()==1 ? "ON" : "OFF";
					System.out.printf("[%s] -> ALERT %s @%s\r\n",time, onoff, data.toString());
				} else {
				{
					System.out.println("RECV -> "+data);
				}
				
			}
				
				
			}
		};
		service.addServiceListener(listener);
		
		MCUDevice[] devices;
		try {
			devices = service.getDevices();
		} catch (DeviceConfigException ex) {
			Logger.getLogger(TestDataService.class.getName()).log(Level.SEVERE, null, ex);
			return;
		}
		for (MCUDevice device : devices) {
			if (device.getDeviceType() == DeviceType.MOTION_DETECTOR) {
				SingleTriggerDataFiter filter = new SingleTriggerDataFiter(device, listener);
				filter.setStateOnTime(2000);
				service.addDataFilter(filter);
			}	
			System.out.printf(Locale.ROOT, 
				"----------------\nID:%s\nType:%s\nMCU BUS:%s\n%s\n%s\n---------------------------\n", 
				device.getDeviceId(), 
				device.getDeviceType(),
				device.getSourceBus(),
				device.getDeviceInfo(),
				device.getDescription());
		}	
		
		try {
			MCUDevice device = service.getDevice(DeviceId.HUM_TEM_SENSOR_1);
			while (true) {
                MCUData data = service.readDeviceValue(DeviceId.HUM_TEM_SENSOR_1, 1000);

				System.out.println("SYNC READ:"+data);
				Thread.sleep(5000);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
*/
			
		
	}
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		//new TestDataService();
				try {
			DisplayService ds = new DisplayService();
			/*
			for (int i = DisplayService.E; i >= DisplayService.D3; i--) {
			//for (int i = DisplayService.D4; i <= DisplayService.F; i++) {
				ds.test(i);
				Thread.sleep(100);
			}
			ds.shutdown();
			*/
			
			//ds.displayString("-");
			double[] data = new double[]{1234, 1.23, 12.1, 10.002, 0.22, 0.0};
			//ds.displayDouble(12.34);

			int DISPLAY_TIME = 2000;
			String[] texts = {"SISA","  "+((char) 248)+"C","ULKO",
			"o/"+((char) 248),"o"+((char) 248),"IN","OUT",
			"HUJU","HEAT","APUA","AIKA","PUM","SCAN"};
			
			
			for (int i=0; i<3;i++) {
				long start = System.currentTimeMillis();
				String s = texts[0];
				if (i==2) s = texts[1];
				long delay = i==1 ? 5000 : 2000;
				do {
					if (i==1) {
						ds.displayDouble(23.7);
					}  else {
						ds.displayString(s);
					}
				 
				} while (System.currentTimeMillis()-start < delay);
			}
			
			for (String s : texts) {
				long start = System.currentTimeMillis();
				do {
				ds.displayString(s); 
				} while (System.currentTimeMillis()-start < DISPLAY_TIME);
			}
			
			for (double d : data) {
				System.out.println("Show: "+d);
				long start = System.currentTimeMillis();
				do {
					ds.displayDouble(d); 
				} while (System.currentTimeMillis()-start < DISPLAY_TIME);
			}

			
			ds.shutdown();

	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
