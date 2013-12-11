package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

// pop a reference from the stack, get the value of the field, 
// and push the value onto the stack
public class Getfield extends T
{
	public String c;
	public String f;
	public codegen.bytecode.type.T type;
	
	public Getfield(String c, String f, codegen.bytecode.type.T type)
	{
		this.c = c;
		this.f = f;
		this.type = type;
	}

	@Override
	public void accept(Visitor v) 
	{
		v.visit(this);
	}
}