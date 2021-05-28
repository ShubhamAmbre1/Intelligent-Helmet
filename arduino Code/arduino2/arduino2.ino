#include <VirtualWire.h>
#include <TinyGPS++.h>
#include <SoftwareSerial.h>
#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>

#define FIREBASE_HOST "your FIREBASE HOST"
#define FIREBASE_AUTH "your secret"

const char* ssid = "";                  // Your wifi Name       
const char* password = "";          // Your wifi Password

//#define ledPin 13
const int inputPin3  = 4;   // Pin  7 of L293D IC
const int inputPin4  = 5;   // Pin  2 of L293D IC

//For gps module
int RXPin = 2;   //put tx = 2 and rx = 3
int TXPin = 3;


SoftwareSerial esp(5, 6);  // Rx Tx

int GPSBaud = 9600;
 
// Create a TinyGPS++ object
TinyGPSPlus gps;

// Create a software serial port called "gpsSerial"
SoftwareSerial gpsSerial(RXPin, TXPin);

void setup() {
  Serial.begin(9600); 
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);

 // Start the software serial port at the GPS's default baud
  gpsSerial.begin(GPSBaud);
  
    vw_set_rx_pin(11);
    vw_setup(2000);
//    pinMode(ledPin, OUTPUT);
    vw_rx_start();
//
    pinMode(inputPin3, OUTPUT);
    pinMode(inputPin4, OUTPUT);
    while (gpsSerial.available() > 0)
    if (gps.encode(gpsSerial.read()))
      displayInfo();

  Serial.begin(115200);
  WiFi.mode(WIFI_OFF);        //Prevents reconnection issue (taking too long to connect)
  delay(1000);
  WiFi.mode(WIFI_STA);        //This line hides the viewing of ESP as wifi hotspot
  
  WiFi.begin(ssid, password);     //Connect to your WiFi router
  Serial.println("");

  Serial.print("Connecting");
  // Wait for connection
  while (WiFi.status() != WL_CONNECTED) {
    delay(250);
    Serial.print(".");
    delay(250);
    }

  //If connection successful show IP address in serial monitor
  Serial.println("");
  Serial.println("Connected to Network/SSID");
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());  //IP address assigned to your ESP
}

void displayInfo()
{
  if (gps.location.isValid())
  {
    Serial.print("Latitude: ");
    Serial.println(gps.location.lat(), 6);
    Serial.print("Longitude: ");
    Serial.println(gps.location.lng(), 6);
    Serial.print("Altitude: ");
    Serial.println(gps.altitude.meters());
  }
  else
  {
    Serial.println("Location: Not Available");
  }
  
  Serial.print("Date: ");
  if (gps.date.isValid())
  {
    Serial.print(gps.date.month());
    Serial.print("/");
    Serial.print(gps.date.day());
    Serial.print("/");
    Serial.println(gps.date.year());
  }
  else
  {
    Serial.println("Not Available");
  }

  Serial.print("Time: ");
  if (gps.time.isValid())
  {
    if (gps.time.hour() < 10) Serial.print(F("0"));
    Serial.print(gps.time.hour());
    Serial.print(":");
    if (gps.time.minute() < 10) Serial.print(F("0"));
    Serial.print(gps.time.minute());
    Serial.print(":");
    if (gps.time.second() < 10) Serial.print(F("0"));
    Serial.print(gps.time.second());
    Serial.print(".");
    if (gps.time.centisecond() < 10) Serial.print(F("0"));
    Serial.println(gps.time.centisecond());
  }
  else
  {
    Serial.println("Not Available");
  }

  Serial.println();
  Serial.println();
  delay(1000);
    
}

void sendGPSLoc(){
  Firebase.setFloat("Shubham/gps/latitude", gps.location.lat());
  Firebase.setFloat("Shubham/gps/longitude", gps.location.lng());
}

void lastWorn(){
  Firebase.setString(gps.date.day() + "/" + gps.date.month() + "/" + gps.date.year());
  Firebase.setBool("Shubham/mq3/accident", false);
  Firebase.setBool("Shubham/mpu/accident", false);
}

void alcoholDetected(){
  Firebase.setBool("Shubham/mq3/accident", true);
  Firebase.setString("Shubham/mq3/detected", gps.date.day() + "/" + gps.date.month() + "/" + gps.date.year() + " " + gps.time.hour() + ":" +gps.time.min());

}

void accident(){
  Firebase.setBool("Shubham/mpu/accident", true);
  Firebase.setString("Shubham/mpu/detected", gps.date.day() + "/" + gps.date.month() + "/" + gps.date.year() + " " + gps.time.hour() + ":" +gps.time.min());
}
void loop() {
    uint8_t buf[VW_MAX_MESSAGE_LEN];
    uint8_t buflen = VW_MAX_MESSAGE_LEN; 
    
    if (vw_get_message(buf, &buflen))
    {
      if(buf[0]=='a')
      {  
       Serial.println("Helmet is on");
       lastWorn();
       Firebase.setString("Shubham/status", "On");
       digitalWrite(inputPin3, HIGH);
       digitalWrite(inputPin4, LOW);
      }  
      else if(buf[0]== 'b'){
        Serial.println("Helmet Removed");
        Firebase.setString("Shubham/status", "Off");
        digitalWrite(inputPin3, LOW);
        digitalWrite(inputPin4, LOW);
      }
      else if(buf[0]== 'c'){
        Serial.println("Alcohol Detected");
        alcoholDetected();
        digitalWrite(inputPin3, LOW);
        digitalWrite(inputPin4, LOW);
      }
      else if(buf[0]== 'd'){
        Serial.println("Accident Detected");
        accident();
        digitalWrite(inputPin3, LOW);
        digitalWrite(inputPin4, LOW);
       
      }
     
    }

    // This sketch displays information every time a new sentence is correctly encoded.
  while (gpsSerial.available() > 0)
    if (gps.encode(gpsSerial.read()))
      displayInfo();

  sendGPSLoc()
  // If 5000 milliseconds pass and there are no characters coming in
  // over the software serial port, show a "No GPS detected" error
  if (millis() > 5000 && gps.charsProcessed() < 10)
  {
    Serial.println("No GPS detected");
    while(true);
  }
}
