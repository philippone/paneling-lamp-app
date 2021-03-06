
#include <AccelStepper.h>
#include <SoftwareSerial.h>
#include "Shape.h"

// stepper
int motorCount = 5;
int ledNumber = 7;


AccelStepper* stepper0 = new AccelStepper(1, 23, 22);
AccelStepper* stepper1 = new AccelStepper(1, 25, 24);
AccelStepper* stepper2 = new AccelStepper(1, 27, 26);
AccelStepper* stepper3 = new AccelStepper(1, 29, 28);
AccelStepper* stepper4 = new AccelStepper(1, 31, 30);
AccelStepper* motors[5] = {
  stepper0, stepper1, stepper2, stepper3, stepper4};
boolean motorRunning[5] = {
  false, false, false, false, false};

float oneRotation = 1600;
// min/max Position in Rotations (x * oneRotation)
int motorMinPos = 0 * oneRotation;
int motorMaxPos = 100 * oneRotation;

//LED stuff
int led[7] = {
  13, 12, 11,3, 2, 8, 7 };
int ledValue[7] = {0,0,0,0,0,0,0}; 

// bound stuff
boolean upperBoundActive = true;
boolean lowerBoundActive = true;
float upperBound = 0;
float lowerBound = 320000;




//demoMode
boolean demoModeActive = true;
float demoModeMinutes = 1;
long lastDemoChangeTime = 0;
int demoIndex = 0;
Shape* demoShapes[13] = { 
	new Shape(0, 8,3,0,0,0, 			255, 255, 255, 255, 255, 255, 255 ),
	new Shape(1, 0,28,0,9,8,			255, 255, 255, 255, 255, 255, 255 ),
	new Shape(2, 22, 28, 0,0,8,			255, 255, 255, 255, 255, 255, 255 ),
	new Shape(3, 22, 28, 0, 0, 20,		255, 255, 255, 255, 255, 255, 255 ),
	new Shape(4, 17, 1, 1, 12, 5,		255, 255, 255, 255, 255, 255, 255 ),
	new Shape(5, 0, 17, 25.5,0, 15,		255, 255, 255, 255, 255, 255, 255 ),
	new Shape(6, 0, 35.5, 42,0,20,		255, 255, 255, 255, 255, 255, 255 ),
	new Shape(7, 0, 7.8, 0,10.5,3,		255, 255, 255, 255, 255, 255, 255 ),
	new Shape(8, 0,0,0, 21, 3,			255, 255, 255, 255, 255, 255, 255 ),
	new Shape(9, 0,0,10,21, 12,			255, 255, 255, 255, 255, 255, 255 ),
	new Shape(10, 3,0,15,0,0,			255, 255, 255, 255, 255, 255, 255 ),
	new Shape(11, 4,15.5,18.5,0,12,		255, 255, 255, 255, 255, 255, 255 ),
	new Shape(12, 0,0,6.5,15.5,9.5,		255, 255, 255, 255, 255, 255, 255 ),
};

// communication
String message;
int msg = 0;
int pos = 0;
boolean powerStatus = false;
long currFormID = -1;
boolean moveToForm;
boolean tranformToForm = false;

void setup(){  

  initStepper();
  initLEDs();
  
  moveToForm = false;

  // Open serial communications and wait for port to open:
  Serial.begin(9600);
  Serial.begin(9600);


  lastDemoChangeTime = millis();

  // TODO
  // request positions of smartphone
  // at the moment all on position 0 at startup
  // stepper1.setCurrentPosition(...)
}



