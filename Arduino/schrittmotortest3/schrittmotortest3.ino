#include <AccelStepper.h>

// Define a stepper and the pins it will use
AccelStepper stepper(1, 9, 8);

int pos = 0;
int msg;
int flag = 0;

int runMode = 0;

void setup()
{  
  stepper.setMaxSpeed(800);
  stepper.setAcceleration(800);

  
  Serial.begin(9600);
}1

void loop()
{
  
  // Serial communication 
  if (Serial.available() > 0){     
      msg = Serial.parseInt();   
      flag=0;
      
      pos = msg;
     
      Serial.print("current pos ");
      Serial.println(stepper.currentPosition());
      
      stepper.move(pos *1600);
      
      Serial.print("move to pos ");
      Serial.println(stepper.currentPosition() + pos * 1600);
      
      stepper.run();
  }
  
    stepper.run();
  
  
}


void handle(int msg) {

  Serial.print("goto ");
  Serial.println(msg);

}



