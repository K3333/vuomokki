/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package huju;


import huju.mcu.DeviceConfigException;
import huju.mcu.DeviceNotFoundException;
import huju.mcu.MCUDataService;
import huju.mcu.ServiceProvider;
import huju.mcu.datatypes.HumidityTemperature;
import huju.mcu.datatypes.MCUData;
import huju.mcu.datatypes.MCUDevice;
import huju.mcu.datatypes.Temperature;
import huju.mcu.device.DeviceId;
import huju.mcu.device.DeviceType;
import huju.mcu.schemas.DisplayElement;
import huju.mcu.service.DisplayService;
import huju.web.obj.DisplayData;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author huju
 */
@WebServlet(name = "DataControlService", urlPatterns = {"/dcs"})
public class DataControlService extends HttpServlet
{
	public static final String INSTANCE_ATTR = "DataControlService.instance";
	private MCUDataService dataService;
	private MCUDevice[] devices = null;
	private Method mDisplay = null;
	private Object display;
	private Hashtable<DeviceId,MCUData> dataCache = new Hashtable<DeviceId,MCUData>();
	private DisplayUpdateThread displayHandler;
	
	public void init() throws ServletException
    {
		getServletConfig().getServletContext().setAttribute(DataControlService.INSTANCE_ATTR, this);
	  	dataService = ServiceProvider.getDataService(ServiceProvider.RASBPERRY_PI);
		InputStream input = getServletContext().getResourceAsStream("/DeviceConfig.xml");
		if (input!=null) 
		{
			InputStreamReader reader = new InputStreamReader(input);
			dataService.setDeviceConfig(reader);
		} else {
			Logger.getLogger(DataControlService.class.getName()).log(Level.WARNING, "Cannot load DeviceConfig.xml");
		}
		displayHandler = new DisplayUpdateThread();
		displayHandler.start();
    }
	
