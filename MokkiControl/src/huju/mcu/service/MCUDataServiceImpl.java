package huju.mcu.service;

import huju.mcu.comm.CommunicationDataListener;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import huju.mcu.DeviceConfigException;
import huju.mcu.DeviceNotFoundException;
import huju.mcu.MCUDataService;
import huju.mcu.comm.SerialCommunicator;
import huju.mcu.ServiceListener;
import huju.mcu.datatypes.HumidityTemperature;
import huju.mcu.datatypes.MCUData;
import huju.mcu.datatypes.MCUDevice;
import huju.mcu.device.ActionType;
import huju.mcu.device.DeviceId;
import huju.mcu.device.DeviceType;
import huju.mcu.device.SourceBus;
import huju.mcu.schemas.Device;
import huju.mcu.schemas.Devices;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MCUDataServiceImpl implements MCUDataService
{
	private List<MCUDevice> devices;
	private static final char SEPARATOR = '\t';
	private SerialCommunicator communicator;
	private CommDataListener dataListener;
	private List<ServiceListener> serviceListeners = new ArrayList<ServiceListener>();
	private Hashtable<MCUDevice, Mutex> locks = new Hashtable<MCUDevice, Mutex>();
        private static final String DEVS_CONF_FILE = "DeviceConfig.xml";

	
	public MCUDataServiceImpl(SerialCommunicator communicator) 
	{
		CommDataListener listener = new CommDataListener();
		this.communicator = communicator;
		this.communicator.setCommunicationDataListener(listener);
		
		ExecutorService executor = Executors.newFixedThreadPool(2);
	}
	
	public void addServiceListener(ServiceListener listener)
	{
		serviceListeners.add(listener);
	}
	
	public void removeServiceListener(ServiceListener listener)
	{
		serviceListeners.remove(listener);;
	}
	
	public void sendData(DeviceId id, MCUData request) throws DeviceNotFoundException
	{
		MCUDevice device;
		try {
			device = getDevice(id);
		} catch (DeviceConfigException ex) {
			throw new DeviceNotFoundException("Config error: "+ex.getMessage());
		}
		if (device==null) {
			
			throw new DeviceNotFoundException("Not found: "+id.name());
		}
		String strRequest = buildRequest(device, ActionType.COMMAND_REQUEST);
		communicator.sendData(strRequest.getBytes());
	}

	@Override
	public MCUData readDeviceValue(DeviceId id, int timeout) throws DeviceNotFoundException
	{
		// TODO Auto-generated method stub
		MCUDevice device;
		try {
			device = getDevice(id);
			
		} catch (DeviceConfigException ex) {
			Logger.getLogger(MCUDataServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
			throw new DeviceNotFoundException(ex.getMessage());
		}
		if (device==null) {
			throw new DeviceNotFoundException("Not found: "+id.name());
		}
		String request = buildRequest(device, ActionType.COMMAND_REQUEST);
		Mutex lock = new Mutex();
		locks.put(device, lock);
		long start = System.currentTimeMillis();
		/*Runnable r = */
		new Thread()
		{
			public void run()
			{
				try {
				Thread.sleep(10);
			} catch (InterruptedException ex) {
			    Logger.getLogger(MCUDataServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
			}
			communicator.sendData(request.getBytes());
		}
		}.start();

		synchronized (lock) {
		try {
			lock.wait(timeout);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		System.out.println("LOCK TIME: " + (System.currentTimeMillis() - start));
		locks.remove(lock);
		return lock.response;
	}
	
	public MCUDevice getDevice(DeviceId deviceId) throws DeviceConfigException
	{
		if (devices==null) 
		{
			readDeviceConfig();
		}
		for (MCUDevice device : devices) {
			if (device.getDeviceId() == deviceId) {
				return device;
			}
		}
		return null;
		
	}
	
	public MCUDevice[] getDevices() throws DeviceConfigException
	{
		if (devices==null) 
		{
			readDeviceConfig();
		}
		return (MCUDevice[]) devices.toArray();
	}
	
	private void readDeviceConfig() throws DeviceConfigException
	{
		String path = System.getProperty("mcuconfig.path");
		if (path==null) {
			path = Paths.get(".").toAbsolutePath().normalize().toString();
		}
		path = path+File.separator+DEVS_CONF_FILE;
		File file = new File(path);
		if (!file.exists()) {
			System.out.println("Cannot fnd file: "+path);
			throw new DeviceConfigException("Device config file: "+path+" does not exists!");
		}
		Devices xmlDevices = null;
		try 
		{
			JAXBContext jaxbContext = JAXBContext.newInstance(Devices.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			xmlDevices = (Devices) jaxbUnmarshaller.unmarshal(file);
		} catch (JAXBException ex) {
			// TODO Auto-generated catch block
			DeviceConfigException e = new DeviceConfigException(ex.getMessage());
			e.setStackTrace(ex.getStackTrace());
			throw e;
		}
		List<Device> xmlDevice = xmlDevices.getDevice();
		devices = new ArrayList<MCUDevice>();
		for (Device d : xmlDevice) {
			MCUDevice device = convert(d);
			devices.add(device);
		}
	}
	
	/**
	 * Converts xml device definetions to MCUDevice object
	 * .
	 * @param device xml parsed JAXB object
	 * @return MCUDevice
	 */
	private MCUDevice convert(Device device) 
	{
		DeviceId id = DeviceId.getDeviceId(device.getDeviceId());
		DeviceType type = DeviceType.getDeviceType(device.getType());
		SourceBus bus = SourceBus.getSourceBus(device.getMcuBus());
		MCUDevice d = new MCUDevice();
		d.setDeviceId(id);
		d.setDeviceType(type);
		d.setSourceBus(bus);
		d.setDeviceInfo(device.getDeviceInfo());
		d.setDescription(device.getDescription());
		return d;
	}
	
	protected String buildRequest(MCUDevice device, ActionType actionType) 
	{
		DeviceType devicetype = device.getDeviceType();
		SourceBus sourceBus = device.getSourceBus();
		StringBuilder sb = new StringBuilder();
		sb.append(actionType.getActionTypeCode());
		sb.append(SEPARATOR);
		sb.append(devicetype.getDeviceTypeCode());
		sb.append(SEPARATOR);
		sb.append(sourceBus.getBusCode());	
		
		String deviceData = getDeviceRequestPart(device, actionType);
		if (deviceData!=null) {
			sb.append(SEPARATOR);
			sb.append(deviceData);
		}
		sb.append("\r\n");
		return sb.toString();
	}
	
	
	protected String getDeviceRequestPart(MCUDevice device, ActionType dataType) 
	{
		return null;
		
	}

	private class CommDataListener implements CommunicationDataListener
	{
		@Override
		public void dataReceived(MCUData data)
		{
			if (!routeToWaiters(data)) {
				for (ServiceListener l : serviceListeners) 
				{
					l.dataReceived(data);
				}
			}
		}
	}
	
	protected boolean routeToWaiters(MCUData data)
        {
		if (locks.size() == 0) {
			return false;
		}
		if (data.getDataType() == ActionType.COMMAND_RESPONSE) {
			Enumeration<MCUDevice> keys = locks.keys();
			while (keys.hasMoreElements()) {
				MCUDevice d = keys.nextElement();
				if (data.getDeviceType() == d.getDeviceType() &&
					data.getSourceBus() == d.getSourceBus())
				{
					Mutex lock = locks.get(d);
					lock.response = data;
					synchronized (lock) {
						lock.notifyAll();
					}
					return true;
				}
			}
		}
		return false;
	}

	private class Mutex
	{
		MCUData response;
	}

}

