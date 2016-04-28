/**
 * \file
 *
 * \brief TWIM Master Example for SAM.
 *
 * Copyright (c) 2012-2015 Atmel Corporation. All rights reserved.
 *
 * \asf_license_start
 *
 * \page License
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The name of Atmel may not be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * 4. This software may only be redistributed and used in connection with an
 *    Atmel microcontroller product.
 *
 * THIS SOFTWARE IS PROVIDED BY ATMEL "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT ARE
 * EXPRESSLY AND SPECIFICALLY DISCLAIMED. IN NO EVENT SHALL ATMEL BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * \asf_license_stop
 *
 */

/**
 *  \mainpage TWIM Master Example
 *
 *  \par Purpose
 *
 *  This application gives an example of how to use TWIM driver of SAM to
 *  access an TWI-compatible EEPROM.
 *
 *  \par Requirements
 *
 *  The program needs a TWI-compatible EEPROM connected with the TWIM module.
 *  See the connection below:
 *  \copydoc twim_master_example_pin_defs
 *
 *  \par Description
 *
 *  At first, the specified TWIM write some data pattern to the EEPROM, then
 *  read it back and check if the written and the read match.
 *
 * \par Usage
 *
 *  -# Build the program and download it into the evaluation board.
 *  -# Connect a serial cable to the UART port for each evaluation kit.
 *  -# On the computer, open and configure a terminal application (e.g.,
 *     HyperTerminal on Microsoft Windows) with these settings:
 *        - 115200 bauds
 *        - 8 data bits
 *        - No parity
 *        - 1 stop bit
 *        - No flow control
 *  -# Start the application. The following traces shall appear on the terminal:
 *     \code
	     -- TWIM Master Example --
	     -- xxxxxx-xx
	     -- Compiled: xxx xx xxxx xx:xx:xx --

\endcode
 *
 */
/*
 * Support and FAQ: visit <a href="http://www.atmel.com/design-support/">Atmel Support</a>
 */


#include <asf.h>
#include <delay.h>
#include <string.h>
#include "mh\hih-8000.h"
#include "mh\am2320.h"
#include "conf_example.h"
#include "mh/mcudatatypes.h"
#include "sleepmgr.h"


#define MOTIOND_1_PIN_EVENT     PIN_PC08 //(EXT2/PIN5) 
#define MOTION_1_PIN_SOUCE     GPIO_EXT2_5 //(EXT2/PIN5) 

#define MOTIOND_2_PIN_EVENT     PIN_PB12 // (EXT1/PIN5) 
#define MOTION_2_PIN_SOUCE     GPIO_EXT1_5 //(EXT2/PIN5) 
#define ONBOARD_LED_PORT (2)
#define ONBOARD_LED_MASK ((1 << 7))

/** Timer counter frequency in Hz. */
#define TC_FREQ             10

#define BOARD_ID_USART             USART1
#define BOARD_USART                USART1
#define BOARD_USART_BAUDRATE       115200
#define USART_Handler              USART1_Handler
#define USART_IRQn                 USART1_IRQn
#define PDCA_PID_USART_RX          1
#define PDCA_PID_USART_TX          19
#define PDCA_RX_CHANNEL  0
#define PDCA_TX_CHANNEL  1
/** Size of the receive buffer used by the PDCA, in bytes. */
#define BUFFER_SIZE         100
/** Max buffer number. */
#define MAX_BUF_NUM         1
/** All interrupt mask. */
#define ALL_INTERRUPT_MASK  0xffffffff
/** Timer counter frequency in Hz. */
#define TC_FREQ             10
/** Receive buffer. */
static uint8_t gs_puc_buffer[2][BUFFER_SIZE];

/** Next Receive buffer. */
static uint8_t gs_puc_nextbuffer[2][BUFFER_SIZE];

/** Current bytes in buffer. */
static uint32_t gs_ul_size_buffer = BUFFER_SIZE;

/** Current bytes in next buffer. */
static uint32_t gs_ul_size_nextbuffer = BUFFER_SIZE;

