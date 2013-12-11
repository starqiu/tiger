package elaborator;

import java.util.Iterator;
import java.util.LinkedList;

public class ElaboratorVisitor implements ast.Visitor 
{
	public ClassTable classTable; // symbol table for class
	public MethodTable methodTable; // symbol table for each method
	public String currentClass; // the class name being elaborated
	public ast.type.T type; // type of the expression being elaborated

	public enum Kind
	{
		ERROR_BOOL,		 	// the type should be boolean
		ERROR_BOTHBOOL,  	// the types of both sides should be boolean
		ERROR_BOTHINT,   	// the types of both sides should be int
		ERROR_INT,       	// the type should be int
		ERROR_INTARRAY,  	// the type should be int array
		ERROR_NOCLASS,		// the type does not exist
		ERROR_NOTINIT,		// the local variable shoule be initialized
		ERROR_NOMETHOD,     // the class does not have the method
		ERROR_NUMARGS, 		// the number of arguments does not match
		ERROR_OBJECT,		// the callee should be an object
		ERROR_RETURN,       // the return type does not match
		ERROR_TYPEARGS,		// the types of arguments do not match
		ERROR_UNDECLARED,   // undeclared id
	}
	
	public ElaboratorVisitor() 
	{
		this.classTable = new ClassTable();
		this.currentClass = null;
		this.type = null;
	}

	private void error(Kind kind, int lineNum)
	{
		System.out.print("error: at line: " + lineNum + ": ");
		switch(kind)
		{
			case ERROR_BOOL:
				System.out.println("the type should be boolean");
				break;
			case ERROR_BOTHBOOL:
				System.out.println("the types of both sides of the operator should be boolean");
				break;
			case ERROR_BOTHINT:
				System.out.println("the types of both sides of the operator should be int");
				break;
			case ERROR_INT:
				System.out.println("the type should be int");
				break;
			case ERROR_INTARRAY:
				System.out.println("the type of the array should be int array");
				break;
			case ERROR_NOCLASS:
				System.out.println("the type does not exist");
				break;
			case ERROR_NOTINIT:
				System.out.println("the local variable should be initialize");
				break;
			case ERROR_NOMETHOD:
				System.out.println("the class does not have the method");
				break;
			case ERROR_NUMARGS:
				System.out.println("the number of arguments does not match");
				break;
			case ERROR_OBJECT:
				System.out.println("the callee should be an object");
				break;
			case ERROR_RETURN:
				System.out.println("the return type does not match");
				break;
			case ERROR_TYPEARGS:
				System.out.println("the types of arguments do not match");
				break;
			case ERROR_UNDECLARED:
				System.out.println("undeclared id");
				break;
			default:
				break;
		}
	}
	
	private void warnClass()
	{
		Iterator<String> itc = this.classTable.getTable().keySet().iterator();
		while(itc.hasNext())
		{
			String key = (String)itc.next();
			ClassBinding value = (ClassBinding)this.classTable.getTable().get(key);
			
			Iterator<String> itv = value.fields.keySet().iterator();
			while(itv.hasNext())
			{
				String id = (String)itv.next();
				VariableBinding vb = value.fields.get(id);
				
				if(!vb.isInitialized && !vb.isUsed)
					System.out.println("warn: at line: " + vb.lineNum + ": the variable \"" + id + "\" is never used");
			}
		}
	}
	
	private void warnMethod()
	{
		Iterator<String> it = this.methodTable.getTable().keySet().iterator();
		while(it.hasNext())
		{
			String key = (String)it.next();
			VariableBinding value = (VariableBinding)this.methodTable.getTable().get(key);
			
			if(!value.isInitialized && !value.isUsed)
				System.out.println("warn: at line: " + value.lineNum + ": the variable \"" + key + "\" is never used");
		}
	}
	
