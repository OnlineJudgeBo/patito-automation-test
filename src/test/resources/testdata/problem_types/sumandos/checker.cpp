#include "testlib.h"

// Acepta cualquier par de enteros no negativos que sumen N.
int main(int argc, char *argv[]) {
    registerTestlibCmd(argc, argv);
    long long n = inf.readLong();
    long long a = ouf.readLong(0, n, "a");
    long long b = ouf.readLong(0, n, "b");
    if (a + b != n) quitf(_wa, "%lld + %lld no es %lld", a, b, n);
    quitf(_ok, "correcto");
}
