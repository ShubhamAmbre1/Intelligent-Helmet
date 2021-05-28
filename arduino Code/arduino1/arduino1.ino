#include <VirtualWire.h>
#include <Wire.h>    // FOR MPU6050
#include <MPU6050.h>  
// Touch Sensor i/o = d2
// MQ3 A0
// Transmitter D12
// MPU sda second last 
// MPU scl last

MPU6050 mpu;

int const buzzer = 8;
char *data;
char *data1;
char *data2;
char *data3;
const int AOUTpin = 0;        //the AOUT pin of the alcohol sensor goes into analog pin A0 of the arduino
int value; //for MQ3
bool check = false;

void setup() {
  Serial.begin(9600);
  pinMode(buzzer, OUTPUT);
  while(!mpu.begin(MPU6050_SCALE_2000DPS, MPU6050_RANGE_2G))
  {
    Serial.println("Could not find a valid MPU6050 sensor, check wiring!");
    delay(500);
  }

  // If you want, you can set accelerometer offsets
  // mpu.setAccelOffsetX();
  // mpu.setAccelOffsetY();
  // mpu.setAccelOffsetZ();
  
  checkSettings();// MPU Function

  //Touch sensor
  pinMode(2, INPUT);

  //Transmitter
  vw_set_tx_pin(12);  //tx pin
  vw_setup(2000);     //sets the baud rate
}

void checkSettings()
{
  Serial.println();
  
  Serial.print(" * Sleep Mode:            ");
  Serial.println(mpu.getSleepEnabled() ? "Enabled" : "Disabled");
  
  Serial.print(" * Clock Source:          ");
  switch(mpu.getClockSource())
  {
    case MPU6050_CLOCK_KEEP_RESET:     Serial.println("Stops the clock and keeps the timing generator in reset"); break;
    case MPU6050_CLOCK_EXTERNAL_19MHZ: Serial.println("PLL with external 19.2MHz reference"); break;
    case MPU6050_CLOCK_EXTERNAL_32KHZ: Serial.println("PLL with external 32.768kHz reference"); break;
    case MPU6050_CLOCK_PLL_ZGYRO:      Serial.println("PLL with Z axis gyroscope reference"); break;
    case MPU6050_CLOCK_PLL_YGYRO:      Serial.println("PLL with Y axis gyroscope reference"); break;
    case MPU6050_CLOCK_PLL_XGYRO:      Serial.println("PLL with X axis gyroscope reference"); break;
    case MPU6050_CLOCK_INTERNAL_8MHZ:  Serial.println("Internal 8MHz oscillator"); break;
  }
  
  Serial.print(" * Accelerometer:         ");
  switch(mpu.getRange())
  {
    case MPU6050_RANGE_16G:            Serial.println("+/- 16 g"); break;
    case MPU6050_RANGE_8G:             Serial.println("+/- 8 g"); break;
    case MPU6050_RANGE_4G:             Serial.println("+/- 4 g"); break;
    case MPU6050_RANGE_2G:             Serial.println("+/- 2 g"); break;
  }  

  Serial.print(" * Accelerometer offsets: ");
  Serial.print(mpu.getAccelOffsetX());
  Serial.print(" / ");
  Serial.print(mpu.getAccelOffsetY());
  Serial.print(" / ");
  Serial.println(mpu.getAccelOffsetZ());
  
  Serial.println();
}



void loop()
{

  Vector rawAccel = mpu.readRawAccel();
  Vector normAccel = mpu.readNormalizeAccel();

  Serial.print(" Xnorm = ");
  Serial.println(normAccel.XAxis);
  
  //MQ3 and touch sensor
  
  value= analogRead(AOUTpin);       //reads the analaog value from the alcohol sensor's AOUT pin
  Serial.print("Alcohol value: ");
  Serial.println(value);            //prints the alcohol value

  //Touch Sensor

  
  if (digitalRead(2) == HIGH && value <= 525  &&  normAccel.XAxis > 5){  // Checking touch and alcohol value
    //Serial.println("Sensor is touched");
    digitalWrite(buzzer, LOW);
    data="a";
    if (check == false){
      vw_send((uint8_t *)data, strlen(data));   // Data sent to rx
      vw_wait_tx();
      check = true;
      Serial.println("Everything working fine");
    }
  }
  else if(digitalRead(2) == LOW || value > 525 || normAccel.XAxis < 5){
    digitalWrite(buzzer, HIGH);
    if (digitalRead(2) == LOW) {
      Serial.println("Helmet Removed");
      data1="b";
      check = false;
      vw_send((uint8_t *)data1, strlen(data1));   // Data sent to rx
      vw_wait_tx();
    }
    if(value > 525) {
      Serial.println("Alcohol Detected");
      data2="c";
      check = false;
      vw_send((uint8_t *)data2, strlen(data2));   // Data sent to rx
      vw_wait_tx();
    }
    if(normAccel.XAxis < 5){
      Serial.println("Accident Detected");
      data3="d";
      check = false;
      vw_send((uint8_t *)data3, strlen(data3));   // Data sent to rx
      vw_wait_tx();
    }
  }


}
