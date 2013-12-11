package codegen.C.stm;

import codegen.C.Visitor;

import java.util.LinkedList;

public class Block extends T 
{
	public LinkedList<T> stms;

	public Block(LinkedList<T> stms) 
	{
		this.stms = stms;
	}

	@Override
	public void accept(Visitor v) 
	{
		v.visit(this);
	}
}