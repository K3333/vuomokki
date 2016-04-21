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
import huju.mcu.datatypes.MCUData;
import huju.mcu.datatypes.MCUDevice;
import huju.mcu.device.ActionType;
import huju.mcu.device.DeviceId;
import huju.mcu.device.DeviceType;
import huju.mcu.device.SourceBus;
import huju.mcu.schemas.Device;
import huju.mcu.schemas.Devices;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MCUDataServiceImpl implements MCUDataService
{

	private static final String DEVS_CONF_FILE = "DeviceConfig.xml";
	private static final char SEPARATOR = '\t';
	/* Communication module*/
	private SerialCommunicator communicator;
	/* Communication data listener  */
	private CommDataListener dataListener;
	/* Client data listeners */
	private final List<ServiceListener> serviceListeners = new ArrayList<ServiceListener>();
	/* Mutex for blocking the read calls */
	private final Hashtable<MCUDevice, Mutex> locks = new Hashtable<MCUDevice, Mutex>();
	/* Devices based on last config read */
	private List<MCUDevice> devices;
	/* Reaceive data listeners filters, used to filter data beofer it is provided to external listeners*/
	private final List<MCUDataFilter> dataFilters = new ArrayList<MCUDataFilter>();
	/* (optional) Reader for ddvice config xml */
	private Reader configReader = null;

	public MCUDataServiceImpl(SerialCommunicator communicator)
	{
		if (communicator!=null) {
			CommDataListener listener = new CommDataListener();
			this.communicator = communicator;
			this.communicator.setCommunicationDataListener(listener);
		} else {
			System.out.println("[WARNING] MCUDataServiceImpl: No communication initialized!");
		}
		

		//ExecutorService executor = Executors.newFixedThreadPool(2);
	}

	public void addServiceListener(ServiceListener listener)
	{
		serviceListeners.add(listener);
	}

	public void removeServiceListener(ServiceListener listener)
	{
		serviceListeners.remove(listener);;
	}

	public void addDataFilter(MCUDataFilter filter)
	{
		dataFilters.add(filter);
	}

	public void removeDataFilter(MCUDataFilter filter)
	{
		dataFilters.remove(filter);;
	}
	
	public void removeAllFilters()
	{
		dataFilters.clear();
	}

	public void sendData(DeviceId id, MCUData request) throws DeviceNotFoundException
	{
		MCUDevice device;
		try {
			device = getDevice(id);
		} catch (DeviceConfigException ex) {
			throw new DeviceNotFoundException("Config error: " + ex.getMessage());
		}
		if (device == null) {

			throw new DeviceNotFoundException("Not found: " + id.name());
		}
		String strRequest = buildRequest(device, ActionType.COMMAND_REQUEST);
		communicator.sendData(strRequest.getBytes());
	}

	@Override
	public MCUData readDeviceValue(DeviceId id, int timeout) throws DeviceNotFoundException
	{
		MCUDevice device;
		try {
			device = getDevice(id);

		} catch (DeviceConfigException ex) {
			Logger.getLogger(MCUDataServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
			throw new DeviceNotFoundException(ex.getMessage());
		}
		if (device == null) {
			throw new DeviceNotFoundException("Not found: " + id.name());
		}
		String request = buildRequest(device, ActionType.COMMAND_REQUEST);
		Mutex lock = new Mutex();
		locks.put(device, lock);
		new Thread()
		{
			public void run()
			{
				try {
					Thread.sleep(10);
					communicator.sendData(request.getBytes());
				} catch (Exception ex) {
					Logger.getLogger(MCUDataServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}.start();

		synchronized (lock) {
			try {
				lock.wait(timeout);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		locks.remove(lock);
		return lock.response;
	}

	public MCUDevice getDevice(DeviceId deviceId) throws DeviceConfigException
	{
		if (devices == null) {
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
		if (devices == null) {
			readDeviceConfig();
		}
		MCUDevice[] ret = new MCUDevice[devices.size()];
		return (MCUDevice[]) devices.toArray(ret);
	}

	private void readDeviceConfig() throws DeviceConfigException
	{
		File file = null;
		if (configReader == null) {
			String path = System.getProperty("mcuconfig.path");
			if (path == null) {
				path = Paths.get(".").toAbsolutePath().normalize().toString();
			}
			path = path + File.separator + DEVS_CONF_FILE;
			file = new File(path);
			if (!file.exists()) {
				System.out.println("Cannot find config file: " + path);
				System.out.println("Ensure that \'mcuconfig.path' system property points to valid path!");
				throw new DeviceConfigException("Device config file: " + path + " does not exists!");
			}
		}
		
		Devices xmlDevices = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Devices.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			xmlDevices = (Devices) ( configReader!=null ? 
									jaxbUnmarshaller.unmarshal(configReader) :
									jaxbUnmarshaller.unmarshal(file));
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
	 * Converts xml device definitions to MCUDevice object .
	 *
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
		// Display update values
		if (device.getDisplayData()!=null) {
			d.setDisplayData(device.getDisplayData().getDisplayElement());
		}
		// Data (db/cache) store times
		if (device.getDataUpdate()!=null ) {
			if (device.getDataUpdate().getDbStoreInterval()!=null) {
				d.setDataStoreInterval(device.getDataUpdate().getDbStoreInterval().longValue());
			}
			if (device.getDataUpdate().getCacheUpdatateInterval()!=null) {
				d.setCacheUpdateInterval(device.getDataUpdate().getCacheUpdatateInterval().intValue());
			}
		}
		if (device.getDataFilter() != null) {
			if (device.getDataFilter().getStateOnDelay()!=null) {
				d.setSingleTriggerDelay(device.getDataFilter().getStateOnDelay().longValue());
			}
		}
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
		if (deviceData != null) {
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

	@Override
	public void setDeviceConfig(Reader configXml)
	{
		configReader = configXml;
	}

	private class CommDataListener implements CommunicationDataListener
	{

		@Override
		public void dataReceived(MCUData data)
		{
			if (!routeToWaiters(data)) {
				if (!filter(data)) {
					for (ServiceListener l : serviceListeners) {
						l.dataReceived(data);
					}
				}
			}
		}
	}

	/**
	 * Used filters to filter received data
	 *
	 * @return true if data is filtered.
	 */
	protected boolean filter(MCUData data)
	{
		for (MCUDataFilter filter : dataFilters) {
			if ((filter.getDeviceType() == DeviceType.UNDEFINED || data.getDeviceType() == filter.getDeviceType())
				&& (filter.getSourceBus() == SourceBus.UNDEFINED || data.getSourceBus() == filter.getSourceBus())) {
				if (filter.processData(data)) {
					return true;
				}
			}
		}
		return false;

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
				if (data.getDeviceType() == d.getDeviceType()
					&& data.getSourceBus() == d.getSourceBus()) {
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
