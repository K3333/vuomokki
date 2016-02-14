package huju.mcu.device;


public enum SourceBus 
{
	UNDEFINED (0),
	/** TwoWire (I2C) buses **/
	BUS_TWIM0 (1),
	BUS_TWIM1 (2),
	BUS_TWIM2 (3),
	BUS_TWIM3 (4),
	/** General purpose IO pins */
	GPIO_EXT1_5 (5),
	GPIO_EXT1_6 (6),
	GPIO_EXT2_5 (7),
	GPIO_EXT2_6 (8),
	GPIO_EXT2_10 (9),
	GPIO_EXT3_5 (10),
	GPIO_EXT3_6 (11),
	GPIO_EXT3_10 (12),
	GPIO_EXT3_15 (13),
	GPIO_EXT4_5 (14),
	GPIO_EXT4_6 (15),
	GPIO_EXT4_9 (16),
	GPIO_EXT4_10 (17),
	GPIO_EXT4_15 (18),
	/** Analog to Digital Converter Interfaces */
	ADCIFE_0 (16),
	ADCIFE_1 (17),
	ADCIFE_2 (18),
	ADCIFE_3 (19),
	ADCIFE_4 (20),
	ADCIFE_5 (21),
	ADCIFE_6 (22),
	ADCIFE_7 (23);
	
	private final int busCode;

    SourceBus(int busCode) {
        this.busCode = busCode;
    }
    
    public int getBusCode() {
    	return this.busCode;
    }
    
    public static SourceBus getSourceBus(String name) 
    {
    	for (SourceBus sb : values()) {
    		if (sb.name().equals(name)) {
    			return sb;
    		}
    	}
    	return UNDEFINED;
    }
    
    
    public static SourceBus getSourceBus(int busCode)
    {
        for (SourceBus bus : values())
        {
            if (bus.getBusCode() == busCode) {
                return  bus;
            }
        }
        return UNDEFINED;
        /*
    	if (busCode==BUS_TWIM0.getBusCode()) return BUS_TWIM0;
    	if (busCode==BUS_TWIM1.getBusCode()) return BUS_TWIM1;
    	if (busCode==BUS_TWIM2.getBusCode()) return BUS_TWIM2;
    	if (busCode==BUS_TWIM3.getBusCode()) return BUS_TWIM3;
    	if (busCode==GPIO_EXT1_5.getBusCode()) return GPIO_EXT1_5;
    	if (busCode==GPIO_EXT1_6.getBusCode()) return GPIO_EXT1_6;
    	if (busCode==GPIO_EXT2_5.getBusCode()) return GPIO_EXT2_5;
    	if (busCode==GPIO_EXT2_10.getBusCode()) return GPIO_EXT2_10;
    	if (busCode==GPIO_EXT3_5.getBusCode()) return GPIO_EXT3_5;
    	if (busCode==GPIO_EXT3_6.getBusCode()) return GPIO_EXT3_6;
    	if (busCode==GPIO_EXT3_10.getBusCode()) return GPIO_EXT3_10;
    	if (busCode==GPIO_EXT3_15.getBusCode()) return GPIO_EXT3_15;
    	if (busCode==GPIO_EXT4_5.getBusCode()) return GPIO_EXT4_5;
    	if (busCode==GPIO_EXT4_6.getBusCode()) return GPIO_EXT4_6;
    	if (busCode==GPIO_EXT4_9.getBusCode()) return GPIO_EXT4_9;
    	if (busCode==GPIO_EXT4_10.getBusCode()) return GPIO_EXT4_10;
    	if (busCode==GPIO_EXT4_15.getBusCode()) return GPIO_EXT4_15;
    	return UNDEFINED;
*/
    }
    
    public String toString()
    {
    	if (busCode==BUS_TWIM0.getBusCode()) return "I2C Slave/Master 1";
    	if (busCode==BUS_TWIM1.getBusCode()) return "I2C Slave/Master 2";
    	if (busCode==BUS_TWIM2.getBusCode()) return "I2C Slave/Master 3";
    	if (busCode==BUS_TWIM3.getBusCode()) return "I2C Slave/Master 4";
    	if (busCode==GPIO_EXT1_5.getBusCode()) return "GPIO - PIN 5/EXT1";
    	if (busCode==GPIO_EXT1_6.getBusCode()) return "GPIO - PIN 6/EXT1";
    	if (busCode==GPIO_EXT2_5.getBusCode()) return "GPIO - PIN 5/EXT2";
    	if (busCode==GPIO_EXT2_10.getBusCode()) return "GPIO - PIN 10/EXT2";
    	if (busCode==GPIO_EXT3_5.getBusCode()) return "GPIO - PIN 5/EXT3";
    	if (busCode==GPIO_EXT3_6.getBusCode()) return "GPIO - PIN 6/EXT3";
    	if (busCode==GPIO_EXT3_10.getBusCode()) return "GPIO - PIN 10/EXT3";
    	if (busCode==GPIO_EXT3_15.getBusCode()) return "GPIO - PIN 15/EXT3";
    	if (busCode==GPIO_EXT4_5.getBusCode()) return "GPIO - PIN 5/EXT4";
    	if (busCode==GPIO_EXT4_6.getBusCode()) return "GPIO - PIN 6/EXT4";
    	if (busCode==GPIO_EXT4_9.getBusCode()) return "GPIO - PIN 9/EXT4";
    	if (busCode==GPIO_EXT4_10.getBusCode()) return "GPIO - PIN 10/EXT4";
    	if (busCode==GPIO_EXT4_15.getBusCode()) return"GPIO - PIN 15/EXT4";
    	return "unknown";
    }
}
