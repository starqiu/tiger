package elaborator;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ast.Acceptable;
import ast.exp.NewIntArray;
import ast.type.Boolean;
import ast.type.Int;
import ast.type.IntArray;


public class ElaboratorVisitor implements ast.Visitor
{
  public ClassTable classTable; // symbol table for class
  public MethodTable methodTable; // symbol table for each method
  public String currentClass; // the class name being elaborated
  public ast.type.T type; // type of the expression being elaborated
  public int errNo = 1;//error NO.

  public ElaboratorVisitor()
  {
    this.classTable = new ClassTable();
    this.methodTable = new MethodTable();
    this.currentClass = null;
    this.type = null;
  }

  private void error()
  {
    System.err.println("type mismatch");
    //System.exit(1);
  }
  private void error(String errMsg, Acceptable e){
	  String acceptMsg = e.toString();
	  if (e.toString().length() >20) {
		acceptMsg =e.toString().substring(0, 20)+"...";
	  }
	  System.err.println("Error"+errNo+" : "+errMsg +"at class "+this.currentClass
			  +" the error code is "+ acceptMsg);
	  errNo++;
  }
  
  private void warn(String fieldOrMethod,String type,String id){
	  System.out.println("warning: " + fieldOrMethod + type + " "+id+" is never used!");
  }

  // /////////////////////////////////////////////////////
  // expressions
  @Override
  public void visit(ast.exp.Add e)
  {
	 e.left.accept(this);
	 if (!(this.type instanceof Int)) {
		error("the left expression of Add operator should return int type!",e);
	 }
	 e.right.accept(this);
	 if (!(this.type instanceof Int)) {
		 error("the right expression of Add operator should return int type!",e);
	 }
	 this.type = new Int();
  }

  @Override
  public void visit(ast.exp.And e)
  {
	 e.left.accept(this);
	 if (!(this.type instanceof Boolean)) {
		error("the left expression of And operator should return boolean type!",e);
	 }
	 e.right.accept(this);
	 if (!(this.type instanceof Boolean)) {
		 error("the right expression of And operator should return boolean type!",e);
	 }
	 this.type = new Boolean();
  }

  @Override
  public void visit(ast.exp.ArraySelect e)
  {
  	 e.array.accept(this);
	 if (!(this.type instanceof IntArray)) {
		error("the array of ArraySelect operator should return IntArray type!",e);
	 }
	 e.index.accept(this);
	 if (!(this.type instanceof Int)) {
		 error("the index expression of ArraySelect operator should return int type!",e);
	 }
	 this.type = new Int();
  }

  @Override
  public void visit(ast.exp.Call e)
  {
	  ast.type.T leftty;
	    ast.type.Class ty = null;

	    e.exp.accept(this);
	    leftty = this.type;
	    if (leftty instanceof ast.type.Class) {
	      ty = (ast.type.Class) leftty;
	      e.type = ty.id;
	    } else
	      error();
	    MethodType mty = this.classTable.getm(ty.id, e.id);
	    java.util.LinkedList<ast.type.T> declaredArgTypes
	    = new java.util.LinkedList<ast.type.T>();
	    for (ast.dec.T dec: mty.argsType){
	      declaredArgTypes.add(((ast.dec.Dec)dec).type);
	    }
	    java.util.LinkedList<ast.type.T> argsty = new java.util.LinkedList<ast.type.T>();
	    for (ast.exp.T a : e.args) {
	      a.accept(this);
	      argsty.addLast(this.type);
	    }
	    if (declaredArgTypes.size() != argsty.size())
	      error("the number of actual and formal arguments is different!",e);
	    // be the same");
	    // For now, the following code only checks that
	    // the types for actual and formal arguments should
	    // be the same. However, in MiniJava, the actual type
	    // of the parameter can also be a subtype (sub-class) of the 
	    // formal type. That is, one can pass an object of type "A"
	    // to a method expecting a type "B", whenever type "A" is
	    // a sub-class of type "B".
	    // Modify the following code accordingly:
	    String typeStr = null;
	    for (int i = 0; i < argsty.size(); i++) {
	      typeStr = argsty.get(i).toString();
	      if (declaredArgTypes.get(i).toString().equals(typeStr)){
	    	  continue;
	      }else{
	    	  //To see whether declaredArgType is argsty's super class
	    	  ClassBinding cb = this.classTable.get(typeStr);
	    	  if (null == cb) {//not a class
	    		  error("the type for actual and formal arguments is different!",e);
	    	  }
	    	  while(null != typeStr){
	    		  if (declaredArgTypes.get(i).toString().equals(typeStr)){
	    	    	  continue;
	    	      }else {
	    	    	  typeStr = cb.extendss;
	    	      }
	    	  }
	    	  //typeStr is null
	    	  error("the type for actual and formal arguments is different!",e);
	      }
	    }
	    this.type = mty.retType;
	    // the following two types should be the declared types.
	    e.at = declaredArgTypes;
	    e.rt = this.type;
	    return;
  }

