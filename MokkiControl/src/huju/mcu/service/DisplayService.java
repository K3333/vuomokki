/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package huju.mcu.service;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author huju
 */
public class DisplayService
{
	public static final int D1 = 0;
	public static final int D2 = 1;
	public static final int D3 = 2;
	public static final int D4 = 3;
	public static final int A = 4;
	public static final int B = 5;
	public static final int C = 6;
	public static final int D = 7;
	public static final int E = 8;
	public static final int F = 9;
	public static final int G = 10;
	public static final int DP = 11;
	
	/**
	 * PI4J PINS
	 */
	private final Pin[] pins = new Pin[] {
		RaspiPin.GPIO_00, // D1
		RaspiPin.GPIO_02, // D2
		RaspiPin.GPIO_04, // D3
		RaspiPin.GPIO_03,  // D4
		RaspiPin.GPIO_26, // A
		RaspiPin.GPIO_29, // B
		RaspiPin.GPIO_23, // C
		RaspiPin.GPIO_24, // D
		RaspiPin.GPIO_06, // E,
		RaspiPin.GPIO_25, // F
		RaspiPin.GPIO_27, // G
		RaspiPin.GPIO_28, // DP
	};
	
	/**
	 * GPIO values for each 7 segments (A-G) of numbers from 0 to 9.
	 * Segment A bit is MSB of seven bit value and segment G is LSB,
	 * then for example number 0 has all other segments except G on,
	 * bit presentation is 1111110 = 0x7F
	 */
	private static final int[] segments = new int[] 
	{
		// 0,1,2,3...9
		0x7e, 0x30, 0x6D, 0x79, 0x33, 0x5B, 0x5F, 0x70, 0x7F, 0x7B 
	};
	// Same as above but for chars in two dimension int array 
	private static final int[][] charSeqments = 
	{{'0','1','2','3','4','5','6','7','8','9',
	  ' ','C', 'c', (char) 248,'O','o','U','u','S','s',
	  'H', 'h','J','j','I','i','L','l','A','a',
	  'B','b','D','d','k','K','T','t','G','g',
	  'E','e','F','f','M','m','N','n','P','p',
	  'r','R','-','/', (char) 176},
	 {0x7e,0x30,0x6d,0x79,0x33,0x5b,0x5f,0x70,0x7F,0x7b,
	  0x00,0x4E,0x0d,0x63,0x7e,0x1d,0x3E,0x1c,0x5b,0x5b,
	  0x37,0x17,0x3c,0x3c,0x30,0x10,0x0e,0x06,0x77,0x7d,
	  0x1f,0x1f,0x3d,0x3d,0x2f,0x57,0x70,0x0f,0x5e,0x7b,
	  0x4f,0x6f,0x47,0x47,0x54,0x14,0x76,0x15,0x67,0x67,
	  0x66,0x05,0x08,0x25,0x63}
	}; 
	

	
	private final GpioController gpio = GpioFactory.getInstance();
	private GpioPinDigitalOutput[] outputs;
	
	public DisplayService()
	{
		outputs = new GpioPinDigitalOutput[pins.length];
		for (int y = 0; y < pins.length; y++) {
			outputs[y] = gpio.provisionDigitalOutputPin(pins[y]);
			outputs[y].setShutdownOptions(true, PinState.HIGH);
		}

	}
	
	public void shutdown() 
	{
		gpio.shutdown();
	}
	
	public void test(int d)
	{
		outputs[d].low();
		/*
		for (int i = A; i <= G; i++) {
			outputs[i] = gpio.provisionDigitalOutputPin(pins[i]);
			if (i==A || i==F) {
				outputs[i].low();
			} else {
				outputs[i].high();
			}
			
		}
		for (int i = D1; i <= D3; i++) {
			outputs[i] = gpio.provisionDigitalOutputPin(pins[i]);
			if (i==d) {
				outputs[i].low();
			} else {
				outputs[i].high();
			}
			
		}
		*/

	}
	
	public void displayDouble(double d) 
	{
		String cur = Double.toString(d);
		int dotPos = cur.indexOf('.');
		int start = 4 - cur.length();
		if (dotPos<4 && dotPos>=0) {
			start++;
		}
		if (start < 0) {
			start = 0;
		}
		int pos = 0;
		for (int i=0; i<4; i++) 
		{
			for (int j=0; j<4; j++) {
				if (j!=i) outputs[j].setState(PinState.HIGH);
			}
			
			if (i >= start) {
				//gpio.provisionDigitalOutputPin(pins[i], PinState.HIGH);
				try {
					
					char c = cur.charAt(pos++);
					if (pos<cur.length() && cur.charAt(pos) == '.') {
						outputs[DP].setState(PinState.LOW);
						pos++;
					} else {
						outputs[DP].setState(PinState.HIGH);
					}
					if (c>='0'&&c<='9') {
						drawNumberSegments((int) (c - 48));
					} else 
					{
						displayCharSegments(c);
					}
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
				
			} else 
			{	
				outputs[DP].setState(PinState.HIGH);
				displayCharSegments(' ');
			}
			outputs[i].setState(PinState.LOW);
		}

	}
	
	public void displayString(String str) {
		for (int i = 0; i<str.length(); i++) {
			for (int j=0; j<4; j++) {
				if (j!=i) outputs[j].setState(PinState.HIGH);
			}
			
			displayCharSegments(str.charAt(i));
			outputs[i].setState(PinState.LOW);
			if (i==3) break;
		}
	};
	
	private void displayCharSegments(char c) 
	{
		boolean found = false;
		for (int x = 0; x < charSeqments[0].length; x++) {
			if (charSeqments[0][x] == c) {
				for (int i = 0; i<7; i++) {
					boolean high = ((charSeqments[1][x] >> (6-i)) & 0x01) == 1;
					PinState state = high ? PinState.LOW : PinState.HIGH;
					outputs[i+4].setState(state);
					found = true;
				}
				break;
			} 
		}
		if (!found) {
			for (int i = 0; i<7; i++) {
				outputs[i+4].setState(PinState.HIGH);
			}
		}
	}
	
	private void drawNumberSegments(int num) 
	{
		// create gpio controller
		/*
		if (num < 0 || num > 9) {
			for (int i = 0; i<=7; i++) outputs[i+4].setState(PinState.LOW);
			return;
		}
		*/
		for (int i = 0; i<7; i++) {
			boolean high = ((segments[num] >> (6-i)) & 0x01) == 1; 
			outputs[i+4].setState(high ? PinState.LOW : PinState.HIGH);
		}
	}	

}