	// /////////////////////////////////////////////////////
	// expressions
	// left + right
	@Override
	public void visit(ast.exp.Add e)
	{
		e.left.accept(this);
		ast.type.T leftty = this.type;
		e.right.accept(this);
		
		if(leftty != null && this.type != null)// if the left and right exist
		{
			if(!leftty.toString().equals("@int") && !this.type.toString().equals("@int"))
				this.error(Kind.ERROR_BOTHINT, e.lineNum);
			else if(!leftty.toString().equals("@int") || !this.type.toString().equals("@int"))
				this.error(Kind.ERROR_BOTHINT, e.lineNum);
		}
		
		this.type = new ast.type.Int();
		return;
	}

	// left && right
	@Override
	public void visit(ast.exp.And e)
	{
		e.left.accept(this);
		ast.type.T leftty = this.type;
		e.right.accept(this);

		if(leftty != null && this.type != null)// if the left and right exist
		{
			if(!leftty.toString().equals("@boolean") && !this.type.toString().equals("@boolean"))
				this.error(Kind.ERROR_BOTHBOOL, e.lineNum);
			else if(!leftty.toString().equals("@boolean") || !this.type.toString().equals("@boolean"))
				this.error(Kind.ERROR_BOTHBOOL, e.lineNum);
		}
		
		this.type = new ast.type.Boolean();
		return;
	}

	// array[index]
	@Override
	public void visit(ast.exp.ArraySelect e)
	{
		e.array.accept(this);
		
		if(this.type != null)// if array exists
		{
			if(!this.type.toString().equals("@int[]"))
				this.error(Kind.ERROR_INTARRAY, e.lineNum);
			
			e.index.accept(this);
			if(this.type != null && !this.type.toString().equals("@int"))
				this.error(Kind.ERROR_INT, e.lineNum);
		}
		
		this.type = new ast.type.Int();
		return;
	}

	// exp.id(expList)
	@Override
	public void visit(ast.exp.Call e) 
	{
		ast.type.T leftty;
		ast.type.Class ty = null;

		e.exp.accept(this);
		leftty = this.type;
		
		if(this.type != null)// if exp exists
		{
			if(leftty instanceof ast.type.Class) 
			{
				ty = (ast.type.Class)leftty;
				e.type = ty.id;
				
				ClassBinding cbb = this.classTable.get(ty.id);
				if(cbb == null)// if the class does not exist
				{
					this.type = null;
					this.error(Kind.ERROR_NOCLASS, e.lineNum);
				}
				else// if the class exists, then find the method of it
				{
					MethodType mty = this.classTable.getm(ty.id, e.id);
					if(mty == null)// if the method does not exist
					{
						this.type = null;
						this.error(Kind.ERROR_NOMETHOD, e.lineNum);
					}
					else// if the method exists
					{
						LinkedList<ast.type.T> argsty = new LinkedList<ast.type.T>();
						for(ast.exp.T a : e.args)// actual arguments of call 
						{
							a.accept(this);
							argsty.addLast(this.type);
						}
						
						LinkedList<ast.type.T> declaredArgsty = new LinkedList<ast.type.T>();
						for(ast.dec.T d: mty.argsType)// declared arguments of call
						{
							declaredArgsty.add(((ast.dec.Dec)d).type);
						}
						
						if(declaredArgsty.size() != argsty.size())// if the number of arguments does not match
							this.error(Kind.ERROR_NUMARGS, e.lineNum);
						else// if match, then check the types
						{
							for(int i = 0; i < argsty.size(); i++) 
							{
								ast.type.T actual = argsty.get(i);
								ast.type.T declared = declaredArgsty.get(i);
								//judge whether actual is the subclass of declared or not
								if(declared instanceof ast.type.Class && actual instanceof ast.type.Class)
								{
									String actualStr = actual.toString();
									ClassBinding cb = this.classTable.get(actualStr);
									while(cb.extendss != null)
									{
										actualStr = cb.extendss;
										if(!declared.toString().equals(cb.extendss))
											cb = this.classTable.get(cb.extendss);
										else
											break;
									}
									if(!declared.toString().equals(actualStr))
										this.error(Kind.ERROR_TYPEARGS, e.lineNum);
								}
								else
								{
									if(!declared.toString().equals(actual.toString()))// if the types of arguments do not match
										this.error(Kind.ERROR_TYPEARGS, e.lineNum);
								}
							}
						}
					
						this.type = mty.retType;
						e.at = declaredArgsty;
						e.rt = this.type;
					}
				}
			} 
			else
			{
				this.type = null;
				this.error(Kind.ERROR_OBJECT, e.lineNum);
			}
		}
		
		return;
	}

