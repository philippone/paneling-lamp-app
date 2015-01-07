#include <AccelStepper.h>

// Define a stepper and the pins it will use
AccelStepper stepper(1, 9, 8);

int pos = 1600;

void setup()
{  
  stepper.setMaxSpeed(60000000);
  stepper.setAcceleration(120000000);
}

void loop()
{
  if (stepper.distanceToGo() == 0)
  {
    if (pos == 1600)
      pos = 0;
    else 
      pos = 1600;
    delay(500);
    //pos = -pos;
    stepper.moveTo(pos);
  }
  stepper.run();
}
