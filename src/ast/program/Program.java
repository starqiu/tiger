package ast.program;

import ast.Visitor;

import java.util.LinkedList;

public class Program extends T 
{
	public ast.mainClass.T mainClass;
	public LinkedList<ast.classs.T> classes;

	public Program(ast.mainClass.T mainClass, LinkedList<ast.classs.T> classes) 
	{
		this.mainClass = mainClass;
		this.classes = classes;
	}

	@Override
	public void accept(Visitor v) 
	{
		v.visit(this);
		return;
	}
}