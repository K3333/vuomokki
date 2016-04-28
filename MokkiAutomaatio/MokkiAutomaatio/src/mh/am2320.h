/*
 * am2320.h
 *
 * Created: 19.11.2015 20:46:51
 *  Author: huju
 */ 


#ifndef AM2320_H_
#define AM2320_H_


#define AM2320_TWI_ADDRESS         0xB8
#define AM2320_TWI_ADDRESS_LEN     1/** Internal Address */
#define VIRTUALMEM_ADDR      0x00
/** Speed of TWI */
#define TWIM_MASTER_SPEED    TWI_STD_MODE_SPEED

#define AM2320_DATA_BUF_LEN         8


/** Enumeration of status codes of read operation.
	
	NormalOperation - Got fresh data that has not been read before
	StaleData - Got readings that has been already fetch in previous operations
	CommandMode - Device is in programming state. Should not never happen
	InvalidTwinNum - TWI module number is out of valid range.
	TwimInitError - Module in TWI port could not be initialized.
	TwimReadError - Error was occurred during the data read.      
	*/	
enum AMStatus {
	Success = 0,
	SensorInitError=1,
	SensorReadError = 2
	};

typedef struct {
	int status;
	double humidity;
	double temperature;
} AM2320;

AM2320 readAM2320(int twim_nr);

#endif /* AM2320_H_ */