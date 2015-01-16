#include <AccelStepper.h>
#include <SoftwareSerial.h>

// stepper

AccelStepper* stepper0 = new AccelStepper(1, 23, 22);
AccelStepper* stepper1 = new AccelStepper(1, 25, 24);
AccelStepper* stepper2 = new AccelStepper(1, 27, 26);
AccelStepper* stepper3 = new AccelStepper(1, 29, 28);
AccelStepper* stepper4 = new AccelStepper(1, 31, 30);
AccelStepper* motors[5] = {stepper0, stepper1, stepper2, stepper3, stepper4};
boolean motorRunning[5] = {false, false, false, false, false};
int motorCount = 5;
float oneRotation = 1600;
// min/max Position in Rotations (x * oneRotation)
int motorMinPos = 0 * oneRotation;
int motorMaxPos = 100 * oneRotation;

//LED stuff
int led[4] = {13, 14, 15, 16};


// communication
//SoftwareSerial mySerial(15, 14); // RX, TX

String message;
int msg = 0;
int pos = 0;

void setup(){  

  initStepper();
  initLEDs();

  // Open serial communications and wait for port to open:
  Serial.begin(9600);
  Serial1.begin(9600);
  
  
  
  // TODO
  // request positions of smartphone
  // at the moment all on position 0 at startup
  // stepper1.setCurrentPosition(...)
}



void loop() {

  // listen to bluetooth
  while (Serial1.available() > 0) {
      char msg = Serial1.read();
      
     if (msg == '*' || msg == '\n') {
        message += "\n";
        handle(message);
        
        // send back to phone (test only)
        //Serial1.print("all: ");
        //Serial1.println(message);
        // send to usb 
        Serial.print("inc: ");
        Serial.print(message);
        
        // reset Message
        message = "";
      } else {
        message += msg  ;
        //Serial.print("r: ");
        //Serial.println(msg);
      } 
  }
  
  // normal usb connection
  while (Serial.available() > 0) {
      char msg = Serial.read();
      
      //Serial.print("all: ");
      //Serial.println(msg);

      if (msg == '*' || msg == '\n') {
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
  
  for (int i = 0; i < motorCount; i++) {
    if (motors[i]->distanceToGo() == 0) {
      if(motorRunning[i]) {
        float stopPosition = motors[i]->currentPosition() / oneRotation;
        Serial.print("motor stopped at ");
        Serial.println(stopPosition);
        
        // send msg to phone with current Position of motor
        Serial1.print("ms;");
        Serial1.print(i);
        Serial1.print(";");
        Serial1.print(stopPosition);
        Serial1.println(";\n");
        
        motorRunning[i] = false;
      }
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
    // if motor is not running, and in range
    long cPos = motors[stepper]->currentPosition();
    if (motors[stepper]->distanceToGo() == 0 &&  cPos + r >= motorMinPos /*&& cPos + r <= motorMaxPos*/) {
      
      if (aboslutePosition) {
        // move to aboslute position
        motorRunning[stepper] = true;
        motors[stepper]->moveTo(r);
      } else {
        // move to relative position
        motorRunning[stepper] = true;
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
  
  int ledP   = -1;
  int value = -1;
  
  int counter = 0;
  String tmp = "";
  for (int i = 0; i < message.length(); i++) {
    if(message.charAt(i) != ';') {
      tmp += message.charAt(i);
    } else {
      if (counter == 0) {
        // parse led #
        ledP = tmp.toInt();
      } else if (counter == 1) {
        // parse led value
        value = tmp.toInt();
      }
      tmp = "";
      counter++;
    }
  }
  
  // set value
  if (ledP >= 0 && value >= 0 && value <= 255) {
    analogWrite(led[ledP], value);
  }
}






void initStepper() {
  // init all steppers
  for (int i=0; i < sizeof(motors); i++) {
      motors[i]->setMaxSpeed(1000);
      motors[i]->setAcceleration(800);
    } 
}

void initLEDs() {
  // set the pins as output
  for (int i = 0; i < sizeof(led); i++) {
    pinMode(led[i], OUTPUT); 
  }
    
}