  @Override
  public void visit(ast.exp.False e)
  {
	  this.type = new Boolean();
  }

  @Override
  public void visit(ast.exp.Id e)
  {
    // first look up the id in method table
    ast.type.T type = this.methodTable.get(e.id);
    // if search failed, then s.id must be a class field.
    if (type == null) {
      type = this.classTable.get(this.currentClass, e.id);
      // mark this id as a field id, this fact will be
      // useful in later phase.
      e.isField = true;
    }
    if (type == null)
      error("can not find any declaration about "+e.id+" in project!",e);
    this.type = type;
    // record this type on this node for future use.
    e.type = type;
    return;
  }

  @Override
  public void visit(ast.exp.Length e)
  {
	  if ( e.array instanceof NewIntArray) {
		error(e.array.toString()+" is not an array of int ,can not calculate it's length!",e);
	  }
	  this.type = new Int();
  }

  @Override
  public void visit(ast.exp.Lt e)
  {
    e.left.accept(this);
    ast.type.T ty = this.type;
    e.right.accept(this);
    if (!this.type.toString().equals(ty.toString()))
      error("the left and the right of the Lt expression if different,should be the same!",e);
    this.type = new ast.type.Boolean();
    return;
  }

  @Override
  public void visit(ast.exp.NewIntArray e)
  {
	  this.type = new IntArray();
  }

  @Override
  public void visit(ast.exp.NewObject e)
  {
    this.type = new ast.type.Class(e.id);
    return;
  }

  @Override
  public void visit(ast.exp.Not e)
  {
	  e.exp.accept(this);
	  if (!(this.type instanceof Boolean)) {
		 error("the exp of Not operator should return Boolean type!",e);
	  }
	  this.type = new Boolean();
  }

  @Override
  public void visit(ast.exp.Num e)
  {
    this.type = new ast.type.Int();
    return;
  }

  @Override
  public void visit(ast.exp.Sub e)
  {
    e.left.accept(this);
    ast.type.T leftty = this.type;
    e.right.accept(this);
    if ((!this.type.toString().equals(leftty.toString()))
    	|| !(this.type instanceof Int))
      error("the type of the left and the right expression in Sub operator should be Int type!",e);
    this.type = new ast.type.Int();
    return;
  }

  @Override
  public void visit(ast.exp.This e)
  {
    this.type = new ast.type.Class(this.currentClass);
    return;
  }

  @Override
  public void visit(ast.exp.Times e)
  {
    e.left.accept(this);
    ast.type.T leftty = this.type;
    e.right.accept(this);
    if ((!this.type.toString().equals(leftty.toString()))
        	|| !(this.type instanceof Int))
          error("the type of the left and the right expression in Times operator should be Int type!",e);
    this.type = new ast.type.Int();
    return;
  }

  @Override
  public void visit(ast.exp.True e)
  {
	  this.type = new Boolean();
  }

  // statements
  @Override
  public void visit(ast.stm.Assign s)
  {
    // first look up the id in method table
    ast.type.T type = this.methodTable.get(s.id);
    // if search failed, then s.id must
    if (type == null)
      type = this.classTable.get(this.currentClass, s.id);
    if (type == null)
      error("can not find any declaraion about"+s.id+"!",s);
    s.exp.accept(this);
    s.type = type;
    if(!(this.type.toString().equals(type.toString()))){
    	error("the type of the left and the right expression in Assign operator mismatch!",s);
    }
    return;
  }

  @Override
  public void visit(ast.stm.AssignArray s)
  {
	    ast.type.T type = this.methodTable.get(s.id);
	    if (type == null)
	      type = this.classTable.get(this.currentClass, s.id);
	    if (type == null)
	      error("can not find any declaraion about"+s.id+"!",s);
	    s.index.accept(this);
	    if (!(this.type instanceof Int)) {
			error("the index expression of AssignArray should be Int type! ",s);
		}
	    s.exp.accept(this);
	    if (!(this.type.equals(type))) {
	    	error("the type of the left and the right expression in Assign operator mismatch!",s);
		}
    
  }

