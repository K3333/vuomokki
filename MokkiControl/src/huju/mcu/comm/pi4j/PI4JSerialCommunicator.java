package huju.mcu.comm.pi4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataListener;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.SerialPortException;

import huju.mcu.comm.SerialCommunicator;
import huju.mcu.datatypes.InvalidDataFormatException;
import huju.mcu.datatypes.MCUData;
import huju.mcu.comm.CommunicationDataListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PI4JSerialCommunicator implements SerialCommunicator 
{
	 final Serial serial = SerialFactory.createInstance();
	 private CommunicationDataListener externalListener;
	 private InternalSerialListener internalListener; 
	 private ExecutorService executor;

	@Override
	public boolean openPort(String port, int baud) 
	{
		try {
			serial.open(port, baud);
			return true;
		} catch (SerialPortException spe) {
			spe.printStackTrace();
		}
		
		return false;
	}

	@Override
	public void setCommunicationDataListener(CommunicationDataListener listener) {
            this.externalListener = listener;
            if (internalListener==null) {
                executor = Executors.newCachedThreadPool(new ThreadFactory() {
                    @Override
	            public Thread newThread(Runnable r) {
	                Thread thread = new Thread(r);
	                thread.setDaemon(true);
	                return thread;
	            }
	        });
			
			internalListener = new InternalSerialListener();
			serial.addListener(internalListener);
			
		}
	}

	@Override
	public void sendData(byte[] data) 
	{
		serial.write(data);
	}
	
	/**
	 * SerialDataListener interface implementaion class.
	 */
	private class InternalSerialListener implements SerialDataListener
	{
		@Override
        public void dataReceived(SerialDataEvent event) {
            // print out the data received to the console
            if (externalListener!=null) {
            	String bts = event.getData();
            	if (bts!=null && bts.length()>0) {
                        try {
                            List<MCUData> mcuDataList= MCUData.constuctData(bts);
                            if (mcuDataList.size()>0) {
                                DataListenerNotifier notifier = new DataListenerNotifier(mcuDataList);
                            executor.execute(notifier);
                            }
                            
                            /*
                            MCUData data = contructData(bts);
                            if (data!=null) {
                            // Call listener in separated thread...
                            DataListenerNotifier notifier = new DataListenerNotifier(data);
                            executor.execute(notifier);
                            }
                            */
                        } catch (InvalidDataFormatException ex) {
                            Logger.getLogger(PI4JSerialCommunicator.class.getName()).log(Level.SEVERE, null, ex);
                        }
            	}
            }
        } 
	/*
	private MCUData contructData(String data) 
        {
            if (data.length()>0) {
                    try {
                            MCUData mcuData[] = MCUData.constuctData(data);
                    } catch (InvalidDataFormatException e) {
                            System.out.println("Receiver invalid data: "+e.getMessage());
                    }
            }
            return null;
	}
        
        */
	}
	
	/**
	 * Runnable to call CommunicationDataListener's dataReceived method.
	 */
	private class DataListenerNotifier implements Runnable 
	{
		private List<MCUData> datas;

		public DataListenerNotifier(List<MCUData> datas) 
		{
                    this.datas = datas;
		}

		public void run() 
		{
                       for (MCUData data : datas) {
                           externalListener.dataReceived(data);
                       }
		}
	} 

}
