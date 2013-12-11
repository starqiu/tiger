package ast.method;

import ast.Visitor;

import java.util.LinkedList;

public class Method extends T
{
	public ast.type.T retType;// the type of return
	public String id;
	public LinkedList<ast.dec.T> formals;// parameters
	public LinkedList<ast.dec.T> locals;// local variables
	public LinkedList<ast.stm.T> stms;
	public ast.exp.T retExp;

	public Method(ast.type.T retType, String id, 
			LinkedList<ast.dec.T> formals, LinkedList<ast.dec.T> locals,
			LinkedList<ast.stm.T> stms, ast.exp.T retExp) 
	{
		this.retType = retType;
		this.id = id;
		this.formals = formals;
		this.locals = locals;
		this.stms = stms;
		this.retExp = retExp;
	}

	@Override
	public void accept(Visitor v) 
	{
		v.visit(this);
		return;
	}
}