package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

// return int or boolean from method
public class Ireturn extends T 
{
	public Ireturn() 
	{
	}

	@Override
	public void accept(Visitor v)
	{
		v.visit(this);
	}
}