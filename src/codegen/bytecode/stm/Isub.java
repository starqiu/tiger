package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

// pop two integers from the operand stack, sub them, 
// and push the integer result back onto the stack
public class Isub extends T
{
	public Isub() 
	{
	}

	@Override
	public void accept(Visitor v) 
	{
		v.visit(this);
	}
}