void loop() {

  // listen to bluetooth
  while (Serial.available() > 0) {
    char msg = Serial.read();

    if (msg == '*' || msg == '\n') {
      message += "\n";
	  
      // send to usb 
      Serial.print("inc: ");
      Serial.print(message);
	  
      // send back to phone (test only)
      //Serial.print("all: ");
      //Serial.println(message);
	  
      handle(message);

      // reset Message
      message = "";
    } 
    else {
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
    } 
    else {
      message += msg  ;
      //Serial.print("r: ");
      //Serial.println(msg);
    } 
  }
  
  // check that motor never reach position -1
  for (int i = 0; i < motorCount; i++) {
	boolean run = true;  
	int error = -1;
	  
    if(upperBoundActive) {
    	if (motors[i]->currentPosition() < upperBound)  {
			
    		run = false;
			error = 0;
		}
    }
    	
	if (lowerBoundActive) {
		if (motors[i]->currentPosition() > lowerBound) {
			run = false;
			error = 1;
		}
	}
		
	if (run){
		// run motor if motor is in bound correct
      	motors[i]->run();
	} else {
		if (error == 0)
    		motors[i]->setCurrentPosition(upperBound);
		else if (error == 1)
			motors[i]->setCurrentPosition(lowerBound);
    }
  }
  
  
  /*
for (int i = 0; i < motorCount; i++) {
  motors[i]->run();
}
  */

  // send rely to phone when motor has reached his position
  for (int i = 0; i < motorCount; i++) {
    if (motors[i]->distanceToGo() == 0) {
      if(motorRunning[i]) {
		// send stop position to phone
      	float stopPosition = motors[i]->currentPosition() / oneRotation;
        Serial.print("motor ");
		Serial.print(i);
		Serial.print(" stopped at ");
        Serial.println(stopPosition);

        // send msg to phone with current Position of motor
        Serial.print("ms;");
        Serial.print(i);
        Serial.print(";");
        Serial.print(stopPosition);
        Serial.println(";\n");

        motorRunning[i] = false;
      }
	  
	  // if all motors are stopped
	  // and demoMode is active
	  // move motors
	  if (demoModeActive) {
		  // test if x minutes are passed
		  if ((millis() - lastDemoChangeTime) >= (demoModeMinutes * 60 * 1000)) {
			  lastDemoChangeTime = millis();
			  changeDemoShape();
		  }
		  
	  }
    }
  }

  // check if form is reached
  // and send message to phone
  
  if (tranformToForm) {
	  boolean reached = true;
	  for (int i = 0; i < motorCount; i++) {
	    if (motors[i]->distanceToGo() > 0) {
	    	reached = false;
	    }
	  }  
	  
	  if (reached) {
		  tranformToForm = false;
		  moveToForm = false;
		  sendFormReachedReply(currFormID);
	  }
  }
  
  
} /*loop end*/

/**
 * handle incomming messages
 */
void handle(String message) {
	// set demoModeTime to current time
	// so it takes x minutes to change the form
	// if demoMode is active
	lastDemoChangeTime = millis();

	if (message.startsWith("c;")) 
		handleConnectedPhone(message.substring(2));
	else if (message.startsWith("mf;"))
		handleMoveForm(message.substring(3));
  	else if (message.startsWith("sa;"))
    	handleStepperPos(message.substring(3), true);
  	else if (message.startsWith("sr;")) {
    	handleStepperPos(message.substring(3), false);
		currFormID = -1;
	}
  	else if (message.startsWith("fs;"))
    	handleStepperForceStop(message.substring(3));
  	else if (message.startsWith("fr;")) {
    	handleStepperForceReset(message.substring(3));
		currFormID = -1;
	}
  	else if (message.startsWith("op;")) {
	  	handleStepperOverridePos(message.substring(3));
		currFormID = -1;
	}
  	else if (message.startsWith("l;")) 
    	handleLEDMsg(message.substring(2));
	else if (message.startsWith("b;"))
		handleBoundsMsg(message.substring(2));
	else if (message.startsWith("rb;"))
		handleRequestBounds();
	else if (message.startsWith("dm;"))
		handleDemoModeChange(message.substring(3)); 
}




void handleRequestBounds() {
	Serial.print("br;");
	Serial.print(upperBoundActive);
	Serial.print(";");
	Serial.print(upperBound);
	Serial.print(";");
	Serial.print(lowerBoundActive);
	Serial.print(";");
	Serial.print(lowerBound);
	Serial.print(";");
	Serial.print(demoModeActive);
	Serial.print(";");
	Serial.print(demoModeMinutes);
	Serial.println(";");
}



