#include <Servo.h> 
#include <Arduino.h">
#include "LampServo.h"



LampServo::LampServo(int p, long tpR) {
  pin = p;
  timePerRotation = tpR;
  emergStop = false;
}


Servo LampServo::getServo() {
  return servo;
}

float LampServo::getPosition(){
}
  
void LampServo::setPin(int p) {
  pin = p;
}

void LampServo::attach() {
  if (attached)
      return;      

  attached = true;
  servo.attach(pin);
}

void LampServo::detach() {
  if (!attached)
    return;
  
  attached = false;
  servo.detach();

}
  
  
void LampServo::driveUP(float rotations){
  unsigned long startTime;
  
  startTime = millis();
  while (millis() - startTime < rotations * timePerRotation || emergStop) {
    // rotate
    servo.write(0);
  }
  // stop
  servo.write(90);
  //emergStop = false;
  
  position -= rotations;
}


void LampServo::driveDOWN(float rotations){
  unsigned long startTime;
  
  // TODO check max Down Position
  
  startTime = millis();
  while (millis() - startTime < rotations * timePerRotation) {
    // rotate
    servo.write(180);
  }
  // stop
  servo.write(90);
  if (emergStop)
    position = 0;
  emergStop = false;
  
  position += rotations;
}



void LampServo::emergencyStop() {
  emergStop = true;
}