	public MCUDevice[] getDevices() 
	{
		if (devices == null) {
			try {
				devices = dataService.getDevices();
				System.out.println("DCS ->DEVICE COUNT: "+devices.length);
			} catch (DeviceConfigException ex) {
				Logger.getLogger(DataControlService.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return devices;
	}
	
	public HumidityTemperature readHumTemp(MCUDevice device)
	{
		MCUData data = null;
		try {
			data = dataService.readDeviceValue(device.getDeviceId(), 1000);
		} catch (DeviceNotFoundException ex) {
			Logger.getLogger(DataControlService.class.getName()).log(Level.SEVERE, null, ex);
		}
		if (data == null || data.getDeviceType()!= DeviceType.TEMPERATURE_HUMIDITY_SENSOR) {
			return null;
		}
		HumidityTemperature ret = (HumidityTemperature) data;
		dataCache.put(device.getDeviceId(), ret);
		return ret;
		
	}
	
	public void destroy() {
		//dataService.
		displayHandler.terminate();
	}
	/*
	private void updateSegmentDisplay(HumidityTemperature ht)
	{
		DisplayData data = new DisplayData();
		for (MCUDevice dev : devices) {
		}
		data.data = new Object[]{ht.getTemperature(),ht.getHumidity()};
		data.delays = new long[]{2000,2000};
		diplayValue(data);
	}
	*/
	
	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		response.setContentType("text/html;charset=UTF-8");
		try (PrintWriter out = response.getWriter()) {
			/* TODO output your page here. You may use following sample code. */
			out.println("<!DOCTYPE html>");
			out.println("<html>");
			out.println("<head>");
			out.println("<title>Servlet DataControllService</title>");			
			out.println("</head>");
			out.println("<body>");
			out.println("<h1>Servlet DataControllService at " + request.getContextPath() + "</h1>");
			out.println("</body>");
			out.println("</html>");
		}
	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo()
	{
		return "Short description";
	}// </editor-fold>
	private void diplayValue(DisplayData dd) 
	{
		if (mDisplay!=null && display!=null) {
			try {
				mDisplay.invoke(display, dd);
			} catch (IllegalAccessException ex) {
				Logger.getLogger(DataControlService.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IllegalArgumentException ex) {
				Logger.getLogger(DataControlService.class.getName()).log(Level.SEVERE, null, ex);
			} catch (InvocationTargetException ex) {
				Logger.getLogger(DataControlService.class.getName()).log(Level.SEVERE, null, ex);
			}
		} else if (display==null && mDisplay==null) {
			ServletContext srcContext = getServletContext();
			ServletContext trgContext = srcContext.getContext("/mokki/DisplayServlet");
			ClassLoader targetClassloader = Thread.currentThread().getContextClassLoader();
			try {
				display = trgContext.getAttribute(DisplayServlet.INTANCE_ATTR);
				if (display!=null) {
					Class<?> clazz = targetClassloader.loadClass("huju.DisplayServlet");
					mDisplay = clazz.getMethod("setDisplayData", DisplayData.class);
					mDisplay.invoke(display, dd);
				} else {
					Logger.getLogger(DataControlService.class.getName()).log(Level.SEVERE, "No servlet instanse");
				}

			} catch (Exception e) {
				Logger.getLogger(DataControlService.class.getName()).log(Level.SEVERE, null, e);
			} 		
			finally {

			}
		}
	}
	
	private class DisplayUpdateThread extends Thread
	{
		private int deviceIndex;
		private int elementIndex;
		private List<MCUDevice> disDevs; 
		private boolean stopped = false;

		public DisplayUpdateThread()
		{
			disDevs = new ArrayList<MCUDevice>();
			refreshDevices();
		}

		public void terminate()
		{
			this.stopped = true;
		}

		public void refreshDevices()
		{
			for (MCUDevice displayElement : disDevs) {
				disDevs.remove(displayElement);
			}
			deviceIndex = 0;
			elementIndex = 0;
			for (MCUDevice d : getDevices()) {
				List<DisplayElement> de = d.getDisplayData();
				Logger.getLogger(DisplayUpdateThread.class.getName()).log(Level.INFO,"Display elems in "+d.getDeviceId().toString()+" => "+((de==null) ? "null" : de.size()));
				if (de!=null && de.size()>0) {
					System.out.println(d.getDeviceId()+"="+de.get(deviceIndex).getType()+","+de.get(deviceIndex).getDuration()+","+de.get(deviceIndex).getValue());
					
					disDevs.add(d);
				}
			}
			Logger.getLogger(DisplayUpdateThread.class.getName()).log(Level.INFO,"Displayable items:"+disDevs.size());
		}


		public void run()
		{	
			stopped = false;
			try {
				sleep(2000);
			} catch (InterruptedException ex) {
				Logger.getLogger(DataControlService.class.getName()).log(Level.SEVERE, null, ex);
			}
			while (!stopped) {
				if (disDevs.size()==0) {
					try {
						Logger.getLogger(DisplayUpdateThread.class.getName()).log(Level.WARNING, "No devices to display..");
						refreshDevices();
						sleep(5000);
					} catch (InterruptedException ex) {
						Logger.getLogger(DataControlService.class.getName()).log(Level.SEVERE, null, ex);
						stopped= true;
					}
				} else {
					if (deviceIndex >= disDevs.size()) {
						deviceIndex = 0;
					}
					MCUDevice dev = disDevs.get(deviceIndex);
					List<DisplayElement> des = dev.getDisplayData();
					if (des==null || des.size()==0) {
						Logger.getLogger(DisplayUpdateThread.class.getName()).log(Level.WARNING, "No display data to display... devId="+dev.getDeviceId());
						try {
							disDevs.remove(deviceIndex);
							deviceIndex = deviceIndex < des.size()-1 ? deviceIndex+1 : 0;
							elementIndex= 0;
							sleep(5000);
						} catch (InterruptedException ex) {
							Logger.getLogger(DataControlService.class.getName()).log(Level.SEVERE, null, ex);
						}

					} else {
						while (!stopped && elementIndex<des.size()) {
							DisplayElement de =	des.get(elementIndex++);
							String value = de.getValue();
							MCUData dData = dataCache.get(dev.getDeviceId());
							if (dev.getDeviceType() == DeviceType.TEMPERATURE_HUMIDITY_SENSOR) {
								if (dData==null) {
									dData = readHumTemp(dev);
									if (dData==null) {
										deviceIndex = deviceIndex<disDevs.size()-1 ? deviceIndex+1 : 0;
										elementIndex = 0;
										continue;
									}
								}
								HumidityTemperature ht = (HumidityTemperature) dData;
								DisplayData dd = null;
								if (ht==null) {
									dd = new DisplayData("err",DisplayData.TYPE_STING);
								} else if (value.equals("@humidity")) {
									dd = new DisplayData(ht.getHumidity(),DisplayData.TYPE_DOUBLE);
								} else if (value.equals("@temperature")){
									dd = new DisplayData(ht.getTemperature(),DisplayData.TYPE_DOUBLE);
								} else {
									dd = new DisplayData(value.toString(),DisplayData.TYPE_STING);
								}
								System.out.println("DATA TO DISP:"+dd.getData());
								
								diplayValue(dd);
							} 
							
							long idleTime = 1000;
							try {
								System.out.println("dur:"+de.getDuration());
								idleTime = Long.valueOf(de.getDuration());
								if (idleTime<=0) {
									idleTime = 1000;
								}
							} catch (Exception e) {
								Logger.getLogger(DataControlService.class.getName()).log(Level.SEVERE, null, e);
							}
							
							try {
								sleep(idleTime);
							} catch (InterruptedException ex) {
								Logger.getLogger(DataControlService.class.getName()).log(Level.SEVERE, null, ex);
								stopped = true;
							}
						}
						if (deviceIndex < disDevs.size()-1) {
							deviceIndex++;
							elementIndex = 0;
						} else {
							deviceIndex = 0;
							elementIndex = 0;
						}
					}
				}
			}
		}
	}
		
}
