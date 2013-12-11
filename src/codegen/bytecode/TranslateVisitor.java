package codegen.bytecode;

import java.util.Hashtable;
import java.util.LinkedList;

import util.Label;

// Given a Java ast, translate it into Java bytecode.

public class TranslateVisitor implements ast.Visitor 
{
	private String classId;
	private int index;
	private Hashtable<String, Integer> indexTable;
	private codegen.bytecode.type.T type;// type after translation
	private codegen.bytecode.dec.T dec;
	private LinkedList<codegen.bytecode.stm.T> stms;
	private codegen.bytecode.method.T method;
	private codegen.bytecode.classs.T classs;
	private codegen.bytecode.mainClass.T mainClass;
	public codegen.bytecode.program.T program;

	public TranslateVisitor() 
	{
		this.classId = null;
		this.indexTable = null;
		this.type = null;
		this.dec = null;
		this.stms = new LinkedList<codegen.bytecode.stm.T>();
		this.method = null;
		this.classs = null;
		this.mainClass = null;
		this.program = null;
	}

	private void emit(codegen.bytecode.stm.T s)
	{
		this.stms.add(s);
	}

	// /////////////////////////////////////////////////////
	// expressions
	// left + right
	@Override
	public void visit(ast.exp.Add e) 
	{
		e.left.accept(this);
		e.right.accept(this);
		this.emit(new codegen.bytecode.stm.Iadd());
		return;
	}

	// left && right
	@Override
	public void visit(ast.exp.And e) 
	{
		e.left.accept(this);
		e.right.accept(this);
		this.emit(new codegen.bytecode.stm.Iand());
		return;
	}

	// array[index]
	@Override
	public void visit(ast.exp.ArraySelect e) 
	{
		e.array.accept(this);
		e.index.accept(this);
		this.emit(new codegen.bytecode.stm.IAload());
		return;
	}

	// exp.id(expList)
	@Override
	public void visit(ast.exp.Call e) 
	{
		e.exp.accept(this);
		for(ast.exp.T x : e.args) 
		{
			x.accept(this);
		}
		e.rt.accept(this);
		codegen.bytecode.type.T rt = this.type;
		
		LinkedList<codegen.bytecode.type.T> at = new LinkedList<codegen.bytecode.type.T>();
		
		for(ast.type.T t : e.at) 
		{
			t.accept(this);
			at.add(this.type);
		}
		this.emit(new codegen.bytecode.stm.Invokevirtual(e.id, e.type, at, rt));
		return;
	}

	// false
	@Override
	public void visit(ast.exp.False e) 
	{
		this.emit(new codegen.bytecode.stm.Ldc(0));
		return;
	}

	// id
	@Override
	public void visit(ast.exp.Id e) 
	{
		if(e.isField)
		{
			this.emit(new codegen.bytecode.stm.Aload(0));
			e.type.accept(this);
			this.emit(new codegen.bytecode.stm.Getfield(e.className, e.id, this.type));
		}
		else
		{
			int index = this.indexTable.get(e.id);
			ast.type.T type = e.type;
			if(type.getNum() > 0)// a reference
				this.emit(new codegen.bytecode.stm.Aload(index));
			else
				this.emit(new codegen.bytecode.stm.Iload(index));
		}
		return;
	}

	// array.length
	@Override
	public void visit(ast.exp.Length e) 
	{
		e.array.accept(this);
		this.emit(new codegen.bytecode.stm.ArrayLength());
		return;
	}

	// left < right
	@Override
	public void visit(ast.exp.Lt e) 
	{
		Label tl = new Label(), fl = new Label(), el = new Label();
		e.left.accept(this);
		e.right.accept(this);
		this.emit(new codegen.bytecode.stm.Ificmplt(tl));
		
		this.emit(new codegen.bytecode.stm.Label(fl));
		this.emit(new codegen.bytecode.stm.Ldc(0));
		this.emit(new codegen.bytecode.stm.Goto(el));
		
		this.emit(new codegen.bytecode.stm.Label(tl));
		this.emit(new codegen.bytecode.stm.Ldc(1));
		this.emit(new codegen.bytecode.stm.Goto(el));
		
		this.emit(new codegen.bytecode.stm.Label(el));
		return;
	}

