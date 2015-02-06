#include "Shape.h"

Shape::Shape(int i, float* positions, int* lV) {
	motorPositions = positions;
	ledValues = lV;
}

Shape::Shape(int i, float m1, float m2, float m3, float m4, float m5, int l1, int l2, int l3, int l4, int l5, int l6, int l7) {
	float m[] = {m1,m2,m3,m4,m5};
	int l[] = {l1,l2,l3,l4,l5,l6,l7};
	motorPositions = m;
	ledValues = l;
}


int Shape::getIndex() {
	return index;
}

float* Shape::getMotorPositions() {
	return motorPositions;
}

int* Shape::getLedValues() {
	return ledValues;
}