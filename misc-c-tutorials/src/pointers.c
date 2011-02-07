#include <stdio.h>


int main(){
	float x;
	float y;
	float* pointerX;
	float* pointerY;


	printf("weise x den wert 4 zu.\n");
	x = 4.0;
	printf("x        wert=%f     addresse=%ld\n", x, &x);

	printf("referenziere pointerX auf x\n");
	pointerX = &x;
	printf("pointerX wert=%f\n", *pointerX);

	printf("weise pointerX den wert 3 zu\n");
	*pointerX = 3.0;
	printf("pointerX wert=%f\n", *pointerX);
	printf("x        wert=%f\n", x);

	printf("multipliziere pointerX mit 10\n");
	*pointerX = *pointerX * 10.0;
	printf("x        wert=%f\n", x);
	printf("pointerX wert=%f\n", *pointerX);

	printf("weise y *pointerX zu, und pointerY auf pointerX\n");
	y = *pointerX;
	pointerY = pointerX;
	printf("y        wert=%f\n", y);
	printf("pointerY wert=%f\n", *pointerY);

	return 0;
}
