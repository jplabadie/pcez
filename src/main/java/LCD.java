import com.pi4j.io.gpio.*;

/**
 * @author Jean-Paul Labadie
 */
public class LCD {

    public void initLCD(){

        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();

        // provision gpio pin #01 as an output pin and turn on
        final GpioPinDigitalOutput pin31_5 = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_06, "in5", PinState.LOW);
        final GpioPinDigitalOutput pin33_4 = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_13, "in4", PinState.LOW);
        final GpioPinDigitalOutput pin35_3 = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_19, "in3", PinState.LOW);
        final GpioPinDigitalOutput pin37_2 = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_26, "in2", PinState.LOW);

        final GpioPinDigitalOutput pin32_rs = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_12, "rs", PinState.LOW);
        final GpioPinDigitalOutput pin36_en = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_16, "en", PinState.LOW);

        // set shutdown state for this pin
        // pin.setShutdownOptions(true, PinState.LOW);
        pin31_5.setShutdownOptions(true, PinState.LOW);
        pin33_4.setShutdownOptions(true, PinState.LOW);
        pin35_3.setShutdownOptions(true, PinState.LOW);
        pin37_2.setShutdownOptions(true, PinState.LOW);
        pin32_rs.setShutdownOptions(true, PinState.LOW);
        pin36_en.setShutdownOptions(true, PinState.LOW);

        pin32_rs.setState(PinState.LOW);
        pin31_5.setState(PinState.LOW);
        pin33_4.low();
        pin35_3.high();
        pin37_2.low();
        pin36_en.high();
        pin36_en.low();

        pin32_rs.setState(PinState.LOW);
        pin31_5.low();
        pin33_4.low();
        pin35_3.high();
        pin37_2.low();
        pin36_en.high();
        pin36_en.low();

        pin32_rs.setState(PinState.LOW);
        pin31_5.high();
        pin33_4.low();
        pin35_3.low();
        pin37_2.low();
        pin36_en.high();
        pin36_en.low();

        pin32_rs.setState(PinState.LOW);
        pin31_5.low();
        pin33_4.low();
        pin35_3.low();
        pin37_2.low();
        pin36_en.high();
        pin36_en.low();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // stop all GPIO activity/threads by shutting down the GPIO controller
        // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
        gpio.shutdown();

    }
}
