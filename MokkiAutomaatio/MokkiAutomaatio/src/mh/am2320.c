/*
 * am2320.c
 *
 * Created: 19.11.2015 20:45:09
 *  Author: Mika Hujanen
 */ 

#include "am2320.h"
#include <twim.h>
#include <asf.h>
#include <delay.h>

//! TWI data package
twi_package_t packet_tx, packet_rx;
uint32_t cpu_speed = 0;
volatile uint8_t am_read_data[AM2320_DATA_BUF_LEN];
const uint8_t read_command[3] = {0x03,0x00,0x04}; 
const Twim* twims[4];


status_code_t init_am2320_twi(Twim *twim)
{

	/* Set TWIM options */
	cpu_speed = sysclk_get_peripheral_bus_hz(twim);
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

AM2320 parseAm2320Data(uint8_t read_data[]) 
{
	AM2320 am_data;
	int hum_adc = (read_data[0] << 8) | read_data[1];
 	double  rh = (double) hum_adc / 10;
	int tem_adc  = ((read_data[2] & 0x7F) << 8) | read_data[3] ; 
	double temp = (double) tem_adc / 10;
	if ((read_data[2] & 0x80) != 0) {
		temp = -temp;
	}
	am_data.status = Success;
	am_data.humidity = rh;
	am_data.temperature = temp;
	return am_data;
}

static status_code_t write_am_cmd(Twim *twim, unsigned char *data, int length)
{
	uint8_t write_cmd_data[1+length];
	write_cmd_data[0] = AM2320_TWI_ADDRESS;
	for (int i=0; i<length; i++) {
		write_cmd_data[i+2] = data[i];
	}
	/* TWI chip address to communicate with */
	packet_tx.chip = AM2320_TWI_ADDRESS;
	/* TWI address/commands to issue to the other chip (node) */
	packet_tx.addr[0] = (VIRTUALMEM_ADDR >> 16) & 0xFF;
	packet_tx.addr[1] = (VIRTUALMEM_ADDR >> 8) & 0xFF;
	/* Length of the TWI data address segment (1-3 bytes) */
	packet_tx.addr_length = AM2320_TWI_ADDRESS_LEN;
	/* Where to find the data to be written */
	packet_tx.buffer = (void *) write_cmd_data;
	/* How many bytes do we want to write */
	packet_tx.length = 1+length;
	printf("Writing data to TARGET\r\n");
	/* Write data to TARGET */
	return twi_master_write(twim, &packet_tx);
}

/**
 * \brief Read the data pattern from the target.
 *
 * \return STATUS_OK   If all bytes were read, error code otherwise
 */
static status_code_t read_sensor(Twim *twim, void* data_buffer)
{
	/* TWI chip address to communicate with */
	packet_rx.chip = AM2320_TWI_ADDRESS;
	/* Length of the TWI data address segment (1-3 bytes) */
	packet_rx.addr_length = AM2320_TWI_ADDRESS_LEN;
	/* How many bytes do we want to write */
	packet_rx.length = AM2320_DATA_BUF_LEN;
	/* TWI address/commands to issue to the other chip (node) */
	int i = 0;
	if (AM2320_TWI_ADDRESS_LEN>2)
	{
		packet_rx.addr[i++] = (VIRTUALMEM_ADDR >> 24) & 0xFF;	
	}
	if (AM2320_TWI_ADDRESS_LEN>1)
	{
		packet_rx.addr[i++] = (VIRTUALMEM_ADDR >> 16) & 0xFF;
	}
	packet_rx.addr[i] = (VIRTUALMEM_ADDR >>8) & 0xFF;
	/* Where to find the data to be written */
	packet_rx.buffer = data_buffer;
	/* Read data from TARGET */
	return twi_master_read(twim, &packet_rx);
}


AM2320 readAM2320(int twim_nr)
{
	AM2320 am_data;
	if (twim_nr<0 || twim_nr>3)
	{
		am_data.status = SensorInitError;
		return am_data;
		
	}
	volatile status_code_t status;
	Twim *twim;
	if (twims[twim_nr] == NULL) {
		twim = get_twim(twim_nr);
		status = init_am2320_twi(twim);
		if (status == STATUS_OK) {
			twims[twim_nr] = twim;
		} else {
			am_data.status = SensorInitError;
			return am_data;
		}
	} else {
		twim = twims[twim_nr];
	}
	//#ifdef SEND_TWI_CO	
	//Wake
	status = write_am_cmd(twim, 0, 0);
	 delay_ms(200);
	 
	 status = write_am_cmd(twim, read_command, 3);
	 delay_ms(2);
	 
	// status = write_am_cmd(twim, 1, 0, 0);
	 //delay_ms(200);
	 
 
	//#endif // SEND_TWI_COMMAND
	
	/*am_read_data[0] = 0x03;
	am_read_data[1] = 0x00;
	am_read_data[2] = 0x04;*/
	for (int i=0; i < 8; i++) {
		am_read_data[i] = 0x00;
	}
	status = read_sensor(twim, am_read_data);
	if (status != STATUS_OK) {
		am_data.status = SensorReadError;// TwimReadError;
		return am_data;
	}
	am_data = parseAm2320Data(am_read_data);
	return am_data;
}