/** Buffer number in use. */
static uint8_t gs_uc_buf_num = 0;

/** Flag of one transfer end. */
static uint8_t g_uc_transend_flag = 0;

/** AST members for RTX command handling */
bool command_pending = false;
mcu_header waiting_command;
uint32_t ast_alarm;
uint32_t ast_counter;
uint8_t key;


/**
 *  Configure serial console.
 */
static void configure_console(void)
{
	const usart_serial_options_t uart_serial_options = {
		.baudrate = CONF_UART_BAUDRATE,
#ifdef CONF_UART_CHAR_LENGTH
		.charlength = CONF_UART_CHAR_LENGTH,
#endif
		.paritytype = CONF_UART_PARITY,
#ifdef CONF_UART_STOP_BITS
		.stopbits = CONF_UART_STOP_BITS,
#endif
	};

	/* Configure console UART. */
	stdio_serial_init(CONF_UART, &uart_serial_options);
}

/**
 * Push button 0 interrupt callback.
 * Writes status of the 
 */
void printDouble(double v, int decimalDigits)
{
	int i = 1;
	int intPart, fractPart;
	for (;decimalDigits!=0; i*=10, decimalDigits--);
	intPart = (int)v;
	fractPart = (int)((v-(double)(int)v)*i);
	printf("%i.%i", intPart, fractPart);
}


void doubleToString(char* str, double v, int decimalDigits) 
{
	int i = 1;
	int intPart, fractPart;
	for (;decimalDigits!=0; i*=10, decimalDigits--);
	intPart = (int)v;
	fractPart = (int)((v-(double)(int)v)*i);
	//snprintf(str, sizeof(str), "%i.%i", intPart,fractPart);
	sprintf(str, "%i.%i", intPart,fractPart);
	
}


			
/**
 * Push button 0 interrupt callback.
 * Writes status of the ioport to UART output
 */
//!	[example_pb0_callback]
static void motiond1_callback(void)
{
	/* Handle pin interrupt here e.g. toggle an LED */
	//LED_Toggle(LED0);
	//printf("pb0_callback %d!!!-\r\n",ioport_get_pin_level(MOTIOND_1_PIN_EVENT));
	
	int pin_val = ioport_get_pin_level(MOTIOND_1_PIN_EVENT);
	int value = 0;
	if (pin_val == IOPORT_PIN_LEVEL_HIGH ) {
		ioport_set_port_level(ONBOARD_LED_PORT, ONBOARD_LED_MASK, IOPORT_PIN_LEVEL_HIGH);
		value = 1;
	} else {
		ioport_set_port_level(ONBOARD_LED_PORT, ONBOARD_LED_MASK, IOPORT_PIN_LEVEL_LOW);
	}
	printf("%d\t%d\t%d\t%d\r\n", TRIGGERED_DATA, MOTION_DETECTOR, MOTION_1_PIN_SOUCE, value);
}
static void motiond2_callback(void)
{
	/* Handle pin interrupt here e.g. toggle an LED */
	//LED_Toggle(LED0);
	//printf("pb0_callback %d!!!-\r\n",ioport_get_pin_level(MOTIOND_1_PIN_EVENT));
	
	int pin_val = ioport_get_pin_level(MOTIOND_2_PIN_EVENT);
	int value = 0;
	if (pin_val == IOPORT_PIN_LEVEL_HIGH ) {
		value = 1;
	} 
	printf("%d\t%d\t%d\t%d\r\n", TRIGGERED_DATA, MOTION_DETECTOR, MOTION_2_PIN_SOUCE, value);
}

void init_GPIO_output(uint32_t pin_nr)
{
	ioport_set_pin_dir(pin_nr, IOPORT_DIR_OUTPUT);
}

