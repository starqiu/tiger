package codegen.C.classes;

import codegen.C.Visitor;

import java.util.LinkedList;

public class Class extends T
{
	public String id;
	public LinkedList<codegen.C.Tuple> decs;

	public Class(String id, LinkedList<codegen.C.Tuple> decs)
	{
		this.id = id;
		this.decs = decs;
	}

	@Override
	public void accept(Visitor v)
	{
		v.visit(this);
	}
}