	// new int[exp]
	@Override
	public void visit(ast.exp.NewIntArray e) 
	{
		e.exp.accept(this);
		this.emit(new codegen.bytecode.stm.NewArray());
		return;
	}

	// new id()
	@Override
	public void visit(ast.exp.NewObject e)
	{
		this.emit(new codegen.bytecode.stm.New(e.id));
		return;
	}

	// !exp
	@Override
	public void visit(ast.exp.Not e)
	{
		Label tl = new Label(), el = new Label();
		e.exp.accept(this);
		this.emit(new codegen.bytecode.stm.Ifne(tl));
		
		this.emit(new codegen.bytecode.stm.Ldc(1));
		this.emit(new codegen.bytecode.stm.Goto(el));
		
		this.emit(new codegen.bytecode.stm.Label(tl));
		this.emit(new codegen.bytecode.stm.Ldc(0));
		
		this.emit(new codegen.bytecode.stm.Label(el));
		return;
	}

	@Override
	public void visit(ast.exp.Num e) 
	{
		this.emit(new codegen.bytecode.stm.Ldc(e.num));
		return;
	}

	// (exp)
	@Override
	public void visit(ast.exp.Paren e) 
	{
		e.exp.accept(this);
		return;
	}

	// left - right
	@Override
	public void visit(ast.exp.Sub e) 
	{
		e.left.accept(this);
		e.right.accept(this);
		this.emit(new codegen.bytecode.stm.Isub());
		return;
	}

	// this
	@Override
	public void visit(ast.exp.This e) 
	{
		this.emit(new codegen.bytecode.stm.Aload(0));
		return;
	}

	// left * right
	@Override
	public void visit(ast.exp.Times e)
	{
		e.left.accept(this);
		e.right.accept(this);
		this.emit(new codegen.bytecode.stm.Imul());
		return;
	}

	// true
	@Override
	public void visit(ast.exp.True e) 
	{
		this.emit(new codegen.bytecode.stm.Ldc(1));
		return;
	}

	// statements
	// id = Exp;
	@Override
	public void visit(ast.stm.Assign s) 
	{
		if(s.id.isField)
		{
			this.emit(new codegen.bytecode.stm.Aload(0));
		}
		s.exp.accept(this);
		if(s.id.isField)
		{
			s.id.type.accept(this);
			this.emit(new codegen.bytecode.stm.Putfield(s.id.className, s.id.id, this.type));
		}
		else
		{
			int index = this.indexTable.get(s.id.id);
			ast.type.T type = s.type;
			if(type.getNum() > 0)
				this.emit(new codegen.bytecode.stm.Astore(index));
			else
				this.emit(new codegen.bytecode.stm.Istore(index));
		}
		return;
	}

	// id[index] = Exp;
	@Override
	public void visit(ast.stm.AssignArray s) 
	{
		s.id.accept(this);
		s.index.accept(this);
		s.exp.accept(this);
		this.emit(new codegen.bytecode.stm.IAstore());
		return;
	}

	// { Statement* }
	@Override
	public void visit(ast.stm.Block s) 
	{
		for(ast.stm.T b : s.stms)
			b.accept(this);
		return;
	}

	// if(condition) 
	//		thenn 
	// else 
	//		elsee
	@Override
	public void visit(ast.stm.If s) 
	{
		Label tl = new Label(), fl = new Label(), el = new Label();
		s.condition.accept(this);
		this.emit(new codegen.bytecode.stm.Ifne(tl));
		
		this.emit(new codegen.bytecode.stm.Label(fl));
		s.elsee.accept(this);
		this.emit(new codegen.bytecode.stm.Goto(el));
		
		this.emit(new codegen.bytecode.stm.Label(tl));
		s.thenn.accept(this);
		this.emit(new codegen.bytecode.stm.Goto(el));
		
		this.emit(new codegen.bytecode.stm.Label(el));
		return;
	}

	// System.out.println(Exp);
	@Override
	public void visit(ast.stm.Print s) 
	{
		s.exp.accept(this);
		this.emit(new codegen.bytecode.stm.Print());
		return;
	}

