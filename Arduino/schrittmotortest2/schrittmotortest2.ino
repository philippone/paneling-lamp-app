#include <AccelStepper.h>

// Define a stepper and the pins it will use
AccelStepper stepper(1, 9, 8);


int target = 1600 * 20;

int pos = target;

void setup()
{  
  stepper.setMaxSpeed(60000000);
  //stepper.setSpeed(60000000);
  stepper.setAcceleration(10000000);
}

void loop()
{
  if (stepper.distanceToGo() == 0)
  {
    if (pos == target) {
      delay(1000);
      pos = -target;
    }
    else 
      pos = target;
    delay(1500);
    //pos = -pos;
    stepper.moveTo(pos);
  }
  stepper.run();
}
