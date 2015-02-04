#include <AccelStepper.h>
#include <SoftwareSerial.h>

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
float lowerBound = 1600;


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
	  
      // send to usb 
      Serial.print("inc: ");
      Serial.print(message);
	  
      // send back to phone (test only)
      //Serial1.print("all: ");
      //Serial1.println(message);
	  
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
        float stopPosition = motors[i]->currentPosition() / oneRotation;
        Serial.print("motor ");
		Serial.print(i);
		Serial.print(" stopped at ");
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

	if (message.startsWith("c;")) 
		handleConnectedPhone(message.substring(2));
	else if (message.startsWith("mf;"))
		handleMoveForm(message.substring(3));
  	else if (message.startsWith("sa;"))
    	handleStepperPos(message.substring(3), true);
  	else if (message.startsWith("sr;"))
    	handleStepperPos(message.substring(3), false);
  	else if (message.startsWith("fs;"))
    	handleStepperForceStop(message.substring(3));
  	else if (message.startsWith("fr;"))
    	handleStepperForceReset(message.substring(3));
  	else if (message.startsWith("op;"))
	  	handleStepperOverridePos(message.substring(3));
  	else if (message.startsWith("l;")) 
    	handleLEDMsg(message.substring(2));
	else if (message.startsWith("b;"))
		handleBoundsMsg(message.substring(2));
	else if (message.startsWith("rb;"))
		handleRequestBounds();
}

void handleRequestBounds() {
	Serial1.print("br;");
	Serial1.print(upperBoundActive);
	Serial1.print(";");
	Serial1.print(upperBound);
	Serial1.print(";");
	Serial1.print(lowerBoundActive);
	Serial1.print(";");
	Serial1.print(lowerBound);
	Serial1.println(";");
}



/*
* When phone connects, send all motor positions as reply
*/
void handleConnectedPhone(String message) {
	Serial1.print("r;");
	for (int i = 0; i < motorCount; i++) {
		float position = motors[i]->currentPosition() / oneRotation;
		Serial1.print(position);
		Serial1.print(";");
	}
	for (int i = 0; i < sizeof(ledValue); i++) {
		Serial1.print(ledValue[i]);
		Serial1.print(";");
	}
	Serial1.print("\n");
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
    Serial1.print("ms;");
    Serial1.print(stepper);
    Serial1.print(";");
    Serial1.print(stopPosition);
    Serial1.println(";fail;\n");
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
    Serial1.print("ms;");
    Serial1.print(stepper);
    Serial1.print(";");
    Serial1.print(stopPosition);
    Serial1.println(";\n");
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
	      Serial1.print("ms;");
	      Serial1.print(stepper);
	      Serial1.print(";");
	      Serial1.print(newPosition);
	      Serial1.println(";\n");
      } 
      else fail = true;
    } 
    else fail = true;

    if (fail) {
      // send msg to phone with current Position of motor
      float stopPosition = motors[stepper]->currentPosition() / oneRotation;
      Serial1.print("ms;");
      Serial1.print(stepper);
      Serial1.print(";");
      Serial1.print(stopPosition);
      Serial1.println(";\n");
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
	Serial1.print("mfr;");
    Serial1.print(currFormID);
    Serial1.println(";\n");
	
	// to computer
    Serial.print("mfr;");
    Serial.print(currFormID);
    Serial.println(";\n");
}


void initStepper() {
  // init all steppers
  for (int i=0; i < sizeof(motors); i++) {
    motors[i]->setMaxSpeed(2000);
    motors[i]->setAcceleration(1000);
  } 
}

void initLEDs() {
  // set the pins as output
  for (int i = 0; i < sizeof(led); i++) {
    pinMode(led[i], OUTPUT); 
  }

}


