#include <AccelStepper.h>

// Define a stepper and the pins it will use
AccelStepper stepper(1, 9, 8);

int pos = 0;
int msg;
int flag = 0;

int runMode = 0;

void setup()
{  
  stepper.setMaxSpeed(60000000);
  stepper.setAcceleration(120000000);
  stepper.setSpeed(60000000);

  
  Serial.begin(9600);
}

void loop()
{
  
  // Serial communication 
  if (Serial.available() > 0){     
      msg = Serial.parseInt();   
      flag=0;
      
      pos = msg;
      
      if (msg >= 0) {
        runMode = 0;
        //stepper.setAcceleration(5000);
      }
      if (msg == -1) {
        stepper.setAcceleration(0);
        runMode = 1;
        
      }
      if (msg == -2) {
        stepper.setAcceleration(0);
        runMode = 2;
       
      }
      handle(msg);
  }
  
  if (runMode == 0) {
    if (stepper.distanceToGo() == 0)
    {
      Serial.print("current pos ");
      Serial.println(stepper.currentPosition());
      stepper.moveTo(pos);
    }
    stepper.run();
  }
  
  if (runMode == 1) {
    stepper.runSpeed();
  }
  
  if (runMode == 2) {
     stepper.runSpeed();
  }
}


void handle(int msg) {

  Serial.print("goto ");
  Serial.println(msg);

}



