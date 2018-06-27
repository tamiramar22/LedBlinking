
package ledblink;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;



public class LedBlink {

    public static void main(String[] args) {
        try {
            final GpioController gpio = GpioFactory.getInstance();
            final GpioPinDigitalOutput ledPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00);
        
            ledPin.blink(500, 15000);
        }
    
    catch (Exception e) {
     e.printStackTrace();   
    }
    }
}
    

 
