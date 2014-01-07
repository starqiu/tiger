package ast.optimizations;

import ast.exp.Paren;
import ast.stm.Throw;
import ast.stm.TryCatch;

// Dead code elimination optimizations on an AST.

public class DeadCode implements ast.Visitor {
	private ast.classs.T newClass;
	private ast.mainClass.T mainClass;
	private ast.stm.T stm;
	private boolean isTrue;
	private boolean isLiteralBoolean;
	private java.util.LinkedList<ast.classs.T> classes;
	private java.util.LinkedList<ast.method.T> mthods;
	public ast.program.T program;

	public DeadCode() {
		this.newClass = null;
		this.mainClass = null;
		this.program = null;
	}

	// //////////////////////////////////////////////////////
	//
	public String genId() {
		return util.Temp.next();
	}

	// /////////////////////////////////////////////////////
	// expressions
	@Override
	public void visit(ast.exp.Add e) {
		this.isLiteralBoolean = false;
	}

	@Override
	public void visit(ast.exp.And e) {
		e.left.accept(this);
		boolean leftIsLiteralBoolean = this.isLiteralBoolean;
		boolean leftIsTrue = this.isTrue;
		e.right.accept(this);
		boolean rightIsLiteralBoolean = this.isLiteralBoolean;
		boolean rightIsTrue = this.isTrue;
		if (leftIsLiteralBoolean && rightIsLiteralBoolean) {
			this.isLiteralBoolean = true;
			this.isTrue = leftIsTrue && rightIsTrue;
		} else
			return;
	}

	@Override
	public void visit(ast.exp.ArraySelect e) {
		this.isLiteralBoolean = false;
	}

	@Override
	public void visit(ast.exp.Call e) {
		this.isLiteralBoolean = false;
	}

	@Override
	public void visit(ast.exp.False e) {
		this.isLiteralBoolean = true;
		this.isTrue = false;
	}

	@Override
	public void visit(ast.exp.Id e) {
		this.isLiteralBoolean = false;
	}

	@Override
	public void visit(ast.exp.Length e) {
		this.isLiteralBoolean = false;
	}

	@Override
	public void visit(ast.exp.Lt e) {
		this.isLiteralBoolean = false;
	}

	@Override
	public void visit(ast.exp.NewIntArray e) {
		this.isLiteralBoolean = false;
	}

	@Override
	public void visit(ast.exp.NewObject e) {
		this.isLiteralBoolean = false;
	}

	@Override
	public void visit(ast.exp.Not e) {
		e.exp.accept(this);
		if (this.isLiteralBoolean)
			this.isTrue = !this.isTrue;
		else
			return;
	}

	@Override
	public void visit(ast.exp.Num e) {
		this.isLiteralBoolean = false;
	}

	@Override
	public void visit(ast.exp.Sub e) {
		this.isLiteralBoolean = false;
	}

	@Override
	public void visit(ast.exp.This e) {
		this.isLiteralBoolean = false;
	}

	@Override
	public void visit(ast.exp.Times e) {
		this.isLiteralBoolean = false;
	}

	@Override
	public void visit(ast.exp.True e) {
		this.isLiteralBoolean = true;
		this.isTrue = true;
	}

	// statements
	@Override
	public void visit(ast.stm.Assign s) {
		this.stm = s;
	}

	@Override
	public void visit(ast.stm.AssignArray s) {
		this.stm = s;
	}

	@Override
	public void visit(ast.stm.Block s) {
		this.stm = s;
	}

	@Override
	public void visit(ast.stm.If s) {
		this.isLiteralBoolean = false;
		s.condition.accept(this);
		if (this.isLiteralBoolean) {
			if (this.isTrue)
				this.stm = s.thenn;
			else
				this.stm = s.elsee;
		} else
			this.stm = s;
	}

	@Override
	public void visit(ast.stm.Print s) {
		this.stm = s;
	}

	@Override
	public void visit(ast.stm.While s) {
		this.isTrue = true;
		this.isLiteralBoolean = false;
		s.condition.accept(this);
		if (this.isLiteralBoolean && this.isTrue == false)
			this.stm = null;
		else
			this.stm = s;
	}

	// type
	@Override
	public void visit(ast.type.Boolean t) {
		return;
	}

	@Override
	public void visit(ast.type.Class t) {
		return;
	}

	@Override
	public void visit(ast.type.Int t) {
		return;
	}

	@Override
	public void visit(ast.type.IntArray t) {
		return;
	}

	// dec
	@Override
	public void visit(ast.dec.Dec d) {
		return;
	}

	// method
	@Override
	public void visit(ast.method.Method m) {
		java.util.LinkedList<ast.stm.T> stms = new java.util.LinkedList<ast.stm.T>();
		for (ast.stm.T stm : m.stms) {
			stm.accept(this);
			if (this.stm != null)
				stms.add(this.stm);
		}
		this.mthods.add(new ast.method.Method(m.retType, m.id, m.formals,
				m.locals, stms, m.retExp));
	}

	// class
	@Override
	public void visit(ast.classs.Class c) {
		this.mthods = new java.util.LinkedList<ast.method.T>();
		for (ast.method.T m : c.methods)
			m.accept(this);
		this.classes.add(new ast.classs.Class(c.id, c.extendss, c.decs,
				this.mthods));
	}

	// main class
	@Override
	public void visit(ast.mainClass.MainClass c) {
		c.stm.accept(this);
		this.mainClass = new ast.mainClass.MainClass(c.id, c.arg, this.stm);
	}

	// program
	@Override
	public void visit(ast.program.Program p) {
		// we don't reuse ast node
		p.mainClass.accept(this);
		this.classes = new java.util.LinkedList<ast.classs.T>();
		for (ast.classs.T clazz : p.classes)
			clazz.accept(this);

		this.program = new ast.program.Program(this.mainClass, this.classes);

		if (control.Control.trace.equals("ast.DeadCode")) {
			System.out.println("before optimization:");
			ast.PrettyPrintVisitor pp = new ast.PrettyPrintVisitor();
			p.accept(pp);
			System.out.println("after optimization:");
			this.program.accept(pp);
		}
		return;
	}

	@Override
	public void visit(Paren e) {
		this.isLiteralBoolean = false;
		return;
	}

	@Override
	public void visit(TryCatch s) {
		this.stm = s;
		return;
	}

	@Override
	public void visit(Throw s) {
		this.stm = s;
		return;
	}
}
