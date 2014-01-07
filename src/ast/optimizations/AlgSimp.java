package ast.optimizations;

import ast.exp.Paren;
import ast.stm.Throw;
import ast.stm.TryCatch;

// Algebraic simplification optimizations on an AST.

public class AlgSimp implements ast.Visitor {
	private ast.classs.T newClass;
	private ast.mainClass.T mainClass;
	private ast.stm.T stm;
	private ast.exp.T exp;
	private boolean isTrue;
	private boolean isLiteralBoolean;
	private boolean isLiteralNum;
	private int literalNum;
	private java.util.LinkedList<ast.classs.T> classes;
	private java.util.LinkedList<ast.method.T> mthods;
	public ast.program.T program;

	public AlgSimp() {
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
		e.left.accept(this);
		ast.exp.T left = this.exp;
		boolean leftIsLiteralNum = this.isLiteralNum;
		int leftLiteralNum = this.literalNum;
		e.right.accept(this);
		ast.exp.T right = this.exp;
		boolean rightIsLiteralNum = this.isLiteralNum;
		int rightLiteralNum = this.literalNum;

		if (leftIsLiteralNum && rightIsLiteralNum) {
			isLiteralNum = true;
			literalNum = leftLiteralNum + rightLiteralNum;
			this.exp = new ast.exp.Num(literalNum, e.lineNum);
		} else if (leftIsLiteralNum && leftLiteralNum == 0) {
			isLiteralNum = false;
			this.exp = right;
		} else if (rightIsLiteralNum && rightLiteralNum == 0) {
			isLiteralNum = false;
			this.exp = left;
		} else {
			isLiteralNum = false;
			this.exp = new ast.exp.Add(left, right, e.lineNum);
		}
	}

	@Override
	public void visit(ast.exp.And e) {
		e.left.accept(this);
		ast.exp.T left = this.exp;
		boolean leftIsLiteralBoolean = this.isLiteralBoolean;
		boolean leftIsTrue = this.isTrue;
		e.right.accept(this);
		ast.exp.T right = this.exp;
		boolean rightIsLiteralBoolean = this.isLiteralBoolean;
		boolean rightIsTrue = this.isTrue;
		if (leftIsLiteralBoolean && rightIsLiteralBoolean) {
			this.isLiteralBoolean = true;
			this.isTrue = leftIsTrue && rightIsTrue;
			if (this.isTrue)
				this.exp = new ast.exp.True(e.lineNum);
			else
				this.exp = new ast.exp.False(e.lineNum);
		} else if (leftIsLiteralBoolean && !leftIsTrue) {
			this.isLiteralBoolean = true;
			this.exp = new ast.exp.False(e.lineNum);
		} else {
			this.isLiteralBoolean = false;
			this.exp = new ast.exp.And(left, right, e.lineNum);
		}
	}

	@Override
	public void visit(ast.exp.ArraySelect e) {
		isLiteralNum = false;
		e.index.accept(this);
		this.exp = new ast.exp.ArraySelect(e.array, this.exp, e.lineNum);
	}

	@Override
	public void visit(ast.exp.Call e) {
		java.util.LinkedList<ast.exp.T> another = new java.util.LinkedList<ast.exp.T>();

		for (ast.exp.T arg : e.args) {
			arg.accept(this);
			another.add(this.exp);
		}
		this.exp = new ast.exp.Call(e.exp, e.id, another, e.lineNum);
	}

	@Override
	public void visit(ast.exp.False e) {
		this.isLiteralBoolean = true;
		this.isTrue = false;
		this.exp = new ast.exp.False(e.lineNum);
	}

	@Override
	public void visit(ast.exp.Id e) {
		this.isLiteralBoolean = false;
		this.isLiteralNum = false;
		this.exp = e;
	}

	@Override
	public void visit(ast.exp.Length e) {
		this.isLiteralBoolean = false;
		this.isLiteralNum = false;
		this.exp = e;
	}

	@Override
	public void visit(ast.exp.Lt e) {
		e.left.accept(this);
		ast.exp.T left = this.exp;
		boolean leftIsLiteralNum = this.isLiteralNum;
		int leftLiteralNum = this.literalNum;
		e.right.accept(this);
		ast.exp.T right = this.exp;
		boolean rightIsLiteralNum = this.isLiteralNum;
		int rightLiteralNum = this.literalNum;

		if (leftIsLiteralNum && rightIsLiteralNum) {
			isLiteralNum = true;
			isLiteralBoolean = true;
			if (leftLiteralNum < rightLiteralNum) {
				isTrue = true;
				this.exp = new ast.exp.True(e.lineNum);
			} else {
				isTrue = false;
				this.exp = new ast.exp.False(e.lineNum);
			}
		} else {
			isLiteralNum = false;
			this.exp = new ast.exp.Lt(left, right, e.lineNum);
		}
	}

	@Override
	public void visit(ast.exp.NewIntArray e) {
		isLiteralNum = false;
		e.exp.accept(this);
		this.exp = new ast.exp.NewIntArray(
				new ast.exp.Num(literalNum, e.lineNum), e.lineNum);
	}

	@Override
	public void visit(ast.exp.NewObject e) {
		this.exp = e;
	}

	@Override
	public void visit(ast.exp.Not e) {
		e.exp.accept(this);
		if (this.isLiteralBoolean) {
			this.isTrue = !this.isTrue;
			if (this.isTrue)
				this.exp = new ast.exp.True(e.lineNum);
			else
				this.exp = new ast.exp.False(e.lineNum);
		} else
			this.exp = new ast.exp.Not(this.exp, e.lineNum);
	}

