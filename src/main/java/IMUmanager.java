
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * @author Jean-Paul Labadie
 */
public class IMUmanager {

    private static I2CBus bus ;
    private static I2CDevice bgi_mag; // magnetometer
    private static I2CDevice bgi_acc; // accelerometer and gyroscope
    private static I2CDevice bgi_bar; // barometer

    private static final int MAG = 0x28; // magnetometer OUT is 0x28 - 0x2D
    private static final int ACC = 0x28; // accelerometer OUT is 0x28 - 0x2D
    private static final int GYR = 0x18; // gyroscope OUT is 0x18 - 0x1D

    private static final double G_GAIN = 0.07; // gyroscope noise level
    private static double G_DT = 0.0254; // gyroscope sampling window (initially 250ms)

    private static double gxa = 0.0;
    private static double gya = 0.0;
    private static double gza = 0.0;

    private static double axa = 0.0;
    private static double aya = 0.0;
    private static double aza = 0.0;

    private static double filt_x = 0.0;
    private static double filt_y = 0.0;

    private static double heading = 0.0;
    private static int MAG_MED_TABLE_SIZE = 9;
    private static double MAG_LPF_FACTOR = 0.4;

    private static double[] old_mag_values = new double[3];

    public static void main(String[] args) throws InterruptedException, IOException, I2CFactory.UnsupportedBusNumberException {
        
        LCD lcd = new LCD();
        lcd.initLCD();

        bus = I2CFactory.getInstance(I2CBus.BUS_1); //IMU sits on bus i2c-1
        bgi_mag = bus.getDevice(0x1C); // magnetometer address is 0x1c
        bgi_acc = bus.getDevice(0x6A); // accelerometer and gyroscope address is 0x6a
        bgi_bar = bus.getDevice(0x77); // barometer address is 0x77

        initIMU();
        DecimalFormat df = new DecimalFormat("000.00");

        System.out.println("Temp "+ readTemp());
        for(int i=0; i < 1000; i++){
            System.out.println("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b" +
                    "\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b");
            double start = System.nanoTime();


//            updateGyroDPS();
//            updateAccDPS();
//            updateFilteredXY();
//
//            System.out.print("Gyr X:" + df.format(filt_x) + " Y:" +  df.format(filt_y) + " Z:" +  df.format(gza));
            updateHeading();
            System.out.println( "Heading: " + df.format(heading) );

            Thread.sleep(250);
            double stop = System.nanoTime();
            //G_DT=(stop-start)/1000000);

        }
    }

    private static void updateHeading(){
        int[] mag = readMagReg(MAG);
        double mag_x = (double) mag[0] * MAG_LPF_FACTOR + old_mag_values[0] * (1-MAG_LPF_FACTOR);
        double mag_y = (double) mag[1] * MAG_LPF_FACTOR + old_mag_values[0] * (1-MAG_LPF_FACTOR);
        double mag_z = (double) mag[2] * MAG_LPF_FACTOR + old_mag_values[0] * (1-MAG_LPF_FACTOR);

        old_mag_values[0] = mag_x;
        old_mag_values[1] = mag_y;
        old_mag_values[2] = mag_z;

        heading = 180 * Math.atan2(mag_y,mag_x) / Math.PI;
        if( heading < 0 )
            heading += 360;

    }

    private static void updateFilteredXY(){
        filt_x = 0.97*(filt_x + gxa * G_DT)+(0.03)*axa;
        filt_y = 0.97*(filt_y + gya * G_DT)+(0.03)*aya;
    }

    private static void updateGyroDPS() {
        int[] gyro = readAccGyroReg(GYR);
        double rgx = (double)gyro[0] * G_GAIN;
        double rgy = (double)gyro[1] * G_GAIN;
        double rgz = (double)gyro[2] * G_GAIN;

        gxa+=rgx*G_DT;
        gya+=rgy*G_DT;
        gza+=rgz*G_DT;
    }

    private static void updateAccDPS(){
        int[] accel = readAccGyroReg(ACC);
        axa = (Math.atan2((double)accel[1],(double)accel[2])+ Math.PI) * 57.29578;
        aya = (Math.atan2((double)accel[2],(double)accel[0])+ Math.PI) * 57.29578;
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

    private static void initIMU(){
        enableMag();
        enableGyr();
        enableAcc();
    }

    private static void enableMag(){
        writeMagReg( 0x20, 0b10011100 ); // temp sensor enable, low power, 80Hz ODR
        writeMagReg( 0x21, 0b01000000 ); // full scale to +/- 12 Gauss
        writeMagReg( 0x22, 0b00000000 ); // set magnetometer to continuous update mode
        writeMagReg( 0x23, 0b00000000 ); // set magnetometer z-axis to low-power mode
    }

    private static void enableAcc(){
        writeAccReg( 0x1F, 0b00111000 ); // enable x,y,z axes on accelerometers
        writeAccReg( 0x20, 0b00101000 ); // +/- 16g mode
    }

    private static void enableGyr(){
        writeAccReg(0x1E, 0b00111000); // enable x,y,z axes on gyro
        writeAccReg(0x10, 0b10111000); // set gyro ODR to 47Hz @ 2000dps
        writeAccReg(0x13, 0b10111000); // swap gyroscope orientation
    }

    private static void writeMagReg( int register, int value ){
        try {
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
            vars[0] = block[0] | block[1] << 8;
            vars[1] = block[2] | block[3] << 8;
            vars[2] = block[4] | block[5] << 8;
            return vars ;
        } catch (IOException e) {
            System.out.println("shucks");
            return vars;
        }
    }

    private static int[] readAccGyroReg(int register){
        byte[] block = new byte[6];
        int[] vars = {0,0,0};
        try {
            bgi_acc.read( register,block,0,6 );
            vars[0] = block[0] | block[1] << 8;
            vars[1] = block[2] | block[3] << 8;
            vars[2] = block[4] | block[5] << 8;
            return vars ;
        } catch (IOException e) {
            System.out.println("shucks");
            return vars;
        }
    }

}
