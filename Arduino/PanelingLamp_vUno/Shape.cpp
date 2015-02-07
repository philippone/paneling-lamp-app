#include "Shape.h"



Shape::Shape(int i, float mv1, float mv2, float mv3, float mv4, float mv5, int lv1, int lv2, int lv3, int lv4, int lv5, int lv6, int lv7) {
	
	index = i;
	
	m0 = mv1;
	m1 = mv2;
	m2 = mv3;
	m3 = mv4;
	m4 = mv5;
	
	l0 = lv1;
	l1 = lv2;
	l2 = lv3;
	l3 = lv4;
	l4 = lv5;
	l5 = lv6;
	l6 = lv7;
	
	
	/*
	float m[] = {mv1,mv2,mv3,mv4,mv5};
	int l[] = {l1,l2,l3,l4,l5,l6,l7};
	for (int i = 0; i < 5; i++) {
		motorPositions[i] = m[i];
	}
	for (int j=0; j< 7; j++) {
		ledValues[j] = l[j];
	}
	*/
}


int Shape::getIndex() {
	return index;
}