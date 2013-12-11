package codegen.bytecode.program;

import codegen.bytecode.Visitor;

import java.util.LinkedList;

public class Program extends T 
{
	public codegen.bytecode.mainClass.T mainClass;
	public LinkedList<codegen.bytecode.classs.T> classes;

	public Program(codegen.bytecode.mainClass.T mainClass, LinkedList<codegen.bytecode.classs.T> classes) 
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