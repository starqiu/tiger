#include "exception.h"
#include <stdio.h>

extern struct ex_stack *global_exception_stack;
extern struct ex_stack this_entry;

//push
void Tiger_try(struct ex_stack *this_entryy){
	this_entryy->prev = global_exception_stack;
	global_exception_stack = this_entryy;
}

//pop
void Tiger_catch(struct ex_stack *this_entryy){
	global_exception_stack = this_entryy->prev;
}

void Tiger_throw(int match){
	if (global_exception_stack  == NULL)
		longjmp(mainContext,65530);
	//get the matched handler
	while (global_exception_stack->match  != match){
		if (global_exception_stack  == NULL)
			longjmp(mainContext,65530);
		global_exception_stack = global_exception_stack->prev;
	}
	longjmp(global_exception_stack->context,match);
}
