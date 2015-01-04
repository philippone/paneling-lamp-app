#ifndef LampServo_h
#define LampServo_h

#include <Servo.h> 
#include <Arduino.h>


class LampServo {

  
  private:
  Servo servo;
  int pin;
  boolean attached;
  float position;
  long timePerRotation;
  
  boolean emergStop;
  float maxDownPosition; // TODO set!  
  
  public:
  LampServo(int pin, long timePerRotation);
  Servo getServo();
  float getPosition();
  void setPin(int p);
  void attach();
  void detach();
  void driveUP(float rotations);
  void driveDOWN(float rotations);
  void emergencyStop();
  
};

#endif
