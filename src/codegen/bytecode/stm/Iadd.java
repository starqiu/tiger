package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

// pop two integers from the operand stack, add them, 
// and push the integer result back onto the stack
public class Iadd extends T
{
	public Iadd() 
	{
	}

	@Override
	public void accept(Visitor v) 
	{
		v.visit(this);
	}
}