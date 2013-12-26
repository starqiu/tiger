#ifndef EXCEPTION_H
#define EXCEPTION_H

#include <setjmp.h>

struct ex_stack {
  struct ex_stack *prev;
  jmp_buf context;
  int match;
};
struct ex_stack *global_exception_stack;
jmp_buf mainContext;

#endif
