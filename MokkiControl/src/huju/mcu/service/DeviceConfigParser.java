package huju.mcu.service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import huju.mcu.schemas.Devices;

import java.io.File;

public class DeviceConfigParser 
{
	public static Devices readDeviceCondig(File xmlFile) 
	{
		Devices devices = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Devices.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			devices = (Devices) jaxbUnmarshaller.unmarshal(xmlFile);
			
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return devices;
	}
}