	// false
	@Override
	public void visit(ast.exp.False e)
	{
		this.type = new ast.type.Boolean();
		return;
	}

	// id
	@Override
	public void visit(ast.exp.Id e) 
	{
		StringBuffer className = new StringBuffer(this.currentClass);
		
		// first look up the id in method table
		ast.type.T type = this.methodTable.get(e.id);
		if(type == null)// if search failed, then e.id may be a class field
		{
			type = this.classTable.get(className, e.id);
			if(type == null)
				this.error(Kind.ERROR_UNDECLARED, e.lineNum);
			else
			{
				// mark this id as a field id, this fact will be useful in later phase
				e.isField = true;
				e.className = className.toString();
			}
		}
		
		if(type != null)// if the id exists
		{
			e.type = type;
			if(!e.isField)// if the id is the local variable of a method
			{
				e.className = this.currentClass;
				if(!this.methodTable.getInit(e.id))
					this.error(Kind.ERROR_NOTINIT, e.lineNum);
				else
					this.methodTable.putUse(e.id, true);
			}
			else if(e.isField && !this.classTable.getUse(className.toString(), e.id))// if the id is the field of a class
				this.classTable.putUse(className.toString(), e.id, true);
		}
		
		this.type = type;
		// record this type on this node for future use.
		e.type = type;
		return;
	}

	// array.length
	@Override
	public void visit(ast.exp.Length e)
	{
		e.array.accept(this);
		
		if(this.type != null)// if the array exists
		{
			if(!this.type.toString().equals("@int[]"))
				this.error(Kind.ERROR_INTARRAY, e.lineNum);
		}
		
		this.type = new ast.type.Int();
		return;
	}

	// left < right
	@Override
	public void visit(ast.exp.Lt e) 
	{
		e.left.accept(this);
		ast.type.T leftty = this.type;
		e.right.accept(this);
		
		if(leftty != null && this.type != null)// if the left and right exist
		{
			if(!leftty.toString().equals("@int") && !this.type.toString().equals("@int"))
				this.error(Kind.ERROR_BOTHINT, e.lineNum);
			else if(!leftty.toString().equals("@int") || !this.type.toString().equals("@int"))
				this.error(Kind.ERROR_BOTHINT, e.lineNum);
		}
		
		this.type = new ast.type.Boolean();
		return;
	}

	// new int[exp]
	@Override
	public void visit(ast.exp.NewIntArray e) 
	{
		e.exp.accept(this);
		
		if(this.type != null)
		{
			if(!this.type.toString().equals("@int"))
				this.error(Kind.ERROR_INT, e.lineNum);
		}
		
		this.type = new ast.type.IntArray();
		return;
	}

	// new id()
	@Override
	public void visit(ast.exp.NewObject e)
	{
		this.type = new ast.type.Class(e.id);
		return;
	}

	// !exp
	@Override
	public void visit(ast.exp.Not e) 
	{
		e.exp.accept(this);
		
		if(this.type != null)
		{
			if(!this.type.toString().equals("@boolean"))
				this.error(Kind.ERROR_BOOL, e.lineNum);
		}
		
		this.type = new ast.type.Boolean();
		return;
	}

	@Override
	public void visit(ast.exp.Num e) 
	{
		this.type = new ast.type.Int();
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
		ast.type.T leftty = this.type;
		e.right.accept(this);

		if(leftty != null && this.type != null)// if the left and right exist
		{
			if(!leftty.toString().equals("@int") && !this.type.toString().equals("@int"))
				this.error(Kind.ERROR_BOTHINT, e.lineNum);
			else if(!leftty.toString().equals("@int") || !this.type.toString().equals("@int"))
				this.error(Kind.ERROR_BOTHINT, e.lineNum);
		}
		
		this.type = new ast.type.Int();
		return;
	}

