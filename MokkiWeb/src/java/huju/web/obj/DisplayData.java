/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package huju.web.obj;

/**
 *
 * @author huju
 */
public class DisplayData
{
	public static final int TYPE_INT = 0;
	public static final int TYPE_DOUBLE = 1;
	public static final int TYPE_STING = 2;
	
	private Object data;
	private int type; 
		
		
	public DisplayData(Object data, int type) 
	{
		this.data = data;
		this.type = type;
	}

	/**
	 * @return the data
	 */
	public Object getData()
	{
		return data;
	}

	/**
	 * @return the type
	 */
	public int getType()
	{
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(int type)
	{
		this.type = type;
	}
}
