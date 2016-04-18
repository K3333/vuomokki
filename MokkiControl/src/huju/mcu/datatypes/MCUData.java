package huju.mcu.datatypes;

import huju.mcu.device.ActionType;
import huju.mcu.device.DeviceType;
import huju.mcu.device.SourceBus;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all data types.
 *
 */
public abstract class MCUData
{

	private ActionType dataType;
	private int referenceId;
	private DeviceType deviceType;
	private SourceBus sourceBus;
	private static String receiveBuffer = null;

	public MCUData()
	{

	}

	public MCUData(ActionType dataType, DeviceType deviceType, SourceBus sourceBus)
	{
		this.dataType = dataType;
		this.deviceType = deviceType;
		this.sourceBus = sourceBus;
	}

	public ActionType getDataType()
	{
		return dataType;
	}

	public void setDataType(ActionType dataType)
	{
		this.dataType = dataType;
	}

	public int getReferenceId()
	{
		return referenceId;
	}

	public void setReferenceId(int referenceId)
	{
		this.referenceId = referenceId;
	}

	public DeviceType getDeviceType()
	{
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType)
	{
		this.deviceType = deviceType;
	}

	public SourceBus getSourceBus()
	{
		return sourceBus;
	}

	public void setSourceBus(SourceBus sourceBus)
	{
		this.sourceBus = sourceBus;
	}

	public static List<MCUData> constuctData(String dataLine) throws InvalidDataFormatException
	{
		if (dataLine == null || dataLine.length() == 0) {
			return null;
		}
		StringBuffer buf = new StringBuffer();
		if (receiveBuffer != null && receiveBuffer.length() > 0) {
			buf = buf.append(receiveBuffer);
		}
		buf = buf.append(dataLine);
		int offset = 0;
		int pos = 0;
		List<String> lines = new ArrayList<String>();
		while (pos < buf.length()
			&& (offset = buf.indexOf("\r\n", pos)) > 0) {
			lines.add(buf.substring(pos, offset));
			pos = offset + 2;
		}
		if (pos < buf.length()) {
			receiveBuffer = buf.substring(pos);
		} else {
			receiveBuffer = null;
		}
		List<MCUData> datas = new ArrayList<MCUData>();
		for (String line : lines) {
			String[] args = line.split("[\t\\r\\n]");
			if (args.length < 3) {
				System.out.println("Corrupted data: [" + line + "]");
				continue;
			}
			String strType = args[1];
			if (strType != null) {
				try {
					int dataType = Integer.parseInt(strType.trim());
					if (dataType == DeviceType.TEMPERATURE_HUMIDITY_SENSOR.getDeviceTypeCode()) {
						datas.add(HumidityTemperature.construct(args));
						continue;
					}
					if ((dataType == DeviceType.MOTION_DETECTOR.getDeviceTypeCode())) {
						datas.add(MotionDetect.construct(args));
						continue;
					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					System.out.printf("Failed to parse [%s]! %s", line, e.getMessage());
					continue;
				}
			}
		}
		return datas;
	}
}