	// this
	@Override
	public void visit(ast.exp.This e) 
	{
		this.type = new ast.type.Class(this.currentClass);
		return;
	}

	// left * right
	@Override
	public void visit(ast.exp.Times e)
	{
		e.left.accept(this);
		ast.type.T leftty = this.type;
		e.right.accept(this);

		if(leftty != null && this.type != null)// if the left and right exist
		{
			if(!leftty.toString().equals("@int") && !this.type.toString().equals("@int"))
				this.error(Kind.ERROR_BOTHINT, e.lineNum);
			else if(!leftty.toString().equals("@int") || !this.type.toString().equals("@int"))
				this.error(Kind.ERROR_BOTHINT, e.lineNum);
		}
		
		this.type = new ast.type.Int();
		return;
	}

	// true
	@Override
	public void visit(ast.exp.True e)
	{
		this.type = new ast.type.Boolean();
		return;
	}

	// statements
	// id = Exp;
	@Override
	public void visit(ast.stm.Assign s)
	{
		StringBuffer className = new StringBuffer(this.currentClass);
		
		// first look up the id in method table
		ast.type.T type = this.methodTable.get(s.id.id);
		// if search failed, then s.id may be a class field
		if(type == null)
		{
			type = this.classTable.get(className, s.id.id);
			if(type == null)
				this.error(Kind.ERROR_UNDECLARED, s.exp.lineNum);
			else// if the id is found in the ClassTable, then it is a field
			{
				s.id.isField = true;
				s.id.className = className.toString();
			}
		}
		
		s.exp.accept(this);
		
		if(type != null)// if id exists
		{	
			s.type = type;
			s.id.type = type;
			
			if(this.type != null)
			{
				if(type.toString().equals("@int") && !this.type.toString().equals("@int"))
					this.error(Kind.ERROR_BOTHINT, s.exp.lineNum);
				else if(type.toString().equals("@boolean") && !this.type.toString().equals("@boolean"))
					this.error(Kind.ERROR_BOTHBOOL, s.exp.lineNum);
				
				if(!s.id.isField)
				{
					s.id.className = this.currentClass;
					if(!this.methodTable.getInit(s.id.id))
						this.methodTable.putInit(s.id.id, true);
				}
				else if(s.id.isField && !this.classTable.getUse(className.toString(), s.id.id))
					this.classTable.putUse(className.toString(), s.id.id, true);
			}
		}
		
		this.type = type;
		return;
	}

	// id[index] = Exp;
	@Override
	public void visit(ast.stm.AssignArray s)
	{
		StringBuffer className = new StringBuffer(this.currentClass);
		ast.type.T type = this.methodTable.get(s.id.id);
		if(type == null)
		{
			type = this.classTable.get(className, s.id.id);
			if(type == null)
				this.error(Kind.ERROR_UNDECLARED, s.exp.lineNum);
			else
			{
				s.id.isField = true;
				s.id.className = className.toString();
			}
		}
		
		if(type != null)// if id exists
		{
			s.id.type = type;
			if(!type.toString().equals("@int[]"))
				this.error(Kind.ERROR_INTARRAY, s.exp.lineNum);
		
			s.index.accept(this);
			if(this.type != null)
			{
				if(!this.type.toString().equals("@int"))
					this.error(Kind.ERROR_INT, s.exp.lineNum);
		
				s.exp.accept(this);
				if(this.type != null)
				{
					if(!this.type.toString().equals("@int"))
						this.error(Kind.ERROR_BOTHINT, s.exp.lineNum);
		
					if(!s.id.isField)
					{
						s.id.className = this.currentClass;
						if(!this.methodTable.getInit(s.id.id))
							this.methodTable.putInit(s.id.id, true);
					}
				}
			}
		}
		
		this.type= new ast.type.Int();
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
		s.condition.accept(this);
		
		if(this.type != null)
		{
			if(!this.type.toString().equals("@boolean"))
				this.error(Kind.ERROR_BOOL, s.condition.lineNum);
		}
		
		s.thenn.accept(this);
		s.elsee.accept(this);
		
		return;
	}

