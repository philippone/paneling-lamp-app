
int ledPin = 13;

void setup() {
  // put your setup code here, to run once:

  pinMode(ledPin, OUTPUT);   // sets the pin as output
  Serial.begin(9600);
}

int i = 0;
char msg;
String message;

void loop() {
  /*
  for (i = 0; i < 255; i++) {
    analogWrite(ledPin, i);  // analogRead values go from 0 to 1023, analogWrite values from 0 to 255
    Serial.println(i);
    
    //delay(500);  
  }
  */
  while (Serial.available() > 0) {
      int msg = Serial.parseInt();
      
      analogWrite(ledPin, msg);
      
      //Serial.print("all: ");
      //Serial.println(msg);
      /*
      if (msg == '\n') {
        message += "\n";
        handle(message);
        // reset Message
        message = "";
      } else {
        message += msg  ;
        Serial.print("r: ");
        Serial.println(msg);
      } 
      */
      
  }
}
  
  
  /*
  // Serial communication 
  while (Serial.available() > 0){     
      msg += Serial.read();   
      
      Serial.print("received: ");
      Serial.println(msg);
      //analogWrite(ledPin, msg);
  }
  if (!Serial.available()) {
      msg = "";
  }
  
  */
  
  //analogWrite(ledPin, 1);
  
  // put your main code here, to run repeatedly: 
  //val = analogRead(analogPin);   // read the input pin
  
  /*
  for (i = 0; i < 255; i++) {
    analogWrite(ledPin, i);  // analogRead values go from 0 to 1023, analogWrite values from 0 to 255
    Serial.println(i);
    
    //delay(500);  
  }
*/


