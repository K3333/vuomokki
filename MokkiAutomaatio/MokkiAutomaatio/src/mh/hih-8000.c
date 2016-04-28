/*
 * hih_8000.c
 *
 * Created: 19.11.2015 20:45:09
 *  Author: Mika Hujanen
 */ 

#include "hih-8000.h"
#include <twim.h>
#include <asf.h>



const uint8_t write_cmd_data[] = {0};

#define PATTERN_TEST_LENGTH (sizeof(read_data)/sizeof(uint8_t))

//! Array to store the received test data
//uint8_t read_data[PATTERN_TEST_LENGTH];
uint8_t read_data[DATA_BUF_LEN];
//! TWI data package
twi_package_t packet_tx, packet_rx;

const Twim* twims[4];


status_code_t init_hih_sensor(Twim *twim)
{

	/* Set TWIM options */
	uint32_t cpu_speed  = sysclk_get_peripheral_bus_hz(twim);
	struct twim_config opts = {
		.twim_clk = cpu_speed,
		.speed = TWI_STD_MODE_SPEED,
		.hsmode_speed = 0,
		.data_setup_cycles = 0,
		.hsmode_data_setup_cycles = 0,
		.smbus = false,
		.clock_slew_limit = 0,
		.clock_drive_strength_low = 0,
		.data_slew_limit = 0,
		.data_drive_strength_low = 0,
		.hs_clock_slew_limit = 0,
		.hs_clock_drive_strength_high = 0,
		.hs_clock_drive_strength_low = 0,
		.hs_data_slew_limit = 0,
		.hs_data_drive_strength_low = 0,
	};
	/* Initialize the TWIM Module */
	twim_set_callback(twim, 0, twim_default_callback, 1);

	return twim_set_config(twim, &opts);
}


static Twim* get_twim(int twim_nr)
{
	switch (twim_nr) {
		case 0: return TWIM0;
		case 1: return TWIM1;
		case 2: return TWIM2;
		case 3: return TWIM3;
		default:
			return NULL;
	}
	
}

HIH8000 parseData(uint8_t read_data[]) 
{
	HIH8000 hih_data;
	int status = (read_data[0] >> 6) & 0x03;
	int hum_adc = (read_data[0] << 8| read_data[1]) & 0x3FFF;
	double  rh = (double) hum_adc / 16382 * 100;
	int tem_adc  = read_data[2]<<6 | ((read_data[3] >> 2) & 0x3F);
	double temp = (double) tem_adc / 16382 * 165 - 40;
	hih_data.status = status;
	hih_data.humidity = rh;
	hih_data.temperature = temp;
	return hih_data;
}

static status_code_t write_cmd(Twim *twim)
{
	/* TWI chip address to communicate with */
	packet_tx.chip = TWI_ADDRESS;
	/* TWI address/commands to issue to the other chip (node) */
	packet_tx.addr[0] = (VIRTUALMEM_ADDR >> 16) & 0xFF;
	packet_tx.addr[1] = (VIRTUALMEM_ADDR >> 8) & 0xFF;
	/* Length of the TWI data address segment (1-3 bytes) */
	packet_tx.addr_length = TWI_ADDRESS_LEN;
	/* Where to find the data to be written */
	packet_tx.buffer = (void *) write_cmd_data;
	/* How many bytes do we want to write */
	packet_tx.length = 1;
	printf("Writing data to TARGET\r\n");
	/* Write data to TARGET */
	return twi_master_write(twim, &packet_tx);
}

/**
 * \brief Read the data pattern from the target.
 *
 * \return STATUS_OK   If all bytes were read, error code otherwise
 */
static status_code_t read_hihsensor(Twim *twim, void* data_buffer)
{
	/* TWI chip address to communicate with */
	packet_rx.chip = TWI_ADDRESS;
	/* Length of the TWI data address segment (1-3 bytes) */
	packet_rx.addr_length = TWI_ADDRESS_LEN;
	/* How many bytes do we want to write */
	packet_rx.length = DATA_BUF_LEN;
	/* TWI address/commands to issue to the other chip (node) */
	int i = 0;
	if (TWI_ADDRESS_LEN>2)
	{
		packet_rx.addr[i++] = (VIRTUALMEM_ADDR >> 24) & 0xFF;	
	}
	if (TWI_ADDRESS_LEN>1)
	{
		packet_rx.addr[i++] = (VIRTUALMEM_ADDR >> 16) & 0xFF;
	}
	packet_rx.addr[i] = (VIRTUALMEM_ADDR >>8) & 0xFF;
	/* Where to find the data to be written */
	packet_rx.buffer = data_buffer;
	/* Read data from TARGET */
	return twi_master_read(twim, &packet_rx);
}


HIH8000 readHIH8000(int twim_nr)
{
	HIH8000 hih_data;
	if (twim_nr<0 || twim_nr>3)
	{
		hih_data.status = InvalidTwimNum;
		return hih_data;
		
	}
	status_code_t status;
	Twim *twim;
	if (twims[twim_nr] == NULL) {
		twim = get_twim(twim_nr);
		status = init_hih_sensor(twim);
		if (status == STATUS_OK) {
			twims[twim_nr] = twim;
		} else {
			hih_data.status = TwimInitError;
			return hih_data;
		}
	} else {
		twim = twims[twim_nr];
	}
	//#ifdef SEND_TWI_COMMAND
	//	write_cmd(twim);  
	//#endif // SEND_TWI_COMMAND
	uint8_t read_data[DATA_BUF_LEN];
	status = read_hihsensor(twim, read_data);
	if (status != STATUS_OK) {
		hih_data.status = status;// TwimReadError;
		return hih_data;
	}
	hih_data = parseData(read_data);
	return hih_data;
}

