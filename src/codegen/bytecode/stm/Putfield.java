package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

// set the value of the field in a reference onto the stack
public class Putfield extends T
{
	public String c;
	public String f;
	public codegen.bytecode.type.T type;
	
	public Putfield(String c, String f, codegen.bytecode.type.T type)
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