void init_GPIO_input(uint32_t pin_nr, uint32_t edgeMode)
{
	uint32_t sense = IOPORT_SENSE_BOTHEDGES;
	if (edgeMode==GPIO_TRIGGER_RISING_EDGE) {
		sense = IOPORT_SENSE_RISING;
	} else if (edgeMode==GPIO_TRIGGER_FALLING_EDGE) {
		sense = IOPORT_SENSE_FALLING;
	} 
	/* Configure pin to trigger an event on falling edge */
	ioport_set_pin_dir(pin_nr, IOPORT_DIR_INPUT);
	ioport_set_pin_mode(pin_nr, IOPORT_MODE_PULLUP |
	IOPORT_MODE_MUX_C );
	ioport_enable_pin(pin_nr);
	ioport_set_pin_sense_mode(pin_nr, sense);
	gpio_set_pin_callback(pin_nr, motiond1_callback, 1);
	gpio_enable_pin_interrupt(pin_nr);
	
}

void initDetectionGPIO()
{
	
	/* Configure pin to trigger an event on falling edge */
	ioport_set_pin_dir(MOTIOND_1_PIN_EVENT, IOPORT_DIR_INPUT);
	ioport_set_pin_mode(MOTIOND_1_PIN_EVENT, IOPORT_MODE_PULLUP |
	IOPORT_MODE_MUX_C );
	ioport_enable_pin(MOTIOND_1_PIN_EVENT);
	ioport_set_pin_sense_mode(MOTIOND_1_PIN_EVENT, IOPORT_SENSE_BOTHEDGES);
	gpio_set_pin_callback(MOTIOND_1_PIN_EVENT, motiond1_callback, 1);
	gpio_enable_pin_interrupt(MOTIOND_1_PIN_EVENT);
	
}

void initDetectionGPIO_2()
{
	
	/* Configure pin to trigger an event on falling edge */
	ioport_set_pin_dir(MOTIOND_2_PIN_EVENT, IOPORT_DIR_INPUT);
	ioport_set_pin_mode(MOTIOND_2_PIN_EVENT, IOPORT_MODE_PULLUP |
	IOPORT_MODE_MUX_C );
	ioport_enable_pin(MOTIOND_2_PIN_EVENT);
	ioport_set_pin_sense_mode(MOTIOND_2_PIN_EVENT, IOPORT_SENSE_BOTHEDGES);
	gpio_set_pin_callback(MOTIOND_2_PIN_EVENT, motiond2_callback, 1);
	gpio_enable_pin_interrupt(MOTIOND_2_PIN_EVENT);
	
}



/** PDCA channel options. */
pdca_channel_config_t pdca_rx_options = {
	.addr = (void *)gs_puc_buffer, /* memory address */
	.pid = PDCA_PID_USART_RX, /* select peripheral - USART0 RX line.*/
	.size = BUFFER_SIZE, /* transfer counter */
	.r_addr = (void *)gs_puc_nextbuffer, /* next memory address */
	.r_size = BUFFER_SIZE, /* next transfer counter */
	.transfer_size = PDCA_MR_SIZE_BYTE, /* select size of the transfer */
	.etrig = false, /* disable event trigger*/
	.ring = false /* not use ring buffer*/
};
pdca_channel_config_t pdca_tx_options = {
	.addr = (void *)gs_puc_buffer, /* memory address */
	.pid = PDCA_PID_USART_TX, /* select peripheral - USART0 TX line.*/
	.size = 0, /* transfer counter */
	.r_addr = (void *)gs_puc_nextbuffer, /* next memory address */
	.r_size = 0, /* next transfer counter */
	.transfer_size = PDCA_MR_SIZE_BYTE, /* select size of the transfer */
	.etrig = false, /* disable event trigger*/
	.ring = false /* not use ring buffer*/
};


void readHumidity(int twim_nr, int readType)
{
	HIH8000 hih_data_1	= readHIH8000(twim_nr);
	int status = hih_data_1.status;
	int source;
	switch (twim_nr) {
		case 0:
		source = BUS_TWIM0;
		break;
		case 1:
		source = BUS_TWIM1;
		break;
		case 2:
		source = BUS_TWIM2;
		break;
		case 3:
		source = BUS_TWIM3;
		break;
		default:
		source = UNDEFINED;
		break;
		
	}
	if (status == NormalOperation || status == StaleData) {
		char hum[10];
		char temp[10];
		doubleToString(hum, hih_data_1.humidity,1);
		doubleToString(temp,hih_data_1.temperature,1);
		printf("%d\t%d\t%d\t%d\t%s\t%s\r\n", readType, TEMPERATURE_HUMIDITY_SENSOR, source, status,hum, temp);
	} else
	{
		printf("%d\t%d\t%d\t%d\t%s\t%s\r\n", readType, TEMPERATURE_HUMIDITY_SENSOR, source, status,0, 0);
	}
	
	
}

