/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package huju.mcu.datatypes;

/**
 *
 * @author huju
 */
public class GPIOValue extends MCUData
{
	private int pinLevel;

	public int getPinLevel() 
	{
		return pinLevel;
	}
	public void setPinLevel(int pinLevel) 
	{
		this.pinLevel = pinLevel;
	}
	
	@Override
	public String toString() 
	{
		// TODO Auto-generated method stub
		return getDeviceType().toString() + 
				" @" + getSourceBus().toString() + 
				"-> PIN LEVEL: "+ getPinLevel();
	}
}
