package codegen.bytecode;

import codegen.bytecode.stm.*;

public interface Visitor 
{
	// statements
	public void visit(Aload s);

	public void visit(Areturn s);
	
	public void visit(ArrayLength s);

	public void visit(Astore s);
	
	public void visit(Getfield s);

	public void visit(Goto s);

	public void visit(Iadd s);
	
	public void visit(IAload s);
	
	public void visit(Iand s);
	
	public void visit(IAstore s);
	
	public void visit(Iconst s);
	
	public void visit(Ificmplt s);

	public void visit(Ifne s);

	public void visit(Iload s);

	public void visit(Imul s);

	public void visit(Ireturn s);

	public void visit(Istore s);

	public void visit(Isub s);

	public void visit(Invokevirtual s);

	public void visit(Label s);

	public void visit(Ldc s);

	public void visit(Print s);
	
	public void visit(Putfield s);

	public void visit(New s);
	
	public void visit(NewArray s);

	// type
	public void visit(codegen.bytecode.type.Class t);

	public void visit(codegen.bytecode.type.Int t);

	public void visit(codegen.bytecode.type.IntArray t);

	// dec
	public void visit(codegen.bytecode.dec.Dec d);

	// method
	public void visit(codegen.bytecode.method.Method m);

	// class
	public void visit(codegen.bytecode.classs.Class c);

	// main class
	public void visit(codegen.bytecode.mainClass.MainClass c);

	// program
	public void visit(codegen.bytecode.program.Program p);
}