static void ast_command_ready_callback(void)
{
	ast_clear_interrupt_flag(AST, AST_INTERRUPT_ALARM);
	ast_disable_interrupt(AST, AST_INTERRUPT_ALARM);
	if (waiting_command.device_type == TEMPERATURE_HUMIDITY_SENSOR) {
		int twi = -1;
		if  (waiting_command.source_bus == BUS_TWIM0 ) twi = 0;
		else if  (waiting_command.source_bus == BUS_TWIM1 ) twi = 1;
		else if  (waiting_command.source_bus == BUS_TWIM2 ) twi = 2;
		else if  (waiting_command.source_bus == BUS_TWIM3 ) twi = 3;
		if (twi>=0) {
			delay_ms(500);
			readHumidity(twi, COMMAND_RESPONSE);
			//printf("%d\t%d\t%d\t%s\r\n", waiting_command.data_type , waiting_command.device_type, waiting_command.source_bus, waiting_command.content_data );
		}
	}
	membag_free(waiting_command.content_data);
	waiting_command.content_data = 0;
	
}

void handle_command(mcu_header header)
{
	if (header.data_type == COMMAND_REQUEST) {
		/*
		if (header.device_type == TEMPERATURE_HUMIDITY_SENSOR) {
			int twi = -1;
			if  (header.source_bus == BUS_TWIM0 ) twi = 0;
			else if  (header.source_bus == BUS_TWIM1 ) twi = 1;
			else if  (header.source_bus == BUS_TWIM2 ) twi = 2;
			else if  (header.source_bus == BUS_TWIM3 ) twi = 3;
			if (twi>=0) {
				//readHumidity(twi, COMMAND_RESPONSE);
			}
			
		}
		*/
		if (!command_pending) {
			waiting_command = header;
			command_pending = true;
		}
		

	}
}

mcu_header parseRequest(uint8_t data[])
{
	uint8_t tmp[gs_ul_size_buffer];
	const size_t size = gs_ul_size_buffer;
	char *buffer = membag_alloc( 12 );
	int start = 0;
	uint32_t code = 0;
	
	mcu_header m_header;
	for (int j=0; j < 3; j++) {
		int pos = 0;
		for (int i=start; i<size; i++) {
			if (data[i] == '\t' || data[i]=='\r' || data[i]=='\n') {
				buffer[pos] = 0x00;
				code = atoi(buffer);
				start = i+1;
				if (start<size && (buffer[start] =='\r' || buffer[start] =='\n')) {
					start++;	
				}
				break;
			}
			buffer[pos] = data[i];
			pos++;
		}
		switch (j) {
			case 0:
				m_header.data_type = code;
				break;
			case 1: 
				m_header.device_type = code;
				break;
			case 2:
				m_header.source_bus = code;
				break;
		}
	} 
	int len = size-start+1;
	if (len>0) 
	{
		if (data[size-2] == '\r' && data[size-1] == '\n') {
			len=len-2;
		} else if (data[size-1] == '\n') {
			len=len-1;
		} 
		
		membag_free(buffer);
		
		if (len > 0) {
			buffer = membag_alloc( len );
			strncpy(buffer,&data[start], len-1);
			buffer[len-1] = 0x00;
		} else {
			buffer = membag_alloc( 1 );
			buffer[0] = 0x00;
		}
		m_header.content_data = buffer;
	}
	return m_header;
	
}

/**
 * \brief Interrupt handler for USART. Echo the bytes received and start the
 * next receive.
 */
