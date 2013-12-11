package ast.stm;

import java.util.LinkedList;

public class Block extends T 
{
	public LinkedList<T> stms;

	public Block(LinkedList<T> stms) 
	{
		this.stms = stms;
	}

	@Override
	public void accept(ast.Visitor v) 
	{
		v.visit(this);
	}
}