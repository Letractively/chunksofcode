/*
 ============================================================================
 Name        : c_tutorial_01.c
 Author      : andre
 Version     :
 Copyright   : (c) 2009
 Description : Hello World in C, Ansi-style
 ============================================================================
 */

#include <stdio.h>
#include <stdlib.h>
#define LAENGE 3

int main(void) {

	float x[LAENGE];
	float *pointerArr;
	int i;

	for (i = 0; i < LAENGE; ++i)
		x[i] = 0.5 * (float) i;

	for (i = 0; i < LAENGE; ++i)
		printf("x[%d] : %f\n", i, x[i]);

	pointerArr = x;

	for (i = 0; i < LAENGE; ++i)
		printf("*(pointerArr + %d) : %f\n", i, *(pointerArr + i));

	return EXIT_SUCCESS;
}
