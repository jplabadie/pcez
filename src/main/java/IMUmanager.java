
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
        enableAcc();
        enableGyr();
        System.out.println("Temp "+ readTemp());
        for(int i=0; i < 20; i++){
            int[] vars = readMagReg(0x28);
            System.out.println("Magnetometer X:"+ vars[0] + " Y:"+ vars[1] + " Z:"+ vars[2]);
            int[] avars = readAccReg(0x28);
            System.out.println("Accelerometer X:"+ avars[0] + " Y:"+ avars[1] + " Z:"+ avars[2]);
            int[] gvars = readAccReg(0x18);
            System.out.println("Gyroscope X:"+ gvars[0] + " Y:"+ gvars[1] + " Z:"+ gvars[2]);
            Thread.sleep(500);
        }
    }

    private static double readTemp(){
        double temperature = 0.0;
        byte[] buff = new byte[2];
        try {
            bgi_mag.read(0x15,buff,0,2);
            temperature = (buff[1]<<12 | buff[0]<<4) >>4;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return temperature;
    }

    private static void enableMag(){
        writeMagReg( 0x20, 0b10011100 ); // temp sensor enable, magnetometer hires 50Hz
        writeMagReg( 0x21, 0b01000000 ); // full scale to +/- 12 Gauss
        writeMagReg( 0x22, 0b00000000 ); // set magnetometer to continuous conversion mode
        writeMagReg( 0x23, 0b00000000 ); // set magnetometer to continuous conversion mode
    }

    private static void enableAcc(){
        writeAccReg( 0x1F, 0b00111000 );
        writeAccReg( 0x20, 0b00101000 );
    }

    private static void enableGyr(){
        writeAccReg(0x1E, 0b00111000);
        writeAccReg(0x10, 0b10111000);
        writeAccReg(0x13, 0b10111000);
    }

    private static void writeMagReg( int register, int value ){
        try {
            System.out.println("writing to mag register: "+ register+ " value: "+value);
           bgi_mag.write(register, (byte)value);
        } catch (IOException e) {
            System.out.println("Failed to write to Mag Register");
        }
    }

    private static void writeAccReg( int register, int value ){
        try {
            bgi_acc.write(register,(byte)value);
        } catch (IOException e) {
            System.out.println("Failed to write to Acc Register");
        }
    }

    private static int[] readMagReg( int register ){
        byte[] block = new byte[6];
        int[] vars = {0,0,0};
        try {
            bgi_mag.read( register,block,0,6 );
            vars[0] = (block[1]<<8)|block[0];
            vars[1] = (block[3]<<8)|block[2];
            vars[2] = (block[4]<<8)|block[5];
            return vars ;
        } catch (IOException e) {
            System.out.println("shucks");
            return vars;
        }
    }

    private static int[] readAccReg( int register ){
        byte[] block = new byte[6];
        int[] vars = {0,0,0};
        try {
            bgi_acc.read( register,block,0,6 );
            vars[0] = (block[1]<<8)|block[0];
            vars[1] = (block[3]<<8)|block[2];
            vars[2] = (block[4]<<8)|block[5];
            return vars ;
        } catch (IOException e) {
            System.out.println("shucks");
            return vars;
        }
    }

}
