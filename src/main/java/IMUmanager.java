
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

import java.io.IOException;

/**
 * @author Jean-Paul Labadie
 */
public class IMUmanager {

    private static I2CBus bus ;
    private static I2CDevice bgi_mag;
    private static I2CDevice bgi_acc;
    private static I2CDevice bgi_bar;

    public static void main(String[] args) throws InterruptedException, IOException, I2CFactory.UnsupportedBusNumberException {

        bus = I2CFactory.getInstance(I2CBus.BUS_1);
        bgi_mag = bus.getDevice(0x1c);
        bgi_acc = bus.getDevice(0x6a);
        bgi_bar = bus.getDevice(0x77);

        enableMag();
        System.out.println("Status "+ bgi_mag.read(0x24));
        System.out.println("Status "+ bgi_mag.read(0x25));
        System.out.println("Status "+ bgi_mag.read(0x26));
        for(int i=0; i < 10; i++){

            Thread.sleep(1000);
        }
    }

    private static double readTemp(){
        double temp = 0.0;
        try {
            temp = bgi_mag.read( 0x06 );
            bgi_mag.read( 0x05);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temp;
    }

    private static void enableMag(){
        writeMagReg( 0x24, (byte) 0b11110000); // temp sensor enable, magnetometer hires 50Hz
        writeMagReg( 0x25, (byte) 0b01100000); // full scale to +/- 12 Gauss
        writeMagReg( 0x26, (byte) 0b00000000 ); // set magnetometer to continuous conversion mode
    }

    private static void enableAcc(){
        writeAccReg((byte) 0x24,(byte) 0b01100111);
        writeAccReg( (byte)0x25,(byte) 0b00100000);
    }

    private static void writeMagReg( int register, byte value ){
        try {
           bgi_mag.write(register, value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeAccReg( byte register, byte value ){
        try {
            bgi_acc.write(register,value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] readMagReg( int register ){
        byte[] block = new byte[6];
        try {
            bgi_mag.read( block,6,register );
            return block;
        } catch (IOException e) {
            System.out.println("shucks");
            return block;
        }
    }
}
