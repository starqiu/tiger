package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

// create new object instances which is left on the stack
public class New extends T
{
	public String c;

	public New(String c) 
	{
		this.c = c;
	}

	@Override
	public void accept(Visitor v)
	{
		v.visit(this);
	}
}