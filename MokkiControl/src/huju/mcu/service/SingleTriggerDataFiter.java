/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package huju.mcu.service;

import huju.mcu.ServiceListener;
import huju.mcu.datatypes.GPIOValue;
import huju.mcu.datatypes.MCUData;
import huju.mcu.datatypes.MCUDevice;
import huju.mcu.datatypes.MotionDetect;
import huju.mcu.device.DeviceType;
import huju.mcu.device.SourceBus;
import huju.mcu.schemas.Device;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * 
 * @author huju
 */
public class SingleTriggerDataFiter implements MCUDataFilter
{
	private DeviceType deviceType;
	private SourceBus sourceBus;
	private ServiceListener serviceListener;
	private ExecutorService executor;
	private int currentState = 0;
	private long lastEventTime = 0;
	private long delayIime = 5000;
	private Timer stateChangeTimer = new Timer();
	private GPIOValue lastEvent;
	
	
	public SingleTriggerDataFiter(MCUDevice device, ServiceListener serviceListener)
	{
		this(device.getDeviceType(), device.getSourceBus(), serviceListener);
	}
	
	public SingleTriggerDataFiter(DeviceType deviceType, SourceBus sourceBus, ServiceListener serviceListener)
	{
		this.deviceType = deviceType;
		this.sourceBus = sourceBus;
		this.serviceListener = serviceListener;
		
		executor = Executors.newFixedThreadPool(4);
	}
	
	
	@Override
	public DeviceType getDeviceType()
	{
		return deviceType;
	
	}

	@Override
	public SourceBus getSourceBus()
	{
		return sourceBus;
	}
	
	public void setStateOnTime(long time) 
	{
		delayIime = time;
	}

	@Override
	public boolean processData(MCUData data)
	{
		if (data.getDeviceType() == DeviceType.MOTION_DETECTOR) {
			GPIOValue value = (GPIOValue) data;
			int state = value.getPinLevel();
			lastEventTime = System.currentTimeMillis();
			if (state==1 && currentState==0) 
			{
				currentState = state;
				lastEvent = value;
				executor.execute(stateChangeTimer);
				serviceListener.dataReceived(data);
			}
			return true;
		}
		return false;
	}
	
	protected void pinStateChanged(GPIOValue data, int state) 
	{
		lastEventTime = state;
		if (data.getPinLevel()==1 && currentState==0) 
		{
			currentState = data.getPinLevel();
			executor.execute(stateChangeTimer);
			serviceListener.dataReceived(data);
		}
	}
	
	private class Timer implements Runnable 
	{  
		@Override		
		public void run()
		{
			long timenow = System.currentTimeMillis();
			if (timenow-lastEventTime >= delayIime) {
				currentState = 0;
				lastEvent.setPinLevel(currentState);
				if (deviceType==DeviceType.MOTION_DETECTOR) {
					((MotionDetect) lastEvent).setEndTime(lastEventTime);
				}
				serviceListener.dataReceived(lastEvent);
				return;
			} 
			long newDelay = delayIime - (timenow - lastEventTime);
			try {
				Thread.sleep(newDelay);
			} catch (InterruptedException ex) {
				Logger.getLogger(SingleTriggerDataFiter.class.getName()).log(Level.SEVERE, null, ex);
				return;
			}
			executor.execute(this);
		}
		
	}
}
