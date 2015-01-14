#include <AccelStepper.h>


// stepper

AccelStepper* stepper0 = new AccelStepper(1, 23, 22);
AccelStepper* stepper1 = new AccelStepper(1, 25, 24);
AccelStepper* stepper2 = new AccelStepper(1, 27, 26);
AccelStepper* stepper3 = new AccelStepper(1, 29, 28);
AccelStepper* stepper4 = new AccelStepper(1, 31, 30);
AccelStepper* motors[5] = {stepper0, stepper1, stepper2, stepper3, stepper4};
int motorCount = 5;
int oneRotation = 1600;

// communication
String message;
int msg = 0;
int pos = 0;

void setup(){  

  initStepper();

  Serial.begin(9600);
  
  // TODO
  // request positions of smartphone
  // at the moment all on position 0 at startup
  // stepper1.setCurrentPosition(...)
}



void loop() {
  //Serial.println("loop");
  
  while (Serial.available() > 0) {
      char msg = Serial.read();
      
      //Serial.print("all: ");
      //Serial.println(msg);

      if (msg == '*') {
        message += "\n";
        handle(message);
        // reset Message
        message = "";
      } else {
        message += msg  ;
        //Serial.print("r: ");
        //Serial.println(msg);
      } 
      
      
  }
  
  // check that motor never reach position -1
  for (int i = 0; i < motorCount; i++) {
    if(motors[i]->currentPosition() >= 0)  {
      motors[i]->run();
    }
  }
  


  
  
}


void handle(String message) {

  //Serial.print("handle message: ");
  //Serial.println(message);
  
  if (message.startsWith("sa;"))
    handleStepperPos(message.substring(3), true);
  else if (message.startsWith("sr;"))
    handleStepperPos(message.substring(3), false);
  else if (message.startsWith("fs;"))
    handleStepperForceStop(message.substring(3));
  else if (message.startsWith("l;")) 
    handleLEDMsg(message.substring(2));
  
}

/**
*  move stepper # to absolute position x
**/
void handleStepperPos(String message, boolean aboslutePosition) {
  /*Message: s;  motor#;  +-Rotations*/
  /*Types  : s;  int   ;  float*/
  
  int stepper = -1;
  float rotations = -1;
  
  int counter = 0;
  String tmp = "";
  for (int i = 0; i < message.length(); i++) {
    if(message.charAt(i) != ';') {
      tmp += message.charAt(i);
    } else {
      if (counter == 0) {
        // parse stepper #
        stepper = tmp.toInt();
      } else if (counter == 1) {
        // parse rotation
        char buffer[10];
        tmp.toCharArray(buffer, 10);
        rotations = atof(buffer);
      }
      tmp = "";
      counter++;
    }
  }
  
  // chekc stepper 
  if (stepper >= 0) {

    long r = rotations * oneRotation;
    // if motor is not running
    if (motors[stepper]->distanceToGo() == 0) {
      if (aboslutePosition) {
        // move to aboslute position
        motors[stepper]->moveTo(r);
      } else {
        // move to relative position
        motors[stepper]->move(r);
      }
    }
  /*
  Serial.print("converted ");
  Serial.print(stepper);
  Serial.print(" to ");
  Serial.println(r);
  */
  }
}


void handleStepperForceStop(String message) {
  int stepper = -1;
  
  String tmp;
  for (int i = 0; i < message.length(); i++) {
    if(message.charAt(i) != ';') {
      tmp += message.charAt(i);
    } else {
      stepper = tmp.toInt();
    }
   
   if (stepper >= 0) {
     if (motors[stepper]->distanceToGo() > 0)
       motors[stepper]->stop();
   } 
  }

}



void handleLEDMsg(String message) {
  Serial.println("led message");
}






void initStepper() {
  // init all steppers
  for (int i=0; i < sizeof(motors); i++) {
      motors[i]->setMaxSpeed(2000);
      motors[i]->setAcceleration(1000);
  } 
  
  
  
  
  

}

