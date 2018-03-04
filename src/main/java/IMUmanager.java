package java;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.impl.I2CDeviceImpl;

import java.io.IOException;
import java.lang.System;

/**
 * @author Jean-Paul Labadie
 */
public class IMUmanager {

    public static void main(String[] args) throws InterruptedException, IOException, I2CFactory.UnsupportedBusNumberException {

        private static I2CBus bus = I2CFactory.getInstance(I2CBus.Bus_1);
        private static I2CDevice bgi_mag = bus.getDevice(0x1c);
        private static I2CDevice bgi_acc = bus.getDevice(0x6a);

        enableMag();
        for(int i=0; i < 20; i++){
            int a = readMagReg( 0x08 );
            System.out.println( "Magnetometer XLM = " + a);
            Thread.sleep(1000);
        }
    }

    private static void enableMag(){
        writeMagReg( 0x24, 0b11110000); // temp sensor enable, magnetometer hires 50Hz
        writeMagReg( 0x25, 0b01100000); // full scale to +/- 12 Gauss
        writeMagReg( 0x26, 0b00000000); // set magnetometer to continuous conversion mode
    }

    private static void enableAcc(){
        writeAccReg( 0x24, 0b01100111);
        writeAccReg( 0x25, 0b00100000);
    }

    private static void writeMagReg( byte register, byte value ){
        int result = big_mag.write(register,value);
    }

    private static void writeAccReg( byte register, byte value ){
        int result = bgi_acc.write(register,value);
    }

    private static int readMagReg( int register ){
        bgi_mag.read( register );
    }
}