void USART_Handler(void)
{
	uint32_t ul_status;

	/* Read USART Status. */
	ul_status = usart_get_status(CONF_UART);

	/* Receive buffer is full. */
	if (ul_status & US_CSR_RXBUFF) {
		/* Disable timer. */
		tc_stop(TC0, 0);
		
		/* Parse data in data buffer before it is cleared. */
		mcu_header mcu_data = parseRequest(gs_puc_buffer[gs_uc_buf_num]);
		if (mcu_data.data_type == COMMAND_REQUEST || !command_pending) 
		{
			waiting_command = mcu_data;
			command_pending = true;
		} else if (mcu_data.content_data!=0) {
			membag_free(mcu_data.content_data);
			mcu_data.content_data = 0;
		}
		
		if (g_uc_transend_flag) {
			gs_ul_size_buffer = BUFFER_SIZE;
			gs_ul_size_nextbuffer = BUFFER_SIZE;
			g_uc_transend_flag = 0;
		}

		gs_uc_buf_num = MAX_BUF_NUM - gs_uc_buf_num;

		// Restart read on buffer. 
		pdca_channel_write_load(PDCA_RX_CHANNEL,
				(void *)gs_puc_buffer[gs_uc_buf_num], BUFFER_SIZE);
		pdca_channel_write_reload(PDCA_RX_CHANNEL,
				(void *)gs_puc_nextbuffer[gs_uc_buf_num], BUFFER_SIZE);

		/* Restart timer. */
		tc_start(TC0, 0);
		
		
		
	}
}

/**
 * \brief Interrupt handler for TC00. Record the number of bytes received,
 * and then restart a read transfer on the USART if the transfer was stopped.
 */

void TC00_Handler(void)
{
	
	uint32_t ul_status;
	uint32_t ul_byte_total = 0;

	//Read TC0 Status. 
	ul_status = tc_get_status(TC0, 0);

	// RC compare. 
	if ((ul_status & TC_SR_CPCS) == TC_SR_CPCS) {
		// Flush PDC buffer.
		ul_byte_total = BUFFER_SIZE -
				pdca_channel_read_load_size(PDCA_RX_CHANNEL);
		if ((ul_byte_total != 0) && (ul_byte_total != BUFFER_SIZE)) {
			// Log current size.
			g_uc_transend_flag = 1;
			if (pdca_channel_read_reload_size(PDCA_RX_CHANNEL) == 0) {
				gs_ul_size_buffer = BUFFER_SIZE;
				gs_ul_size_nextbuffer = ul_byte_total;
			} else {
				gs_ul_size_buffer = ul_byte_total;
				gs_ul_size_nextbuffer = 0;
			}

			// Trigger USART Receive Buffer Full Interrupt.
			// Restart read on buffer. 
			pdca_channel_write_reload(PDCA_RX_CHANNEL,
					(void *)gs_puc_nextbuffer[gs_uc_buf_num], 0);
			pdca_channel_write_load(PDCA_RX_CHANNEL,
					(void *)gs_puc_buffer[gs_uc_buf_num], 0);
		}
	}
}

void process_command(void)
{
	char* user_data = waiting_command.content_data;
	waiting_command.content_data = 0;
	command_pending = false;
	if (waiting_command.device_type == TEMPERATURE_HUMIDITY_SENSOR) {
		int twi = -1;
		if  (waiting_command.source_bus == BUS_TWIM0 ) twi = 0;
		else if  (waiting_command.source_bus == BUS_TWIM1 ) twi = 1;
		else if  (waiting_command.source_bus == BUS_TWIM2 ) twi = 2;
		else if  (waiting_command.source_bus == BUS_TWIM3 ) twi = 3;
		if (twi>=0) {
			delay_ms(10);
			readHumidity(twi, COMMAND_RESPONSE);
			//printf("%d\t%d\t%d\t%s\r\n", waiting_command.data_type , waiting_command.device_type, waiting_command.source_bus, waiting_command.content_data );
		}
	} else if (waiting_command.device_type == RELAY) {
		
	}
	
	if (user_data!=0) {
		membag_free(user_data);
	}
	
	
	
	
}

