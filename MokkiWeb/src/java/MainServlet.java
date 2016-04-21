/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import huju.DataControlService;
import huju.mcu.DeviceConfigException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import huju.mcu.MCUDataService;
import huju.mcu.ServiceProvider;
import huju.mcu.datatypes.HumidityTemperature;
import huju.mcu.datatypes.MCUDevice;
import huju.mcu.datatypes.MotionDetect;
import huju.mcu.device.DeviceId;
import huju.mcu.device.DeviceType;
import huju.web.obj.DisplayData;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;

/**
 *
 * @author huju
 */
@WebServlet(urlPatterns = {"/auto"}, initParams = {
	@WebInitParam(name = "libPath", value = "/home/pi")})
public class MainServlet extends HttpServlet
{
	private Object dataController = null;
	Class<?> clazzDc;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		
		
	public void init() throws ServletException
    {
		ServletContext srcContext = getServletContext();
		ServletContext trgContext = srcContext.getContext("/mokki/dcs");
		dataController = trgContext.getAttribute(DataControlService.INSTANCE_ATTR);
		ClassLoader targetClassloader = Thread.currentThread().getContextClassLoader();
		if (dataController!=null) {
			try {
				clazzDc = targetClassloader.loadClass("huju.DataControlService");
			} catch (ClassNotFoundException ex) {
				Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, null, ex);
			}
		} else {
			Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, "DataController could not be loaded");
		}
	}
	
	private MotionDetect getMotionDetect(MCUDevice device) {
		if (dataController!=null && clazzDc!=null) {
			try {
				Method m = clazzDc.getMethod("geLastMotionDetection", MCUDevice.class);
				Object ret = m.invoke(dataController, device);
				return (MotionDetect) ret;
			} catch (NoSuchMethodException ex) {
				Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, null, ex);
			} catch (SecurityException ex) {
				Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IllegalAccessException ex) {
				Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IllegalArgumentException ex) {
				Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, null, ex);
			} catch (InvocationTargetException ex) {
				Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, null, ex);
			}
		} else {
			Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, "No servlet instanse");
		}
		return null;
	}
	
	private HumidityTemperature getTemperatureHumidity(MCUDevice device) {
		if (dataController!=null && clazzDc!=null) {
			try {
				Method m = clazzDc.getMethod("getHumTemp", MCUDevice.class);
				Object ret = m.invoke(dataController, device);
				return (HumidityTemperature) ret;
			} catch (NoSuchMethodException ex) {
				Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, null, ex);
			} catch (SecurityException ex) {
				Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IllegalAccessException ex) {
				Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IllegalArgumentException ex) {
				Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, null, ex);
			} catch (InvocationTargetException ex) {
				Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, null, ex);
			}
		} else {
			Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, "No servlet instanse");
		}
		return null;
	}
	
	private MCUDevice[] getDevices()
	{
		if (dataController!=null && clazzDc!=null) {
			try {
				Method m = clazzDc.getMethod("getDevices", new Class[0]);
				Object ret = m.invoke(dataController, new Object[0]);
				return (MCUDevice[]) ret;
			} catch (NoSuchMethodException ex) {
				Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, null, ex);
			} catch (SecurityException ex) {
				Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IllegalAccessException ex) {
				Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IllegalArgumentException ex) {
				Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, null, ex);
			} catch (InvocationTargetException ex) {
				Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, null, ex);
			} catch (Exception ex) {
				Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, null, ex);
			}
		} else {
			Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, "No servlet instanse");
		}
		return null;
	}
	
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
			out.println("<title>Servlet MainServlet</title>");		
			out.println("</head>");
			out.println("<body>");
			
			/*
		ServletContext srcContext = getServletContext();
		ServletContext trgContext = srcContext.getContext("/mokki/DisplayServlet");
		if (trgContext==null) {
			trgContext = srcContext.getContext("/DisplayServlet");
		}
		ClassLoader targetClassloader = Thread.currentThread().getContextClassLoader();
		try {
			Enumeration<String> keys = trgContext.getAttributeNames();
			while (keys.hasMoreElements()) {
				String name = keys.nextElement();
				Object value = trgContext.getAttribute(name);
				out.println("<br>"+name+"="+value);
			}
			Object obj = trgContext.getAttribute("INSTANCE");
			if (obj!=null) {
				Class<?> clazz = targetClassloader.loadClass("huju.DisplayServlet");
				Method m = clazz.getMethod("setDisplayData", DisplayData.class);
				DisplayData dd = new DisplayData();
				dd.data = new Object[]{"14.27","heat"};
				dd.delays = new long[]{5000, 1000};
				m.invoke(obj, dd);
			} else {
				Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, "No servlet instanse");
			}
			
		} catch (Exception e) {
			Logger.getLogger(MainServlet.class.getName()).log(Level.SEVERE, null, e);
		} 		
		finally {
		
		}
		*/

			out.println("<h1>Devices</h1>");
			out.println(getDeviceTable(getDevices()));
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
		return "Automation";
	}// </editor-fold>
	

	
	private String getDeviceTable(MCUDevice[] devices) 
	{
		StringBuffer buf = new StringBuffer();
		
		
		if (devices==null) {
			buf.append("<table>\n");
			buf.append("<tr>");
			buf.append("<td>");
			buf.append("Virhe laitteiden luvussa!");
			buf.append("</td>");
			buf.append("</tr>");
			buf.append("</table>\n");
		} else {
			for (MCUDevice d : devices) {
				buf.append("<hr><h2>"+d.getDeviceType().toString()+"</h2>");
				buf.append("<p>"+d.getDeviceId().toString());
				buf.append("<br>"+d.getDeviceInfo());
				buf.append("<br><b>"+d.getDescription()+"</b></p>");
				buf.append("<h1>"+getvalue(d)+"</h1>");
			}
			/*
			buf.append("<tr>\n");
			
			buf.append("<td><b>ID</b></td>\n");
			buf.append("<td><b>Tyyppi</b></td>\n");
			buf.append("<td><b>Kuvaus</b></td>\n");
			buf.append("<td><b>Arvo</b></td>\n");
			buf.append("</tr>\n");
			for (MCUDevice d : devices) {
				buf.append("<tr>\n");
				buf.append("<td>");
				buf.append(d.getDeviceId().toString());
				buf.append("</td>\n");
				buf.append("<td>");
				buf.append(d.getDeviceType().toString());
				buf.append("</td>\n");
				buf.append("<td>");
				buf.append(d.getDescription());
				buf.append("</td>\n");
				buf.append("<td>");
				buf.append(getvalue(d));
				buf.append("</td>\n");
				buf.append("</tr>\n");
				buf.append("</table>"); 
			}
		*/
		}
		
		return buf.toString();

	}
	
	private String getvalue(MCUDevice d)
	{
		if (d.getDeviceType() == DeviceType.TEMPERATURE_HUMIDITY_SENSOR) {
			HumidityTemperature humTemp = getTemperatureHumidity(d);
			if (humTemp!=null) {
				return String.format("%3.1f %s | %3.1f %s", humTemp.getTemperature(), ((char) 176)+"C", humTemp.getHumidity(), "%");
			}
		} else if (d.getDeviceType() == DeviceType.MOTION_DETECTOR) {
			MotionDetect motion = getMotionDetect(d);
			if (motion!=null) {
				Date date = new Date();
				date.setTime(motion.getStartTime());
				String start = dateFormat.format(date);
				if (motion.getPinLevel()!=0) {
					long dur = (System.currentTimeMillis()-motion.getStartTime())/1000;
					String f = "<h1 style=\"color:red;\">!!! ALERT !!!</h1><p>  duration:%d s  Started:%s</p>";
					return String.format(f, dur, start) ;
				}
				date.setTime(motion.getEndTime());
				String end = dateFormat.format(date);
				long dur = (motion.getEndTime()-motion.getStartTime())/1000;
				String f = "<p>Last alert:  %s - %s (%d s)</p>";
				return String.format(f, start, end, dur) ;
			}
		}
		
		return d.getDeviceType().name()+" unavailable";
	}

}
