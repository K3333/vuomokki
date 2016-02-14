package huju.mcu.datatypes;

import huju.mcu.device.ActionType;
import huju.mcu.device.DeviceType;
import huju.mcu.device.SourceBus;

public class MotionDetect extends MCUData 
{
	private int pinLevel;
	@Override
	public String toString() 
	{
		// TODO Auto-generated method stub
		return getDeviceType().toString() + 
				"on" + getSourceBus().toString() + 
				" | PIN LEVEL: "+ getPinLevel();
	}
	public int getPinLevel() 
	{
		return pinLevel;
	}
	public void setPinLevel(int pinLevel) 
	{
		this.pinLevel = pinLevel;
	}
	
	public static MotionDetect construct(String[] args) throws InvalidDataFormatException
	{
		if (args.length < 4) {
			throw new InvalidDataFormatException("Expected 4 arguments, got: "+args.length);
		}
		MotionDetect md = new MotionDetect();
		md.setDataType(ActionType.getActionType(Integer.parseInt(args[0])));
		md.setDeviceType(DeviceType.getDeviceType(Integer.parseInt(args[1])));
		md.setSourceBus(SourceBus.getSourceBus(Integer.parseInt(args[2])));
		md.setPinLevel(Integer.parseInt(args[3]));
		return md;
	}

}
