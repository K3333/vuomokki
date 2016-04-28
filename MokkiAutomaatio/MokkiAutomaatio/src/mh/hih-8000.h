/*
 * hih_8000.h
 *
 * Created: 19.11.2015 20:46:51
 *  Author: huju
 */ 


#ifndef HIH-8000_H_
#define HIH-8000_H_


#define TWI_ADDRESS         0x27
#define TWI_ADDRESS_LEN     2
/** Internal Address */
#define VIRTUALMEM_ADDR      0x0
/** Speed of TWI */
#define TWIM_MASTER_SPEED    TWI_STD_MODE_SPEED
/** TWIM Interrupt Handler */
#define EXAMPLE_TWIM_Handler TWIM3_Handler

#define DATA_BUF_LEN         8

enum TWIM_NUM {
	TWIM_1 = 1,
	TWIM_2 = 2,
	TWIM_3 = 3
	};

/** Enumeration of status codes of read operation.
	
	NormalOperation - Got fresh data that has not been read before
	StaleData - Got readings that has been already fetch in previous operations
	CommandMode - Device is in programming state. Should not never happen
	InvalidTwinNum - TWI module number is out of valid range.
	TwimInitError - Module in TWI port could not be initialized.
	TwimReadError - Error was occurred during the data read.      
	*/	
enum Status {
	NormalOperation = 0,
	StaleData = 1,
	CommandMode = 2,
	InvalidTwimNum = 3,
	TwimInitError=4,
	TwimReadError = 5
	
	};

typedef struct {
	int status;
	double humidity;
	double temperature;
} HIH8000;

HIH8000 readHIH8000(int twim_nr);

#endif /* HIH-8000_H_ */