	// while(condition) 
	// 		body
	@Override
	public void visit(ast.stm.While s) 
	{
		Label tl = new Label(), fl = new Label(), el = new Label();
		
		this.emit(new codegen.bytecode.stm.Label(tl));
		s.condition.accept(this);
		this.emit(new codegen.bytecode.stm.Ifne(fl));
		this.emit(new codegen.bytecode.stm.Goto(el));
		
		this.emit(new codegen.bytecode.stm.Label(fl));
		s.body.accept(this);
		this.emit(new codegen.bytecode.stm.Goto(tl));
		
		this.emit(new codegen.bytecode.stm.Label(el));
		return;
	}

	// type
	// boolean
	@Override
	public void visit(ast.type.Boolean t) 
	{
		this.type = new codegen.bytecode.type.Int();
		return;
	}

	@Override
	public void visit(ast.type.Class t) 
	{
		this.type = new codegen.bytecode.type.Class(t.id);
		return;
	}

	// int
	@Override
	public void visit(ast.type.Int t) 
	{
		this.type = new codegen.bytecode.type.Int();
		return;
	}

	// int[]
	@Override
	public void visit(ast.type.IntArray t) 
	{
		this.type = new codegen.bytecode.type.IntArray();
		return;
	}

	// Dec -> Type id
	@Override
	public void visit(ast.dec.Dec d)
	{
		d.type.accept(this);
		this.dec = new codegen.bytecode.dec.Dec(this.type, d.id);
		if(d.isField)
			return;
		this.indexTable.put(d.id, index++);
		return;
	}

	// Method -> public Type id(FormalList)
	//           { 
	//				VarDecl* 
	//				Statement* 
	//				return Exp ;
	//			 }
	@Override
	public void visit(ast.method.Method m) 
	{
		// record, in a hash table, each var's index
		// this index will be used in the load store operation
		this.index = 1;
		this.indexTable = new Hashtable<String, Integer>();

		m.retType.accept(this);
		codegen.bytecode.type.T newRetType = this.type;
		
		LinkedList<codegen.bytecode.dec.T> newFormals = new LinkedList<codegen.bytecode.dec.T>();
		for(ast.dec.T d : m.formals) 
		{
			d.accept(this);
			newFormals.add(this.dec);
		}
		
		LinkedList<codegen.bytecode.dec.T> locals = new LinkedList<codegen.bytecode.dec.T>();
		for(ast.dec.T d : m.locals) 
		{
			d.accept(this);
			locals.add(this.dec);
		}
		
		this.stms = new java.util.LinkedList<codegen.bytecode.stm.T>();
		for(ast.stm.T s : m.stms) 
		{
			s.accept(this);
		}

		// return statement is specially treated
		m.retExp.accept(this);

		if(m.retType.getNum() > 0)
			emit(new codegen.bytecode.stm.Areturn());
		else
			emit(new codegen.bytecode.stm.Ireturn());

		this.method = new codegen.bytecode.method.Method(newRetType, m.id, 
				this.classId, newFormals, locals, this.stms, 0, this.index);

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
		this.classId = c.id;
		
		LinkedList<codegen.bytecode.dec.T> newDecs = new LinkedList<codegen.bytecode.dec.T>();
		for(ast.dec.T dec : c.decs) 
		{
			dec.accept(this);
			newDecs.add(this.dec);
		}
		
		LinkedList<codegen.bytecode.method.T> newMethods = new LinkedList<codegen.bytecode.method.T>();
		for(ast.method.T m : c.methods) 
		{
			m.accept(this);
			newMethods.add(this.method);
		}
		
		this.classs = new codegen.bytecode.classs.Class(c.id, c.extendss, newDecs, newMethods);
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
		c.stm.accept(this);
		
		this.mainClass = new codegen.bytecode.mainClass.MainClass(c.id, c.arg, this.stms);
		
		this.stms = new LinkedList<codegen.bytecode.stm.T>();
		return;
	}
	
	// Program -> MainClass ClassDecl*
	@Override
	public void visit(ast.program.Program p) 
	{
		// do translations
		p.mainClass.accept(this);

		LinkedList<codegen.bytecode.classs.T> newClasses = new LinkedList<codegen.bytecode.classs.T>();
		for(ast.classs.T classs : p.classes) 
		{
			classs.accept(this);
			newClasses.add(this.classs);
		}
		
		this.program = new codegen.bytecode.program.Program(this.mainClass, newClasses);
		return;
	}
}