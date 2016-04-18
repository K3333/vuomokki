package huju.mcu.datatypes;

import huju.mcu.device.ActionType;
import huju.mcu.device.DeviceType;
import huju.mcu.device.SourceBus;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MotionDetect extends GPIOValue 
{
	private long timestamp;
	public static MotionDetect construct(String[] args) throws InvalidDataFormatException
	{
	
		if (args.length < 4) {
			throw new InvalidDataFormatException("Expected 4 arguments, got: "+args.length);
		}
		MotionDetect md = new MotionDetect();
		md.setTimestamp(System.currentTimeMillis());
		md.setDataType(ActionType.getActionType(Integer.parseInt(args[0])));
		md.setDeviceType(DeviceType.getDeviceType(Integer.parseInt(args[1])));
		md.setSourceBus(SourceBus.getSourceBus(Integer.parseInt(args[2])));
		md.setPinLevel(Integer.parseInt(args[3]));
		return md;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp()
	{
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}

}
