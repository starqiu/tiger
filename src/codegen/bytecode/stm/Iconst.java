package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

// push int constant <i> onto the stack
public class Iconst extends T
{
	public int i;

	public Iconst(int i)
	{
		this.i = i;
	}

	@Override
	public void accept(Visitor v) 
	{
		v.visit(this);
	}
}