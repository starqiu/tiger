package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

// pop reference from the stack, and store it in local variable <index>
public class Astore extends T 
{
	public int index;

	public Astore(int index) 
	{
		this.index = index;
	}

	@Override
	public void accept(Visitor v) 
	{
		v.visit(this);
	}
}