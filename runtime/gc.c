#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// "new" a new object, do necessary initializations, and
// return the pointer (reference).
void *Tiger_new (void *vtable, int size)
{
  // You should write 4 statements for this function.
  // #1: "malloc" a chunk of memory of size "size":
  void* p = (void*) malloc(size);
  // #2: clear this chunk of memory (zero off it):
  //free(p);
  // #3: set up the "vtable" pointer properly:
  vtable = p;
  // #4: return the pointer 
  return vtable;
}
