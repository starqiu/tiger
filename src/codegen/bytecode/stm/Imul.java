package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

// pop two integers from the operand stack, multiply them, 
// and push the integer result back onto the stack
public class Imul extends T 
{
	public Imul() 
	{
	}

	@Override
	public void accept(Visitor v) 
	{
		v.visit(this);
	}
}