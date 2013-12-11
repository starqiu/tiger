package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

// pop int array and index from the stack, 
// get an int value at <index> from the int array, 
// and push it onto the stack
public class IAload extends T
{
	public IAload()
	{
	}
	
	@Override
	public void accept(Visitor v) 
	{
		v.visit(this);
	}
}