#include "testlib.h"

// Da todo el caso por el doble exacto y la mitad si la respuesta se pasa o falta por 1.
int main(int argc, char *argv[]) {
    registerTestlibCmd(argc, argv);
    long long esperado = ans.readLong();
    long long diferencia = ouf.readLong() - esperado;
    if (diferencia == 0) quitf(_ok, "correcto");
    if (diferencia == 1 || diferencia == -1) quitp(0.5, "cerca");
    quitf(_wa, "lejos");
}
