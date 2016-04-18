/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package huju;

import huju.mcu.service.DisplayService;
import huju.web.obj.DisplayData;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author huju
 */
@WebServlet(name = "DisplayServlet", urlPatterns = {"/DisplayServlet"})
public class DisplayServlet extends HttpServlet implements Runnable 
{
	public static final String INTANCE_ATTR = "DisplayServlet.instance";
	private DisplayService display; 
	private Thread displayThread;
	private DisplayData data;
	private boolean stopped = false;

	
	public void init() throws ServletException
    {
		getServletConfig().getServletContext().setAttribute(INTANCE_ATTR, this);
		display = new DisplayService();
		displayThread = new Thread(this);
		displayThread.setPriority(Thread.MIN_PRIORITY);
		displayThread.start();
		Logger.getLogger(DisplayServlet.class.getName()).log(Level.INFO, "DisplayServlet STARTED...");
    }
	
	public void run(){
		int curPos = 0;
		long start = -1;
		while (!stopped) {
			if (data!=null && data.getData()!=null) {
				if (data.getType() == DisplayData.TYPE_DOUBLE) {
					display.displayDouble((double) data.getData());
				} else if (data.getType() == DisplayData.TYPE_INT) {
					display.displayString(Integer.toString((int) data.getData()));
				} else if (data.getType() == DisplayData.TYPE_STING) {
					display.displayString((String)data.getData());
				}
			} else {
				display.displayString("----");
			}
				/*
				if (data.delays==null) {
					display.displayString(""+data.data[0]);
				} else {
					long now = System.currentTimeMillis();
					if (now-start>=data.delays[curPos]) {
						if (curPos < data.delays.length-1) {
							curPos++;
						} else {
							curPos=0;
						}
						start = now;
					}
					Object o = data.data[curPos];
					if (o instanceof Double) {
						display.displayDouble((Double) o);
					} else {
						display.displayString(""+o);
					}
					
				}
				*/
				
	
			try {
				displayThread.sleep(3);
			} catch (InterruptedException ex) {
				Logger.getLogger(DisplayServlet.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		//display.shutdown();
	}
	
	public void setDisplayData(DisplayData data) {
		this.data = data;
	}
	
	public void destroy() {
		stopped = true;
		display.shutdown();
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
			out.println("<title>Servlet DisplayServlet</title>");			
			out.println("</head>");
			out.println("<body>");
			out.println("<h1>Servlet DisplayServlet at " + request.getContextPath() + "</h1>");
			out.println("</body>");
			out.println("</html>");
		}
		getServletConfig().getServletContext().setAttribute("INSTANCE", this);
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

}
