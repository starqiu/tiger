package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

// push constant <i> from run-time constant pool onto the stack
public class Ldc extends T 
{
	public int i;

	public Ldc(int i)
	{
		this.i = i;
	}

	@Override
	public void accept(Visitor v) 
	{
		v.visit(this);
	}
}