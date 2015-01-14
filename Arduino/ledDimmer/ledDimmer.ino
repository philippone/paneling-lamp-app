
int ledPin = 3;

void setup() {
  // put your setup code here, to run once:

  pinMode(ledPin, OUTPUT);   // sets the pin as output
  Serial.begin(9600);
}

int i = 0;
char msg;
String message;

void loop() {
  while (Serial.available() > 0) {
      char msg = Serial.read();
      
      //Serial.print("all: ");
      //Serial.println(msg);
      
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
  
  analogWrite(ledPin, 1);
  
  // put your main code here, to run repeatedly: 
  //val = analogRead(analogPin);   // read the input pin
  
  /*
  for (i = 0; i < 255; i++) {
    analogWrite(ledPin, i);  // analogRead values go from 0 to 1023, analogWrite values from 0 to 255
    Serial.println(i);
    
    //delay(500);  
  }
*/
}



void handle(String msg) {
  msg.trim();

  //String splitted = split(msg);

  
  if (msg.charAt(i) == 'd') {
  }
  
  
}

String[] test() {

}

/*
String[] split(String msg) {

  String splitted[msg.length()];
  int j = 0;
  String value = "";
  for(int i = 0; i <= msg.length(); i++) {
    if (msg.charAt(i) == delimiter) {
      splitted[j++] = value;
    } else {
      value += msg.charAt(i);
    }
  }
  
  return splitted;
}
*/
