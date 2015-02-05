#ifndef Shape_h
#define Shape_h

#include <Arduino.h>

class Shape {
	
private:
	int index;
	float[] motorPositions;
	int[] ledValues;
	
	Shape(int i, float[] mV, int lV);
	int getIndex();
	float[] getMotorPositions();
	int[] getLedValues();
	
}

#endif