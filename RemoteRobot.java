
package remoterobot;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.i2c.I2CFactory;
import i2c.VL53L0X;
import remoterobot.drivers.Motor;

public class RemoteRobot extends Thread {
    
    public enum MoveDirections {
        STOP,
        ROTATE_CW,
        ROTATE_CCW
    }

    public enum MovementState {
        STOPPED,
        MOVING
    }
    
    public enum DistanceSensorsType {
        SHAFT_LASER;
    }
    
    public static void main(String[] args) throws I2CFactory.UnsupportedBusNumberException, Exception{
        //motor
        RemoteMotor.MovementState state = RemoteMotor.MovementState.MOVING;
        RemoteMotor.MoveDirections moveDirection = RemoteMotor.MoveDirections.ROTATE_CW;
        Double velocity = 18.0;
        //boolean complete = false;
        boolean smooth = false;
        Double speed = null;
        Double distance = 500.0;
             
        GpioController gpio = GpioFactory.getInstance();
        GpioPinDigitalOutput enable = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "Enable", PinState.LOW);
        Motor motor = new Motor(RaspiPin.GPIO_00, RaspiPin.GPIO_02);
        
        switch (moveDirection) {
            case ROTATE_CW:
                velocity = velocity*0.8;
                speed = 1.0;
                break;
            case ROTATE_CCW:
                velocity = velocity*0.8;
                speed = -1.0;
                break;
        }
        
        motor.SetDirection(speed > 0);
        
        if (Math.abs(speed) > 0.0000001) {
            motor.move(Math.abs(speed), velocity, distance, smooth,new Motor.MotorAdvCallback() {
            @Override
                    public boolean stop() {
                        return false;
                    }
            });
        }
        
        //sensor
        VL53L0X vl53l0x = new VL53L0X();
        GpioPinDigitalOutput sensor1Pin;
        
        sensor1Pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_12, "Shaft", PinState.LOW);
        sensor1Pin.setState(true);
        while(true){
            Thread.sleep(20);
            vl53l0x.startRanging(VL53L0X.VL53L0X_BEST_ACCURACY_MODE);
            Double laserDistance = 1.0 * vl53l0x.getDistance();
            vl53l0x.stopRanging();
   
            if (laserDistance > 30 && laserDistance < 2000) {
                    laserDistance = laserDistance / 10;
                } else {
                    laserDistance = null;
                }
            System.out.print(laserDistance);
            
        }
    }
}
