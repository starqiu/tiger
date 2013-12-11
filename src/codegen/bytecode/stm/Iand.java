package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

// compute the bitwise and
public class Iand extends T
{
	public Iand()
	{
	}
	
	@Override
	public void accept(Visitor v) 
	{
		v.visit(this);
	}
}