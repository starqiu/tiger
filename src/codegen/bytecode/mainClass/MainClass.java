package codegen.bytecode.mainClass;

import codegen.bytecode.Visitor;

import java.util.LinkedList;

public class MainClass extends T 
{
	public String id;
	public String arg;
	public LinkedList<codegen.bytecode.stm.T> stms;

	public MainClass(String id, String arg, LinkedList<codegen.bytecode.stm.T> stms) 
	{
		this.id = id;
		this.arg = arg;
		this.stms = stms;
	}

	@Override
	public void accept(Visitor v) 
	{
		v.visit(this);
		return;
	}
}