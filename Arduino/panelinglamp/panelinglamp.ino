#include <Servo.h> 
#include "LampServo.h"


boolean lampOn = true;

LampServo s1(10, 1000);
LampServo s2(11, 1000);
LampServo s3(9,  1000);
LampServo s4(6,  1000);
LampServo s5(5,  1000);

// must be PWM Pin
int ledPin1 = 3;
int ledPin1_val = 255; // 0 - 255 == off - on


// emergency Pins (Analog In; 0 - 5 volts mapped to 0 - 1023)
int emergPin1 = 1;
int emergPin2 = 2;
int emergPin3 = 3;
int emergPin4 = 4;
int emergPin5 = 5;
int emergThreshold = 100;


// communication
int msg;
int flag = 0;


void setup() {
  
  //reserve pins for led control (PWM)
  pinMode(ledPin1, OUTPUT);
  // TODO more led pins
  
  // attach servos
  s1.attach();
  // TODO attach all servos
  
  
  
  Serial.begin(9600);
  
  // TODO send current pos to phone or vince versa?
}



void loop() {
  
  checkEmergencyPositions();
  
  // Serial communication 
  if (Serial.available() > 0){     
      msg = Serial.read();   
      flag=0;
      
      handle(msg);
  }
  
  // light control
  if (lampOn) {
    analogWrite(ledPin1, ledPin1_val);
    //...
  } else {
    analogWrite(ledPin1, 0);
    //...
  }
   
}

/*
* handle communication input
*/
void handle(int msg) {

}

/*
* check end position
*/
void checkEmergencyPositions() {
  if (analogRead(emergPin1) > emergThreshold) s1.emergencyStop();
  if (analogRead(emergPin2) > emergThreshold) s2.emergencyStop();
  if (analogRead(emergPin3) > emergThreshold) s3.emergencyStop();
  if (analogRead(emergPin4) > emergThreshold) s4.emergencyStop();
  if (analogRead(emergPin5) > emergThreshold) s5.emergencyStop();

}  
 


