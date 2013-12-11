package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

// pop int array, an int value and index from the stack, 
// and store it in an element at <index> of the array
public class IAstore extends T
{
	public IAstore()
	{
	}
	
	@Override
	public void accept(Visitor v) 
	{
		v.visit(this);
	}
}