	// System.out.println(Exp);
	@Override
	public void visit(ast.stm.Print s) 
	{
		s.exp.accept(this);
		
		if(this.type != null)
		{
			if(!this.type.toString().equals("@int"))
				this.error(Kind.ERROR_INT, s.exp.lineNum);
		}
		
		return;
	}

	// while(condition) 
	// 		body
	@Override
	public void visit(ast.stm.While s)
	{
		s.condition.accept(this);
		
		if(this.type != null)
		{
			if(!this.type.toString().equals("@boolean"))
				this.error(Kind.ERROR_BOOL, s.condition.lineNum);
		}
		
		s.body.accept(this);
		
		return;
	}

	// type
	// boolean
	@Override
	public void visit(ast.type.Boolean t) 
	{
	}

	@Override
	public void visit(ast.type.Class t) 
	{
	}

	// int
	@Override
	public void visit(ast.type.Int t) 
	{
	}

	// int[]
	@Override
	public void visit(ast.type.IntArray t) 
	{
	}

	// Dec -> Type id
	@Override
	public void visit(ast.dec.Dec d) 
	{
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
		// construct the method table
		this.methodTable = new MethodTable();
		this.methodTable.put(m.formals, m.locals);
		if(control.Control.elabMethodTable)
		{
			System.out.println(m.id + "():");
			this.methodTable.dump();
		}
		
		for(ast.stm.T s : m.stms)
			s.accept(this);
		
		m.retExp.accept(this);
		
		if(this.type != null && !this.type.toString().equals(m.retType.toString()))
			this.error(Kind.ERROR_RETURN, m.retExp.lineNum);
		
		this.warnMethod();
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
		this.currentClass = c.id;

		for(ast.method.T m : c.methods) 
			m.accept(this);
		
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
		this.currentClass = c.id;
		// "main" has an argument "arg" of type "String[]", but
		// one has no chance to use it. So it's safe to skip it...
		c.stm.accept(this);
		return;
	}

	// ////////////////////////////////////////////////////////
	// step 1: build class table
	// class table for main class
	private void buildMainClass(ast.mainClass.MainClass main)
	{
		this.classTable.put(main.id, new ClassBinding(null));
	}

	// class table for normal classes
	private void buildClass(ast.classs.Class c) 
	{
		this.classTable.put(c.id, new ClassBinding(c.extendss));
		
		for(ast.dec.T dec : c.decs) 
		{
			ast.dec.Dec d = (ast.dec.Dec)dec;
			this.classTable.put(c.id, d.id, new VariableBinding(d.type, false, d.lineNum));
		}
		
		for(ast.method.T method : c.methods) 
		{
			ast.method.Method m = (ast.method.Method)method;
			this.classTable.put(c.id, m.id,	new MethodType(m.retType, m.formals));
		}
	}

	// step 1: end
	// ///////////////////////////////////////////////////

	// Program -> MainClass ClassDecl*
	@Override
	public void visit(ast.program.Program p) 
	{
		// ////////////////////////////////////////////////
		// step 1: build a symbol table for class (the class table)
		// a class table is a mapping from class names to class bindings
		// classTable: className -> ClassBinding{extends, fields, methods}
		buildMainClass((ast.mainClass.MainClass)p.mainClass);
		
		for(ast.classs.T c : p.classes) 
		{
			buildClass((ast.classs.Class)c);
		}
		
		// we can double check that the class table is OK!
		if(control.Control.elabClassTable) 
		{
			this.classTable.dump();
		}
		
		// ////////////////////////////////////////////////
		// step 2: elaborate each class in turn, under the class table
		// built above.
		p.mainClass.accept(this);
		
		for(ast.classs.T c : p.classes)
		{
			c.accept(this);
		}
		
		this.warnClass();
	}
}