	@Override
	public void visit(ast.exp.Num e) {
		isLiteralNum = true;
		literalNum = e.num;
		this.exp = new ast.exp.Num(literalNum, e.lineNum);
	}

	@Override
	public void visit(ast.exp.Sub e) {
		e.left.accept(this);
		ast.exp.T left = this.exp;
		boolean leftIsLiteralNum = this.isLiteralNum;
		int leftLiteralNum = this.literalNum;
		e.right.accept(this);
		ast.exp.T right = this.exp;
		boolean rightIsLiteralNum = this.isLiteralNum;
		int rightLiteralNum = this.literalNum;

		if (leftIsLiteralNum && rightIsLiteralNum) {
			isLiteralNum = true;
			literalNum = leftLiteralNum - rightLiteralNum;
			this.exp = new ast.exp.Num(literalNum, e.lineNum);
		} else if (rightIsLiteralNum && rightLiteralNum == 0) {
			isLiteralNum = false;
			this.exp = left;
		} else {
			isLiteralNum = false;
			this.exp = new ast.exp.Sub(left, right, e.lineNum);
		}
	}

	@Override
	public void visit(ast.exp.This e) {
		this.exp = e;
	}

	@Override
	public void visit(ast.exp.Times e) {
		e.left.accept(this);
		ast.exp.T left = this.exp;
		boolean leftIsLiteralNum = this.isLiteralNum;
		int leftLiteralNum = this.literalNum;
		e.right.accept(this);
		ast.exp.T right = this.exp;
		boolean rightIsLiteralNum = this.isLiteralNum;
		int rightLiteralNum = this.literalNum;

		if (leftIsLiteralNum && rightIsLiteralNum) {
			isLiteralNum = true;
			literalNum = leftLiteralNum * rightLiteralNum;
			this.exp = new ast.exp.Num(literalNum, e.lineNum);
		} else if ((leftIsLiteralNum && leftLiteralNum == 0)
				|| (rightIsLiteralNum && rightLiteralNum == 0)) {
			isLiteralNum = true;
			this.exp = new ast.exp.Num(0, e.lineNum);
		} else if (leftIsLiteralNum && leftLiteralNum == 1) {
			this.exp = right;
		} else if (rightIsLiteralNum && rightLiteralNum == 1) {
			this.exp = left;
		} else {
			isLiteralNum = false;
			this.exp = new ast.exp.Times(left, right, e.lineNum);
		}
	}

	@Override
	public void visit(ast.exp.True e) {
		this.isLiteralBoolean = true;
		this.isTrue = true;
		this.exp = new ast.exp.True(e.lineNum);
	}

	// statements
	@Override
	public void visit(ast.stm.Assign s) {
		s.exp.accept(this);
		this.stm = new ast.stm.Assign(s.id, this.exp, s.lineNum);
	}

	@Override
	public void visit(ast.stm.AssignArray s) {
		s.index.accept(this);
		this.stm = new ast.stm.AssignArray(s.id, this.exp, s.exp);
	}

	@Override
	public void visit(ast.stm.Block s) {
		java.util.LinkedList<ast.stm.T> stms = new java.util.LinkedList<ast.stm.T>();
		for (ast.stm.T stm : stms) {
			stm.accept(this);
			stms.add(this.stm);
		}
		this.stm = new ast.stm.Block(stms);
	}

	@Override
	public void visit(ast.stm.If s) {
		this.isLiteralBoolean = false;
		s.condition.accept(this);
		ast.exp.T exp = this.exp;
		if (this.isLiteralBoolean) {
			if (this.isTrue)
				this.stm = s.thenn;
			else
				this.stm = s.elsee;
			return;
		}
		s.thenn.accept(this);
		ast.stm.T thenn = this.stm;
		s.elsee.accept(this);
		ast.stm.T elsee = this.stm;
		this.stm = new ast.stm.If(exp, thenn, elsee);
	}

	@Override
	public void visit(ast.stm.Print s) {
		s.exp.accept(this);
		this.stm = new ast.stm.Print(this.exp);
	}

	@Override
	public void visit(ast.stm.While s) {
		this.isTrue = true;// assume true
		this.isLiteralBoolean = false;
		s.condition.accept(this);
		if (this.isLiteralBoolean && this.isTrue == false) {
			this.stm = null;
			return;
		}
		ast.exp.T exp = this.exp;
		s.body.accept(this);

		this.stm = new ast.stm.While(exp, this.stm);
	}
	

	@Override
	public void visit(TryCatch s) {// my final project ,none business with Lab5
		return;
	}

	@Override
	public void visit(Throw s) {
		return;
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
		m.retExp.accept(this);
		this.mthods.add(new ast.method.Method(m.retType, m.id, m.formals,
				m.locals, stms, this.exp));
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

		if (control.Control.isTracing("ast.AlgSimp")) {
			System.out.println("before ast.AlgSimp optimization:");
			ast.PrettyPrintVisitor pp = new ast.PrettyPrintVisitor();
			p.accept(pp);
			System.out.println("after ast.AlgSimp optimization:");
			this.program.accept(pp);
		}
		return;
	}

	@Override
	public void visit(Paren e) {
		e.exp.accept(this);
		if (this.isLiteralNum) {//is Num?
			this.exp= new ast.exp.Num(this.literalNum);
		}else if (this.isLiteralBoolean) {//is Boolean
			if (isTrue) {
				this.exp= new ast.exp.True(e.lineNum);
			}else {
				this.exp= new ast.exp.False(e.lineNum);
			}
		}
		return;
	}

}
