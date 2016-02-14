package huju.mcu.datatypes;

/**
 * DataType for temperature sensing.
 * @author huju
 *
 */
public class Temperature extends MCUData 
{

	private int status;
	private double temperature;
	
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
	
	@Override
	public String toString()
	{
		return getDeviceType().toString() + 
				"on" + getSourceBus().toString() + 
				" | status: "+ getStatus() + 
				" temperature:" + getTemperature();
	}
}
