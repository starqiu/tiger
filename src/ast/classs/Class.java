package ast.classs;
import ast.Visitor;

import java.util.LinkedList;

public class Class extends T 
{
	public String id;
	public String extendss;// null for non-existing "extends"
	public LinkedList<ast.dec.T> decs;
	public LinkedList<ast.method.T> methods;

	public Class(String id, String extendss, LinkedList<ast.dec.T> decs, LinkedList<ast.method.T> methods) 
	{
		this.id = id;
		this.extendss = extendss;
		this.decs = decs;
		this.methods = methods;
	}

	@Override
	public void accept(Visitor v) 
	{
		v.visit(this);
		return;
	}
}
