package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

// return reference from method
public class Areturn extends T 
{
	public Areturn() 
	{
	}

	@Override
	public void accept(Visitor v) 
	{
		v.visit(this);
	}
}