  @Override
  public void visit(ast.stm.Block s)
  {
	  for (ast.stm.T stm  : s.stms) {
		stm.accept(this);
	  }
  }

  @Override
  public void visit(ast.stm.If s)
  {
    s.condition.accept(this);
    if (!this.type.toString().equals("@boolean"))
      error("the condition expression of If operator should be boolean",s);
    s.thenn.accept(this);
    s.elsee.accept(this);
    return;
  }

  @Override
  public void visit(ast.stm.Print s)
  {
    s.exp.accept(this);
    /*if (!this.type.toString().equals("@int"))
      error("can not print this type!",s);*/
    return;
  }

  @Override
  public void visit(ast.stm.While s)
  {
	  s.condition.accept(this);
	  if (!(this.type instanceof Boolean)) {
		  error("the condition of while statement should return boolean type!!",s);
	  }
	  s.body.accept(this);
  }

  // type
  @Override
  public void visit(ast.type.Boolean t)
  {
	  this.type = t;
  }

  @Override
  public void visit(ast.type.Class t)
  {
	  this.type = t;
  }

  @Override
  public void visit(ast.type.Int t)
  {
	  this.type = t;
  }

  @Override
  public void visit(ast.type.IntArray t)
  {
	  this.type = t;
  }

  // dec
  @Override
  public void visit(ast.dec.Dec d)
  {
	    ast.type.T type = this.methodTable.get(d.id);
	    if (type == null)
	      type = this.classTable.get(this.currentClass, d.id);
	    if (type == null){
	    	this.classTable.put(this.currentClass, d.id, d.type);
	    }
	    this.type = d.type;
  }

  // method
  @Override
  public void visit(ast.method.Method m)
  {
    // construct the method table
    this.methodTable.put(m.formals, m.locals);

    if (control.Control.elabMethodTable)
      this.methodTable.dump();

    for (ast.stm.T s : m.stms)
      s.accept(this);
    m.retExp.accept(this);
    return;
  }

  // class
  @Override
  public void visit(ast.classs.Class c)
  {
    this.currentClass = c.id;

    for (ast.method.T m : c.methods) {
      m.accept(this);
    }
    return;
  }

  // main class
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
  // class table for Main class
  private void buildMainClass(ast.mainClass.MainClass main)
  {
    this.classTable.put(main.id, new ClassBinding(null));
  }

  // class table for normal classes
  private void buildClass(ast.classs.Class c)
  {
    this.classTable.put(c.id, new ClassBinding(c.extendss));
    for (ast.dec.T dec : c.decs) {
      ast.dec.Dec d = (ast.dec.Dec) dec;
      this.classTable.put(c.id, d.id, d.type);
    }
    for (ast.method.T method : c.methods) {
      ast.method.Method m = (ast.method.Method) method;
      this.classTable.put(c.id, m.id, new MethodType(m.retType, m.formals));
    }
  }

  // step 1: end
  // ///////////////////////////////////////////////////

  // program
  @Override
  public void visit(ast.program.Program p)
  {
    // ////////////////////////////////////////////////
    // step 1: build a symbol table for class (the class table)
    // a class table is a mapping from class names to class bindings
    // classTable: className -> ClassBinding{extends, fields, methods}
    buildMainClass((ast.mainClass.MainClass) p.mainClass);
    for (ast.classs.T c : p.classes) {
      buildClass((ast.classs.Class) c);
    }

    // we can double check that the class table is OK!
    if (control.Control.elabClassTable) {
      this.classTable.dump();
    }

    // ////////////////////////////////////////////////
    // step 2: elaborate each class in turn, under the class table
    // built above.
    p.mainClass.accept(this);
    for (ast.classs.T c : p.classes) {
      c.accept(this);
    }
    
    //check if any declarations never used
    Set<Entry<String, ClassBinding>> cSet = this.classTable.getTable().entrySet();
    for (Entry<String, ClassBinding> entry : cSet) {
    	Set<Entry<String, ast.type.T>> fieldSet = entry.getValue().fields.entrySet();
		for (Entry<String, ast.type.T> typeEnr : fieldSet) {
			if (typeEnr.getValue().callTimes < 1) {
				warn("field ",typeEnr.getValue().toString(), typeEnr.getKey());
			}
		}
		Set<Entry<String, elaborator.MethodType>> methodSet = entry.getValue().methods.entrySet();
		for (Entry<String, elaborator.MethodType> typeEnr : methodSet) {
			if (typeEnr.getValue().callTimes < 1) {
				warn("method ",typeEnr.getValue().toString(), typeEnr.getKey());
			}
		}
	}
  }
}
