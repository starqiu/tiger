package codegen.C.program;

import codegen.C.Visitor;

import java.util.LinkedList;

public class Program extends T 
{
	public LinkedList<codegen.C.classes.T> classes;
	public LinkedList<codegen.C.vtable.T> vtables;
	public LinkedList<codegen.C.method.T> methods;
	public codegen.C.mainMethod.T mainMethod;

	public Program(LinkedList<codegen.C.classes.T> classes, LinkedList<codegen.C.vtable.T> vtables,
			LinkedList<codegen.C.method.T> methods, codegen.C.mainMethod.T mainMethod) 
	{
		this.classes = classes;
		this.vtables = vtables;
		this.methods = methods;
		this.mainMethod = mainMethod;
	}

	@Override
	public void accept(Visitor v) 
	{
		v.visit(this);
		return;
	}
}