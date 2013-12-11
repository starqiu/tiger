package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

// pop int from the stack, and store it in local variable <index>
public class Istore extends T 
{
	public int index;

	public Istore(int index)
	{
		this.index = index;
	}

	@Override
	public void accept(Visitor v)
	{
		v.visit(this);
	}
}