package ast;

import java.util.LinkedList;

public class PrettyPrintVisitor implements Visitor
{
  private int indentLevel;

	public PrettyPrintVisitor() 
	{
		this.indentLevel = 0;
	}

  private void indent()
  {
    this.indentLevel += 2;
  }

  private void unIndent()
  {
    this.indentLevel -= 2;
  }

  private void printSpaces()
  {
    int i = this.indentLevel;
    while (i-- != 0)
      this.say(" ");
  }

  private void sayln(String s)
  {
    System.out.println(s);
  }

  private void say(String s)
  {
    System.out.print(s);
  }
  
  public void sayLoop4Args(LinkedList args)
  {
	  
	   int argc = args.size();
	   if(0 >= argc){
		   return;
	   }
	   Acceptable x = null;
	    for (int i = 0; i < argc-1; i++) {
	    	x = (Acceptable) args.get(i);
	    	x.accept(this);
	    	this.say(", ");
		}
	    ((Acceptable) args.get(argc-1)).accept(this);
  }

  // /////////////////////////////////////////////////////
  // expressions
  @Override
  public void visit(ast.exp.Add e)
  {
    // Lab2, exercise4: filling in missing code.
    // Similar for other methods with empty bodies.
    // Your code here:
	e.left.accept(this);
	this.say(" + ");
	e.right.accept(this);
  }

  @Override
  public void visit(ast.exp.And e)
  {
	  e.left.accept(this);
	  this.say(" && ");
	  e.right.accept(this);
  }

  @Override
  public void visit(ast.exp.ArraySelect e)
  {
	  e.array.accept(this);
	  this.say(" [");
	  e.index.accept(this);
	  this.say("]");
  }

  @Override
  public void visit(ast.exp.Call e)
  {
    e.exp.accept(this);
    this.say("." + e.id + "(");
    sayLoop4Args(e.args);
    /*for (ast.exp.T x : e.args) {
      x.accept(this);
      this.say(", ");
    }*/
    this.say(")");
    return;
  }

  @Override
  public void visit(ast.exp.False e)
  {
	  this.say("false");
  }

  @Override
  public void visit(ast.exp.Id e)
  {
    this.say(e.id);
  }

  @Override
  public void visit(ast.exp.Length e)
  {
	  e.array.accept(this);
	  this.say(" .length");
  }

  @Override
  public void visit(ast.exp.Lt e)
  {
    e.left.accept(this);
    this.say(" < ");
    e.right.accept(this);
    return;
  }

  @Override
  public void visit(ast.exp.NewIntArray e)
  {
	  this.say("new int[" );
	  e.exp.accept(this);
	  this.say("]");
  }

  @Override
  public void visit(ast.exp.NewObject e)
  {
    this.say("new " + e.id + "()");
    return;
  }

  @Override
  public void visit(ast.exp.Not e)
  {
	  this.say("!(");
	  e.exp.accept(this);
	  this.say(")");
  }

  @Override
  public void visit(ast.exp.Num e)
  {
    System.out.print(e.num);
    return;
  }

	// (exp)
	@Override
	public void visit(ast.exp.Paren e) 
	{
		this.say("(");
		e.exp.accept(this);
		this.say(")");
		return;
	}
	
	// left - right
	@Override
	public void visit(ast.exp.Sub e)
	{
		e.left.accept(this);
		this.say(" - ");
		e.right.accept(this);
		return;
	}

  @Override
  public void visit(ast.exp.This e)
  {
    this.say("this");
  }

  @Override
  public void visit(ast.exp.Times e)
  {
    e.left.accept(this);
    this.say(" * ");
    e.right.accept(this);
    return;
  }

  @Override
  public void visit(ast.exp.True e)
  {
	  this.say("true");
  }

  // statements
  @Override
  public void visit(ast.stm.Assign s)
  {
    this.printSpaces();
    this.say(s.id + " = ");
    s.exp.accept(this);
    this.sayln(";");
    return;
  }

	// id[index] = Exp;
	@Override
	public void visit(ast.stm.AssignArray s) 
	{
		this.printSpaces();
		this.say(s.id.id + "[");
		s.index.accept(this);
		this.say("] = ");
		s.exp.accept(this);
		this.say(";");
		this.sayln("");
		return;
	}

	// { Statement* }
	@Override
	public void visit(ast.stm.Block s) 
	{
		this.printSpaces();
		this.sayln("{");
		this.indent();
		for(ast.stm.T b : s.stms)
			b.accept(this);
		this.unIndent();
		this.printSpaces();
		this.say("}");
		this.sayln("");
		return;
	}

