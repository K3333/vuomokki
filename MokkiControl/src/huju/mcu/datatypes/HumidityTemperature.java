package huju.mcu.datatypes;

import huju.mcu.device.ActionType;
import huju.mcu.device.DeviceType;
import huju.mcu.device.SourceBus;

public class HumidityTemperature extends MCUData
{
	private static final int STATUS_ERROR = -1;
	private static final int STATUS_SUCCESS = 1;
        
	private int status;
	private double temperature;
	private double humidity;
	private long timestamp;
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) 
	{
		this.status = status;
	}
	public double getTemperature() 
	{
		return temperature;
	}
	public void setTemperature(double temperature) 
	{
		this.temperature = temperature;
	}
	public double getHumidity() 
	{
		return humidity;
	}
	public void setHumidity(double humidity) 
	{
		this.humidity = humidity;
	}
	
	public static HumidityTemperature construct(String[] args) throws InvalidDataFormatException
	{
		if (args.length!=6) {
                    throw new InvalidDataFormatException("Expected 6 arguments, got: "+args.length);
		}
		HumidityTemperature ht = new HumidityTemperature();
		ht.setDataType(ActionType.getActionType(Integer.parseInt(args[0])));
		ht.setDeviceType(DeviceType.getDeviceType(Integer.parseInt(args[1])));
		ht.setSourceBus(SourceBus.getSourceBus(Integer.parseInt(args[2])));
		ht.setStatus(args[3]!=null ? Integer.parseInt( args[3]) : HumidityTemperature.STATUS_ERROR);
		ht.setHumidity(args[4]!=null ? Double.parseDouble(args[4]) : Double.NaN);
		ht.setTemperature(args[5]!=null ? Double.parseDouble(args[5]) : Double.NaN);
		ht.setTimestamp(System.currentTimeMillis());
		return ht;
	}
	
	@Override
	public String toString()
	{
		return getDeviceType().toString() + 
				" on " + getSourceBus().toString() + 
				" | status: "+ getStatus() + 
				" humidity:" + getHumidity() +
				" temperature:" + getTemperature();
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
