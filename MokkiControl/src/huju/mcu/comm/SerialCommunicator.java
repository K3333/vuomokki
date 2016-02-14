package huju.mcu.comm;

public interface SerialCommunicator 
{
	/**
	 * Initialize and open the serial communication port.
	 *  
	 * @param port The COM port to open, for example 'COM3' (Windows) or "/dev/ttyAMA0" (Linux) 
	 * @param baud The speed of the port    
	 * @return boolean was port opened successfully.
	 */
	public boolean openPort(String port, int baud);
	
	/**
	 * Set listener to be notified when data is received to the serial port. 
	 * 
	 * @param listener
	 */
	public void setCommunicationDataListener(CommunicationDataListener listener);
	
	/**
	 * Writes data to communication port.
	 * 
	 * @param data Data to write
	 */
	public void sendData(byte[] data);
}
