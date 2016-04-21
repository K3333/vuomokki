/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package huju.db;

import huju.mcu.datatypes.HumidityTemperature;
import huju.mcu.datatypes.MotionDetect;
import huju.mcu.device.DeviceId;
import huju.mcu.device.DeviceType;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
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
	
	public List<MotionDetect> getMotionDetect(DeviceId id, long start, long end)
	{
		List <MotionDetect> mds = new ArrayList<MotionDetect>();
		Context initCtx;
		try {
			initCtx = new InitialContext();
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mokkidb","tomcat","");
			Timestamp tsStart = null;
			Timestamp tsEnd = null;
			String query = "select start,end,state from motion_data where device_id=?";
			if (start>0 && end > 0) {
				tsStart = new Timestamp(start);
				tsEnd = new Timestamp(end);
				query += " and time>=? and time < ?"; 
			} else if (start>0) {
				tsStart = new Timestamp(start);
				query += " and time>=?";
			} else if(end>0) {
				tsEnd = new Timestamp(end);
				query += " and time<?";
			}
			query += " ORDER BY start DESC";
			
			PreparedStatement ps = connection.prepareStatement(query);
			int pi = 1;
			ps.setInt(pi++, id.getDeviceCode());
			if (tsStart!=null) {
				ps.setTimestamp(pi++, tsStart);
			}
			if (tsEnd!=null) {
				ps.setTimestamp(pi++, tsEnd);
			}
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				System.out.println("ROW!");
				MotionDetect md = new MotionDetect();
				Timestamp rsStart = rs.getTimestamp("start");
				Timestamp rsEnd = rs.getTimestamp("end");
				int state = rs.getInt("state");
				md.setStartTime(rsStart!=null ? rsStart.getTime() : -1);
				md.setEndTime(rsEnd!=null ? rsEnd.getTime() : -1);
				md.setPinLevel(state);
				mds.add(md);
			}
		} catch (NamingException ex) {
			Logger.getLogger(MokkiDB.class.getName()).log(Level.SEVERE, null, ex);
		} catch (Exception ex) {
			Logger.getLogger(MokkiDB.class.getName()).log(Level.SEVERE, null, ex);
		}
		return mds;
	}
	
	public void storeMotionDetect(DeviceId id, MotionDetect motion)
	{
		Context initCtx;
		try {
			initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mokkidb","tomcat","");
			PreparedStatement ps = connection.prepareStatement("insert into motion_data (device_id, state, start, end) values(?,?,?,?)");
			
			Timestamp tsStart = new Timestamp(motion.getStartTime());
			Timestamp tsEnd = null;
			if (motion.getEndTime()>0) {
				tsEnd = new Timestamp(motion.getEndTime());
			}
			
			int pi = 1;
			ps.setInt(pi++, id.getDeviceCode());
			ps.setInt(pi++, motion.getPinLevel());
			ps.setTimestamp(pi++,tsStart);
			if (tsEnd!=null) {
				ps.setTimestamp(pi++, tsEnd);
			} else {
				ps.setNull(pi++, Types.TIMESTAMP);
			}

			ps.executeUpdate();
			ps.close();
	
		} catch (NamingException ex) {
			Logger.getLogger(MokkiDB.class.getName()).log(Level.SEVERE, null, ex);
		} catch (Exception ex) {
			Logger.getLogger(MokkiDB.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public List <HumidityTemperature>  getHumidity(DeviceId id, long start, long end) 
	{
		List <HumidityTemperature> hts = new ArrayList<HumidityTemperature>();
			
		Context initCtx;
		try {
			initCtx = new InitialContext();
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mokkidb","tomcat","");
			Timestamp tsStart = null;
			Timestamp tsEnd = null;
			String query = "select humidity,temperature,time from hum_tem_data where device_id=?";
			if (start>0 && end > 0) {
				tsStart = new Timestamp(start);
				tsEnd = new Timestamp(end);
				query += " and time>=? and time < ?"; 
			} else if (start>0) {
				tsStart = new Timestamp(start);
				query += " and time>=?";
			} else if(end>0) {
				tsEnd = new Timestamp(end);
				query += " and time<?";
			}
			query += " ORDER BY time DESC";
			
			PreparedStatement ps = connection.prepareStatement(query);
			int pi = 1;
			ps.setInt(pi++, id.getDeviceCode());
			if (tsStart!=null) {
				ps.setTimestamp(pi++, tsStart);
			}
			if (tsEnd!=null) {
				ps.setTimestamp(pi++, tsEnd);
			}
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				System.out.println("ROW!");
				HumidityTemperature ht = new HumidityTemperature();
				ht.setHumidity(rs.getDouble("humidity"));
				ht.setTemperature(rs.getDouble("temperature"));
				Timestamp time = rs.getTimestamp("time");
				ht.setTimestamp(time.getTime());
				ht.setDeviceType(DeviceType.TEMPERATURE_HUMIDITY_SENSOR);
				hts.add(ht);
;
			}
			ps.close();
	
		} catch (NamingException ex) {
			Logger.getLogger(MokkiDB.class.getName()).log(Level.SEVERE, null, ex);
		} catch (Exception ex) {
			Logger.getLogger(MokkiDB.class.getName()).log(Level.SEVERE, null, ex);
		}
		return hts;
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
			ps.executeUpdate();
			ps.close();
	
		} catch (NamingException ex) {
			Logger.getLogger(MokkiDB.class.getName()).log(Level.SEVERE, null, ex);
		} catch (Exception ex) {
			Logger.getLogger(MokkiDB.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
