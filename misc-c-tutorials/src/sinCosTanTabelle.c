#include <stdio.h>
#include <math.h>

int main() {

	int     i_grad;
	double  d_radiant;
	double  d_pi;
	double  d_sin, d_cos, d_tan;

	printf("\nBerechne eine tabelle der sinusfunktion:\n\n");
	d_pi = 4.0 * atan(1.0);
	printf("pi ist: %f \n\n", d_pi);
	printf("winkel     sinus   cosinus    tangens\n");

	i_grad = 0;

	while(i_grad <= 360) {

		d_radiant = d_pi * i_grad / 180.0;
		d_sin = sin(d_radiant);
		d_cos = cos(d_radiant);
		d_tan = tan(d_radiant);

		printf("%+3d        %+f  %+f  %+f\n", i_grad, d_sin, d_cos, d_tan);
		i_grad += 10;
	}

	return 0;
}

