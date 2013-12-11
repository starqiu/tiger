package codegen.C.mainMethod;

import codegen.C.Visitor;

import java.util.LinkedList;

public class MainMethod extends T
{
	public LinkedList<codegen.C.dec.T> locals;
	public codegen.C.stm.T stm;

	public MainMethod(LinkedList<codegen.C.dec.T> locals, codegen.C.stm.T stm)
	{
		this.locals = locals;
		this.stm = stm;
	}

	@Override
	public void accept(Visitor v)
	{
		v.visit(this);
	}
}