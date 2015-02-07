#ifndef Shape_h
#define Shape_h

#include <Arduino.h>

class Shape {
	
private:
	int index;
	
public:
	float m0;
	float m1;
	float m2;
	float m3;
	float m4;
	
	int l0;
	int l1;
	int l2;
	int l3;
	int l4;
	int l5;
	int l6;
	int l7;
	
	
	Shape(int i, float m1, float m2, float m3, float m4, float m5, int l1, int l2, int l3, int l4, int l5, int l6, int l7);
	int getIndex();
	
	
};

#endif