	// if(condition) 
	//		thenn 
	// else 
	//		elsee
	@Override
	public void visit(ast.stm.If s) 
	{
		this.printSpaces();
		this.say("if(");
		s.condition.accept(this);
		this.sayln(")");
		if(!s.thenn.getClass().toString().equals("class ast.stm.Block"))
		{
			this.indent();
			s.thenn.accept(this);
			this.unIndent();
			this.printSpaces();
			this.sayln("else");
			if(!s.elsee.getClass().toString().equals("class ast.stm.Block"))
			{
				this.indent();
				s.elsee.accept(this);
				this.unIndent();
			}
			else
			{
				s.elsee.accept(this);
			}
		}
		else
		{
			s.thenn.accept(this);
			this.printSpaces();
			this.sayln("else");
			if(!s.elsee.getClass().toString().equals("class ast.stm.Block"))
			{
				this.indent();
				s.elsee.accept(this);
				this.unIndent();
			}
			else
			{
				s.elsee.accept(this);
			}
		}
		return;
	}

  @Override
  public void visit(ast.stm.Print s)
  {
    this.printSpaces();
    this.say("System.out.println(");
    s.exp.accept(this);
    this.sayln(");");
    return;
  }

	// while(condition) 
	// 		body
	@Override
	public void visit(ast.stm.While s) 
	{
		this.printSpaces();
		this.say("while(");
		s.condition.accept(this);
		this.sayln(")");
		if(!s.body.getClass().toString().equals("class ast.stm.Block"))
		{
			this.indent();
			s.body.accept(this);
			this.unIndent();
		}
		else
		{
			s.body.accept(this);
		}
		return;
	}

	// type
	// boolean
	@Override
	public void visit(ast.type.Boolean t) 
	{
		this.say("boolean");
	}

	// id
	@Override
	public void visit(ast.type.Class t) 
	{
		this.say(t.id);
	}

  @Override
  public void visit(ast.type.Int t)
  {
    this.say("int");
  }

  @Override
  public void visit(ast.type.IntArray t)
  {
	  this.say("int[]");
  }

  // dec
  @Override
  public void visit(ast.dec.Dec d)
  {
	  d.type.accept(this);
	  this.say(" "+d.id);
  }

  // method
  @Override
  public void visit(ast.method.Method m)
  {
	this.printSpaces();
    this.say("public ");
    m.retType.accept(this);
    this.say(" " + m.id + "(");
    this.sayLoop4Args(m.formals);
    /*for (ast.dec.T d : m.formals) {
      ast.dec.Dec dec = (ast.dec.Dec) d;
      dec.type.accept(this);
      this.say(" " + dec.id + ", ");
    }*/
    this.sayln(")");
		this.printSpaces();
		this.sayln("{");

		this.indent();
		for(ast.dec.T d : m.locals)
		{
			this.printSpaces();
			d.accept(this);
			this.sayln(";");
		}
		if(!m.locals.isEmpty())
			this.sayln("");
		
		for(ast.stm.T s : m.stms)
			s.accept(this);
		
		this.printSpaces();
		this.say("return ");
		m.retExp.accept(this);
		this.say(";");
		this.sayln("");
		this.unIndent();
		this.printSpaces();
		this.sayln("}");
		return;
	}

	// Class -> class id 
	//			{ 
	//				VarDecl* 
	//				MethodDecl* 
	//			}
	// 		 -> class id extends id 
	//			{ 
	//				VarDecl* 
	//				MethodDecl* 
	//			}
	@Override
	public void visit(ast.classs.Class c) 
	{
		this.say("class " + c.id);
		if(c.extendss != null)
			this.sayln(" extends " + c.extendss);
		else
			this.sayln("");

		this.sayln("{");
		this.indent();
		for(ast.dec.T d : c.decs) 
		{
			this.printSpaces();
			d.accept(this);
			this.say(";");
			this.sayln("");
		}
		if(!c.decs.isEmpty())
			this.sayln("");
		
		for(ast.method.T mthd : c.methods)
			mthd.accept(this);
		
		this.unIndent();
		this.printSpaces();
		this.sayln("}");
		return;
	}

	// MainClass -> class id
    //              {
	//                	public static void main(String[] id )
	//                	{
	//                		Statement
	//                	}
	//               }
	@Override
	public void visit(ast.mainClass.MainClass c) 
	{
		this.sayln("class " + c.id);
		this.sayln("{");
		this.indent();
		this.printSpaces();
		this.sayln("public static void main(String[] " + c.arg + ")");
		this.printSpaces();
		this.sayln("{");
		this.indent();
		c.stm.accept(this);
		this.unIndent();
		this.printSpaces();
		this.sayln("}");
		this.unIndent();
		this.printSpaces();
		this.sayln("}");
		return;
	}

	// Program -> MainClass ClassDecl*
	@Override
	public void visit(ast.program.Program p) 
	{
		p.mainClass.accept(this);
		this.sayln("");
		for(ast.classs.T classs : p.classes) 
		{
			classs.accept(this);
		}
		this.sayln("");
		return;
	}
}
