#include <stdio.h>

int main(void) {
	char textA [100];
	char textB [100];
	char textC [100];

	char *pointerA;
	char *pointerB;

	char nachricht[] = "I bins nur, ein einfacher String!";
	printf("originalnachricht: '%s'\n", nachricht);

	int i = 0;
	while( (textA[i] = nachricht[i]) != '\0') {
		i++;
	}

	printf("textA: '%s'\n", textA);

	/*use explicit pointer arithmetic*/
	pointerA = nachricht;
	pointerB = textB;

	while( (*pointerB ++ = *pointerA ++) != '\0') {
		;
	}

	printf("textB: '%s'\n", textB);

	return 0;
}
