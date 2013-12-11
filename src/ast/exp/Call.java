package ast.exp;

import java.util.LinkedList;

public class Call extends T 
{
	public T exp;
	public String id;
	public LinkedList<T> args;
	public String type;// type of first field "exp"
	public LinkedList<ast.type.T> at;// arg's type
	public ast.type.T rt;

	public Call(T exp, String id, LinkedList<T> args) 
	{
		this.exp = exp;
		this.id = id;
		this.args = args;
		this.type = null;
	}
	
	public Call(T exp, String id, LinkedList<T> args, int lineNum) 
	{
		this(exp, id, args);
		this.lineNum = lineNum;
	}

	@Override
	public void accept(ast.Visitor v) 
	{
		v.visit(this);
		return;
	}
}