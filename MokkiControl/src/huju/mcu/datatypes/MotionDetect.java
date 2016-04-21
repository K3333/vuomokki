package huju.mcu.datatypes;

import huju.mcu.device.ActionType;
import huju.mcu.device.DeviceType;
import huju.mcu.device.SourceBus;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MotionDetect extends GPIOValue 
{
	private long startTime;
	private long endTime;
	public static MotionDetect construct(String[] args) throws InvalidDataFormatException
	{
	
		if (args.length < 4) {
			throw new InvalidDataFormatException("Expected 4 arguments, got: "+args.length);
		}
		MotionDetect md = new MotionDetect();
		md.setStartTime(System.currentTimeMillis());
		md.setDataType(ActionType.getActionType(Integer.parseInt(args[0])));
		md.setDeviceType(DeviceType.getDeviceType(Integer.parseInt(args[1])));
		md.setSourceBus(SourceBus.getSourceBus(Integer.parseInt(args[2])));
		md.setPinLevel(Integer.parseInt(args[3]));
		return md;
	}

	/**
	 * @return the startTime
	 */
	public long getStartTime()
	{
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(long startTime)
	{
		this.startTime = startTime;
	}

	/**
	 * @return the endTime
	 */
	public long getEndTime()
	{
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(long endTime)
	{
		this.endTime = endTime;
	}

}