/**
 * \brief Configure Timer Counter 0 (TC0) to generate an interrupt every 200ms.
 * This interrupt will be used to flush USART input and echo back.
 */
static void configure_tc(void)
{
	uint32_t ul_div;
	uint32_t ul_tcclks;
	static uint32_t ul_pbaclk;

	/* Configure clock service. */
	sysclk_enable_peripheral_clock(TC0);

	/* Get system clock. */
	ul_pbaclk = sysclk_get_peripheral_bus_hz(TC0);

	/* Configure TC for a 1Hz frequency and trigger on RC compare. */
	tc_find_mck_divisor(TC_FREQ, ul_pbaclk, &ul_div, &ul_tcclks, ul_pbaclk);
	tc_init(TC0, 0, ul_tcclks | TC_CMR_CPCTRG);
	tc_write_rc(TC0, 0, (ul_pbaclk / ul_div) / TC_FREQ);

	/* Configure and enable interrupt on RC compare. */
	NVIC_EnableIRQ(TC00_IRQn);
	tc_enable_interrupt(TC0, 0, TC_IER_CPCS);
}

void configure_ast()
{
	/** AST counter mode config */
	struct ast_config ast_conf;
	ast_conf.mode = AST_COUNTER_MODE;
	ast_conf.osc_type = AST_OSC_32KHZ;
	ast_conf.psel = AST_PSEL_32KHZ_1HZ;
	ast_conf.counter = 0;
	
	/* Enable osc32 oscillator*/
	if (!osc_is_ready(OSC_ID_OSC32)) {
		osc_enable(OSC_ID_OSC32);
		osc_wait_ready(OSC_ID_OSC32);
	}

	/* Enable the AST */
	ast_enable(AST);
	
	/** set asr config  **/
	ast_set_config(AST, &ast_conf);
	
	/* First clear alarm status. */
	ast_clear_interrupt_flag(AST, AST_INTERRUPT_ALARM);

	/* Enable wakeup from alarm0. */
	ast_enable_wakeup(AST, AST_WAKEUP_ALARM);
	ast_set_callback(AST, AST_INTERRUPT_ALARM, ast_command_ready_callback ,AST_ALARM_IRQn, 1);
	
	/* Disable first interrupt for alarm0. */
	ast_disable_interrupt(AST, AST_INTERRUPT_ALARM);
	
}



/**
 * \remarks Main function.
 * @{
 */
int main (void)
{

	/* Initialize the SAM system */
	sysclk_init();
	board_init();

	/* Initialize the console USART */
	configure_console();
	
	/** Initialize the membag system. */
	membag_init();
	
	//configure_ast();
	
	/* Configure TC. */
	configure_tc();
	
	/* Enable PDCA module clock */
	pdca_enable(PDCA);
	/* Init PDCA channel with the pdca_options.*/
	pdca_channel_set_config(PDCA_RX_CHANNEL, &pdca_rx_options);
	pdca_channel_set_config(PDCA_TX_CHANNEL, &pdca_tx_options);
	/* Enable PDCA channel, start receiving data. */
	pdca_channel_enable(PDCA_RX_CHANNEL);
	pdca_channel_enable(PDCA_TX_CHANNEL);

	/* Enable USART RXBUFF interrupt */
	usart_enable_interrupt(BOARD_USART, US_IER_RXBUFF);
	/* Configure and enable interrupt of USART. */
	NVIC_EnableIRQ(USART_IRQn);

	/* Start timer. */
	tc_start(TC0, 0);

	

	/* Output example information */
	printf("-- Mokki Automation --\r\n");
	printf("-- %s\n\r", BOARD_NAME);
	printf("-- Compiled: %s %s --\n\r", __DATE__, __TIME__);
	
	initDetectionGPIO();
	initDetectionGPIO_2();
	
	HIH8000 hih_data_1;
	//AM2320 am_data;
	while (1)
	{
		//readHumidity(3, UNDEFINED);
		if (command_pending) {
			process_command();
			command_pending = false;
		}
		delay_ms(20);
	}	
}
//! @}

/* END OF FILE */
