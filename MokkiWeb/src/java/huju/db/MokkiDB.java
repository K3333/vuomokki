/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package huju.db;

import huju.mcu.datatypes.HumidityTemperature;
import huju.mcu.device.DeviceId;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author huju
 */
public class MokkiDB
{
	static 
	{
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
			Logger.getLogger(MokkiDB.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	public void connect() {
		/*
		Context initCtx;
		try {
			initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			DataSource ds = (DataSource) envCtx.lookup("jdbc/mokkidb");
			
			//Connection connection = ds.getConnection();
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mokkidb","tomcat","");
			Statement stat = connection.createStatement();
			ResultSet rs = stat.executeQuery("SELECT * FROM hum_tem_data");
			while (rs.next()) {
				System.out.println("ROW!");
			}
			System.out.println("CONNECTED TO DB");
		} catch (NamingException ex) {
			Logger.getLogger(MokkiDB.class.getName()).log(Level.SEVERE, null, ex);
		} catch (Exception ex) {
			Logger.getLogger(MokkiDB.class.getName()).log(Level.SEVERE, null, ex);
		}
*/
	}
	
	public void storeHumidity(DeviceId id, HumidityTemperature hum, long scanInterval)
	{
		Context initCtx;
		try {
			initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mokkidb","tomcat","");
			PreparedStatement ps = connection.prepareStatement("insert into hum_tem_data (device_id,humidity,temperature, time, scan_interval) values(?,?,?,?,?)");
			
			Timestamp ts = new Timestamp(hum.getTimestamp());
			int pi = 1;
			ps.setInt(pi++, id.getDeviceCode());
			ps.setDouble(pi++, hum.getHumidity());
			ps.setDouble(pi++, hum.getTemperature());
			ps.setTimestamp(pi++, ts);
			ps.setInt(pi++, 0);
			ps.execute();
			ps.close();
	
		} catch (NamingException ex) {
			Logger.getLogger(MokkiDB.class.getName()).log(Level.SEVERE, null, ex);
		} catch (Exception ex) {
			Logger.getLogger(MokkiDB.class.getName()).log(Level.SEVERE, null, ex);
		}

	}
}