/*
* When phone connects, send all motor positions as reply
*/
void handleConnectedPhone(String message) {
	Serial.print("r;");
	Serial.print(currFormID);
	Serial.print(";");
	
	for (int i = 0; i < motorCount; i++) {
		float position = motors[i]->currentPosition() / oneRotation;
		Serial.print(position);
		Serial.print(";");
	}
	for (int i = 0; i < ledNumber; i++) {
		Serial.print(ledValue[i]);
		Serial.print(";");
	}
	Serial.print("\n");
}




/*
 * move lamp to form
*/
void handleMoveForm(String message) {
	float m[5];
	int l[4];
	moveToForm = true; /* set to false if form is reached */
	Serial.println("set moveToForm = true");
	
	
	
	int msgCounter = 0;
	String tmp = "";
    for (int i = 0; i < message.length(); i++) {
      if(message.charAt(i) != ';') {
        tmp += message.charAt(i);
      } 
      else {
		  // load id
		  if (msgCounter == 0) {
			  char buffer[10];
			  tmp.toCharArray(buffer, 10);
			  long id = atol(buffer);
			  currFormID = id;

			  
		  } else if (msgCounter >= 1 && msgCounter <= 5) {
			  // receive m0 - m4
	          // parse rotation
	          char buffer[10];
	          tmp.toCharArray(buffer, 10);
	          float p = atof(buffer);
			  
			  m[msgCounter -1 ] = p *  oneRotation;
			  // move to position
			  moveMotorTo(msgCounter -1, p * oneRotation);
			  moveToForm = true; /* set to false if form is reached */
			  
		  } else if (msgCounter >= 6 && msgCounter <= 9) {
			  // receive l0 - l3
			  int v = tmp.toInt();
			  l[msgCounter -6] = v;
			  
			  setLEDto(msgCounter -6, v);

		  }
	      tmp = "";
	      msgCounter++;
      }
	}
	tranformToForm = true;
}

/**
 *  move stepper # to absolute position x
 **/
