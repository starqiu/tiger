package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

// get length of array
public class ArrayLength extends T
{
	public ArrayLength() 
	{
	}

	@Override
	public void accept(Visitor v) 
	{
		v.visit(this);
	}
}