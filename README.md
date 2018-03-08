### pcez
A non-commercial system for recording vehicle danger and incident data.

# Overview
The project is currently being developed in Java, and uses the Pi4j framework to improve interaction with the hardware http://pi4j.com/

The idea is to build a package of hardware and software that can be put into a plane or other vehicle to provide positional data collection, similar to commercial flight-data-recorder "black boxes". Future additions to the project could include voice/visual data recording, support for additional sensors, remote interface with a mobile device, trip recreation and visualization, real-time position reporting, etc.

# Current Status
After some confusion arising from incorrect datasheets, I'm successfully developing and testing on the hardware. I'm relatively new to the hardware scene, and I've never worked with positional data before, so its been a trip already. I'm currently working on integrating the magnetometer, accelerometer, and gyroscope sensor data and building filters for each to improve data quality. I also need to determine a good way to build and run tests for the hardware, as well as cleaning up the structure of the code and package. After this, I intend to spend some time with the barometer and GPS. Finally, I'll move towards data storage and retrieval, and system behavior such as power-on behavior and power management, etc.

# Hardware
Currently designed for the RaspberryPi 2 or later, paired with the BerryGPS-IMU v2. Communication with the GPS is currently being done with SPI, while communication with the IMU is done over i2c.

The GPS unit is a M20048 from Antenova with an internal antenna and an external antenna port:
https://www.mouser.com/catalog/specsheets/antenova_M20048-1.pdf
The IMU Magnetometer, Gyroscope, and Accelerometers are provided by the ST LSM9DSM1 
http://www.st.com/content/ccc/resource/technical/document/datasheet/1e/3f/2a/d6/25/eb/48/46/DM00103319.pdf/files/DM00103319.pdf/jcr:content/translations/en.DM00103319.pdf
The IMU 'Barometer' is a Bosch BMP280 Digital Pressure Sensor 
https://cdn-shop.adafruit.com/datasheets/BST-BMP280-DS001-11.pdf
https://github.com/BoschSensortec/BMP280_driver




