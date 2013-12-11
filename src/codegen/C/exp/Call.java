package codegen.C.exp;

import codegen.C.Visitor;

import java.util.LinkedList;

public class Call extends T 
{
	//public String assign;
	public String assign;
	public T exp;
	public String id;
	public LinkedList<T> args;
	public LinkedList<codegen.C.type.T> at;// arg's type

	public Call(String assign, T exp, String id, LinkedList<T> args, LinkedList<codegen.C.type.T> at) 
	{
		this.assign = assign;
		this.exp = exp;
		this.id = id;
		this.args = args;
		this.at = at;
	}

	@Override
	public void accept(Visitor v) 
	{
		v.visit(this);
		return;
	}
}