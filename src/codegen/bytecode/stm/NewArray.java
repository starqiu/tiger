package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

// create new array which is left on the stack
public class NewArray extends T
{
	public NewArray()
	{
	}
	
	@Override
	public void accept(Visitor v)
	{
		v.visit(this);
	}
}