void handleStepperPos(String message, boolean aboslutePosition) {
  /*
	Message: 
		relative:		sr;  motor#;  +-Rotations
		absolute		sa; ... 
  	Types  : sr/sa;  int   ;  float
  */

  int stepper 	= -1;
  float rotations = 0;

  boolean fail = false;
  int counter = 0;
  String tmp = "";
  for (int i = 0; i < message.length(); i++) {
    if(message.charAt(i) != ';') {
      tmp += message.charAt(i);
    } 
    else {
      if (counter == 0) {
        // parse stepper #
        stepper = tmp.toInt();
      } 
      else if (counter == 1) {
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
    //if (cPos + r >= motorMinPos /*&& cPos + r <= motorMaxPos*/) {

      if (aboslutePosition) {
        // move to aboslute position
		  moveMotorTo(stepper, r);
      } 
      else {
		  if (motors[stepper]->distanceToGo() == 0) {
        	  // move to relative position
        	  moveMotorRel(stepper, r);
			}
      }
    //} 
    //else fail = true;
  } 
  else fail = true;

  if (fail) {
    // send msg to phone with current Position of motor
    float stopPosition = motors[stepper]->currentPosition() / oneRotation;
    Serial.print("ms;");
    Serial.print(stepper);
    Serial.print(";");
    Serial.print(stopPosition);
    Serial.println(";fail;\n");
  } 
}

/*
* move motor to absolute position
*/
void moveMotorTo(int stepper, long position) {
	Serial.print("moveMotorTo ");
	Serial.print(stepper);
	Serial.print(" to ");
	Serial.println(position);
    motorRunning[stepper] = true;
    motors[stepper]->moveTo(position);
}

/*
* move motor relative stepps
* @param stepper which should run
* @param stepps how many stepps the stepper should run relative from its current pos
*/
void moveMotorRel(int stepper, long stepps) {
    motorRunning[stepper] = true;
    motors[stepper]->move(stepps);
}

void handleStepperForceStop(String message) {
  int stepper = -1;

  String tmp;
  for (int i = 0; i < message.length(); i++) {
    if(message.charAt(i) != ';') {
      tmp += message.charAt(i);
    } 
    else {
      stepper = tmp.toInt();
    }

    if (stepper >= 0) {
      //if (motors[stepper]->distanceToGo() > 0)
        motors[stepper]->stop();
    } 
  }
}

/*
* set stepper to position 0
 */
void handleStepperForceReset(String message) {
  int stepper = -1;

  boolean fail = false;
  String tmp;
  for (int i = 0; i < message.length(); i++) {
    if(message.charAt(i) != ';') {
      tmp += message.charAt(i);
    } 
    else {
      stepper = tmp.toInt();
    }
  }

  if (stepper >= 0) {
    if (motors[stepper]->distanceToGo() == 0) {
      motorRunning[stepper] = true;
      motors[stepper]->moveTo(0); 
    } 
    else fail = true;
  } 
  else fail = true;

  if (fail) {
    // send msg to phone with current Position of motor
    float stopPosition = motors[stepper]->currentPosition() / oneRotation;
    Serial.print("ms;");
    Serial.print(stepper);
    Serial.print(";");
    Serial.print(stopPosition);
    Serial.println(";\n");
  } 

}


void handleBoundsMsg(String message) {
	int bound = -1;
	int boundValue = -1;
	int counter = 0;
	float value = 0;
	String tmp = "";
	
    for (int i = 0; i < message.length(); i++) {
      if(message.charAt(i) != ';') {
        tmp += message.charAt(i);
      } 
      else {
        if (counter == 0) {
          // parse bound (upper or lower) #
          bound = tmp.toInt();
        } 
		else if (counter == 1) {
			// parse boundValue (true, false)
			boundValue = tmp.toInt();
		}
        else if (counter == 2) {
          // parse value
          char buffer[10];
          tmp.toCharArray(buffer, 10);
          value = atof(buffer);
		  setBound(bound, boundValue, value);
        }
        tmp = "";
        counter++;
      }
    }
}


void setBound(int bound, int boundValue, float value) {
	// upperbound -> bound == 1
	
	boolean bV = boundValue == 1 ? true : false;
	
	Serial.print("set bound ");
	Serial.print(bound);
	Serial.print(", ");
	Serial.print(bV);
	Serial.print(", ");
	Serial.println(value);
	if (bound == 1) {
		upperBoundActive = bV;
		upperBound = value;
	} else if (bound == 0) {
		lowerBoundActive = bV;
		lowerBound = value;
	}
}


/**
Msg: dm; is active; minutes

*/
void handleDemoModeChange(String message) {
	
	int counter = 0;
	int active = -1;
	float minutes = -1;
	String tmp = "";
	
    for (int i = 0; i < message.length(); i++) {
      if(message.charAt(i) != ';') {
        tmp += message.charAt(i);
      } 
      else {
        if (counter == 0) {
          // parse bound (upper or lower) #
          active = tmp.toInt();
        } 
        else if (counter == 1) {
          // parse value
          char buffer[10];
          tmp.toCharArray(buffer, 10);
          minutes = atof(buffer);
		  if (active >= 0 && minutes > 0)
		  	setDemoMode(active, minutes);
        }
        tmp = "";
        counter++;
      }
    } 
}

void setDemoMode(int active, float minutes) {
	boolean b = active == 0 ? false : true;
	// if minutes == 0 set it to 1 (1 min) as minimum
	if (minutes == 0) {
		minutes = 1;
		// send value to phone
		handleRequestBounds();
	}
	demoModeActive = b;
	demoModeMinutes = minutes;
}

/**
* override the currentPosition of the motor
*/
void handleStepperOverridePos(String message) {
	
    int stepper 	= -1;
    float newPosition = 0;

    boolean fail = false;
    int counter = 0;
    String tmp = "";
    for (int i = 0; i < message.length(); i++) {
      if(message.charAt(i) != ';') {
        tmp += message.charAt(i);
      } 
      else {
        if (counter == 0) {
          // parse stepper #
          stepper = tmp.toInt();
        } 
        else if (counter == 1) {
          // parse rotation
          char buffer[10];
          tmp.toCharArray(buffer, 10);
          newPosition = atof(buffer);
        }
        tmp = "";
        counter++;
      }
    }

    // check stepper 
    if (stepper >= 0) {

      long r = newPosition * oneRotation;
      // if motor is not running, and in range
      long cPos = motors[stepper]->currentPosition();
      if (motors[stepper]->distanceToGo() == 0) {
		  motors[stepper]->setCurrentPosition(r);
		  // send to phone
	      // send msg to phone with current Position of motor
	      	//float stopPosition = motors[stepper]->currentPosition() / oneRotation;
	      Serial.print("ms;");
	      Serial.print(stepper);
	      Serial.print(";");
	      Serial.print(newPosition);
	      Serial.println(";\n");
      } 
      else fail = true;
    } 
    else fail = true;

    if (fail) {
      // send msg to phone with current Position of motor
      float stopPosition = motors[stepper]->currentPosition() / oneRotation;
      Serial.print("ms;");
      Serial.print(stepper);
      Serial.print(";");
      Serial.print(stopPosition);
      Serial.println(";\n");
    } 
	
	
}


void handleLEDMsg(String message) {
  Serial.println("led message");

  int ledP   = -2;
  int value = -1;

  int counter = 0;
  String tmp = "";
  for (int i = 0; i < message.length(); i++) {
    if(message.charAt(i) != ';') {
      tmp += message.charAt(i);
    } 
    else {
      if (counter == 0) {
        // parse led #
        ledP = tmp.toInt();
      } 
      else if (counter == 1) {
        // parse led value
        value = tmp.toInt();
      }
      tmp = "";
      counter++;
    }
  }

  // set value
  setLEDto(ledP, value);
}


/*
* set led to value
* 0 is off
*/
void setLEDto(int ledP, int value) {
	if (ledP == -1 && value >= 0 && value <= 255) {
		for(int i = 0; i < ledNumber; i++) {
			ledValue[i] = value;
			analogWrite(led[i], value);
		}
	} else if (ledP >= 0 && value >= 0 && value <= 255) {
		ledValue[ledP] = value;
      	analogWrite(led[ledP], value);
    }
}


void sendFormReachedReply(long currFormID) {
    // to phone
	Serial.print("mfr;");
    Serial.print(currFormID);
    Serial.println(";\n");
	
	// to computer
    Serial.print("mfr;");
    Serial.print(currFormID);
    Serial.println(";\n");
}


void initStepper() {
  // init all steppers
  for (int i=0; i < motorCount; i++) {
    motors[i]->setMaxSpeed(2000);
    motors[i]->setAcceleration(1000);
  } 
}

void initLEDs() {
  // set the pins as output
  for (int i = 0; i < ledNumber; i++){
    pinMode(led[i], OUTPUT); 
  }

}



/**
change the shape if demo mode is acitve
therefore it takes the next shape out of demoShapes (our 'shape database' on Arduino)
*/
void changeDemoShape() {
	if (demoIndex == 13)
		demoIndex = 0;
	
	
	// take next shape and increase index
	Shape* s = demoShapes[demoIndex++];
	
	currFormID = s->getIndex();
	tranformToForm = true;
	
	moveMotorTo(0, s->m0  * oneRotation); motorRunning[0] = true;
	moveMotorTo(1, s->m1  * oneRotation); motorRunning[1] = true;
	moveMotorTo(2, s->m2  * oneRotation); motorRunning[2] = true;
	moveMotorTo(3, s->m3  * oneRotation); motorRunning[3] = true;
	moveMotorTo(4, s->m4  * oneRotation); motorRunning[4] = true;
	
	
	setLEDto(0, s->l0);
	setLEDto(1, s->l1);
	setLEDto(2, s->l2);
	setLEDto(3, s->l3);
	setLEDto(4, s->l4);
	setLEDto(5, s->l5);
	setLEDto(6, s->l6);
	
	
}



