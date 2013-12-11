package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

import java.util.LinkedList;

// invoke all methods except interface methods(which use invokeinterface), 
// static methods(which use invokestatic), 
// and the few special cases handled by invokespecial
public class Invokevirtual extends T 
{
	public String f;
	public String c;
	public LinkedList<codegen.bytecode.type.T> at;
	public codegen.bytecode.type.T rt;

	public Invokevirtual(String f, String c, LinkedList<codegen.bytecode.type.T> at, codegen.bytecode.type.T rt) 
	{
		this.f = f;
		this.c = c;
		this.at = at;
		this.rt = rt;
	}

	@Override
	public void accept(Visitor v) 
	{
		v.visit(this);
	}
}