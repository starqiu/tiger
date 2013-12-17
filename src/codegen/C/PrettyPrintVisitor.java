package codegen.C;

import control.Control;

import java.io.*;
import java.util.LinkedList;

public class PrettyPrintVisitor implements Visitor 
{
	private int indentLevel;
	private BufferedWriter writer;
	private LinkedList<String> reId;

        public PrettyPrintVisitor() 
        {
                this.indentLevel = 0;
        }

        private void indent() 
        {
                this.indentLevel += 4;
        }

        private void unIndent() 
        {
                this.indentLevel -= 4;
        }

        private void printSpaces() 
        {
                int i = this.indentLevel;
                while(i-- != 0)
                        this.say(" ");
        }

        private void sayln(String s) 
        {
                this.say(s);
                try 
                {
                        this.writer.write("\n");
                } 
                catch(Exception e) 
                {
                        e.printStackTrace();
                        System.exit(1);
                }
        }

        private void say(String s) 
        {
                try 
                {
                        this.writer.write(s);
                } 
                catch(Exception e) 
                {
                        e.printStackTrace();
                        System.exit(1);
                }
        }

        // /////////////////////////////////////////////////////
        // expressions
        // left + right
        @Override
        public void visit(codegen.C.exp.Add e) 
        {
                e.left.accept(this);
                this.say(" + ");
                e.right.accept(this);
                return;
        }

        // left && right
        @Override
        public void visit(codegen.C.exp.And e) 
        {
                e.left.accept(this);
                this.say(" && ");
                e.right.accept(this);
                return;
        }

	// array[index]
	@Override
	public void visit(codegen.C.exp.ArraySelect e) 
	{
		this.say("(");
		e.array.accept(this);
		this.say(" + 4)[");
		e.index.accept(this);
		this.say("]");
		return;
	}

	// exp.id(expList)
	// (assign = exp, assign->vptr->id(assign, args))
	@Override
	public void visit(codegen.C.exp.Call e) 
	{
		this.say("(frame." + e.assign + "=");
		e.exp.accept(this);
		this.say(", ");
		this.say("frame." + e.assign + "->vptr->" + e.id + "(frame." + e.assign);
		int size = e.args.size();
		if(size == 0) 
		{
			this.say("))");
			return;
		}
		for(codegen.C.exp.T x : e.args)
		{
			this.say(", ");
			int index = e.args.indexOf(x);
			codegen.C.type.T type = e.at.get(index);
			if(type instanceof codegen.C.type.Class)
				this.say("(struct " + type.toString() + " *)");
			x.accept(this);
		}
		this.say("))");
		return;
	}

        // id
        @Override
        public void visit(codegen.C.exp.Id e)
        {
                if(!e.isField)
			if(this.reId.contains(e.id))
				this.say("frame." + e.id);
			else
                        this.say(e.id);
                else
                        this.say("this->" + e.id);
                return;
        }

	// array.length
	@Override
	public void visit(codegen.C.exp.Length e)
	{
		this.say("*(");
		e.array.accept(this);
		this.say(" + 2)");
		return;
	}

        // left < right
        @Override
        public void visit(codegen.C.exp.Lt e) 
        {
                e.left.accept(this);
                this.say(" < ");
                e.right.accept(this);
                return;
        }

        // new int[exp]
        // ((int *)(Tiger_new_array(exp * sizeof(int))))
        @Override
        public void visit(codegen.C.exp.NewIntArray e) 
        {
                this.say("((int *)(Tiger_new_array(");
                e.exp.accept(this);
                this.say(" * sizeof(int))))");
                return;
        }

        // new id()
        // ((struct id *)(Tiger_new(&id_vtable_, sizeof(struct id))))
        @Override
        public void visit(codegen.C.exp.NewObject e) 
        {
                this.say("((struct " + e.id + "*)(Tiger_new(&" + e.id 
                                + "_vtable_, sizeof(struct " + e.id + "))))");
                return;
        }

        // !exp
        @Override
        public void visit(codegen.C.exp.Not e) 
        {
                this.say("!");
                e.exp.accept(this);
                return;
        }

        @Override
        public void visit(codegen.C.exp.Num e) 
        {
                this.say(Integer.toString(e.num));
                return;
        }
        
        // (exp)
        @Override
        public void visit(codegen.C.exp.Paren e) 
        {
                this.say("(");
                e.exp.accept(this);
                this.say(")");
                return;
        }

        // left - right
        @Override
        public void visit(codegen.C.exp.Sub e)
        {
                e.left.accept(this);
                this.say(" - ");
                e.right.accept(this);
                return;
        }

        // this
        @Override
        public void visit(codegen.C.exp.This e)
        {
                this.say("this");
        }

        // left * right
        @Override
        public void visit(codegen.C.exp.Times e) 
        {
                e.left.accept(this);
                this.say(" * ");
                e.right.accept(this);
                return;
        }

        // statements
        // id = Exp;
        @Override
        public void visit(codegen.C.stm.Assign s) 
        {
                this.printSpaces();
                s.id.accept(this);
                this.say(" = ");
                s.exp.accept(this);
                this.say(";");
                this.sayln("");
                return;
        }

	// id[index] = Exp;
	@Override
	public void visit(codegen.C.stm.AssignArray s) 
	{
		this.printSpaces();
		this.say("(");
		s.id.accept(this);
		this.say(" + 4)[");
		s.index.accept(this);
		this.say("] = ");
		s.exp.accept(this);
		this.say(";");
		this.sayln("");
		return;
	}

        // { Statement* }
        @Override
        public void visit(codegen.C.stm.Block s) 
        {
                this.printSpaces();
                this.sayln("{");
                this.indent();
                for(codegen.C.stm.T b : s.stms)
                        b.accept(this);
                this.unIndent();
                this.printSpaces();
                this.say("}");
                this.sayln("");
                return;
        }

        // if(condition) 
        //                thenn 
        // else 
        //                elsee
        @Override
        public void visit(codegen.C.stm.If s) 
        {
                this.printSpaces();
                this.say("if(");
                s.condition.accept(this);
                this.sayln(")");
                if(!(s.thenn instanceof codegen.C.stm.Block))
                {
                        this.indent();
                        s.thenn.accept(this);
                        this.unIndent();
                        this.printSpaces();
                        this.sayln("else");
                        if(!(s.elsee instanceof codegen.C.stm.Block))
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
                        if(!(s.elsee instanceof codegen.C.stm.Block))
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

        // System_out_println(exp)
        @Override
        public void visit(codegen.C.stm.Print s) 
        {
                this.printSpaces();
                this.say("System_out_println(");
                s.exp.accept(this);
                this.sayln(");");
                return;
        }

        // while(condition) 
        //                 body
        @Override
        public void visit(codegen.C.stm.While s) 
        {
                this.printSpaces();
                this.say("while(");
                s.condition.accept(this);
                this.sayln(")");
                if(!(s.body instanceof codegen.C.stm.Block))
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
        @Override
        public void visit(codegen.C.type.Class t)
        {
                this.say("struct " + t.id + " *");
        }

        // int
        @Override
        public void visit(codegen.C.type.Int t) 
        {
                this.say("int");
        }

        // int[]
        // int *
        @Override
        public void visit(codegen.C.type.IntArray t) 
        {
                this.say("int *");
        }

        // dec
        @Override
        public void visit(codegen.C.dec.Dec d) 
        {
                d.type.accept(this);
                this.say(" " + d.id);
                return;
        }

        // method
        @Override
        public void visit(codegen.C.method.Method m) 
	{
		this.reId = new LinkedList<String>();
		
		m.retType.accept(this);
		this.say(" " + m.classId + "_" + m.id + "(");
		int size = m.formals.size();
		for(codegen.C.dec.T d : m.formals) 
		{
			codegen.C.dec.Dec dec = (codegen.C.dec.Dec)d;
			size--;
			dec.type.accept(this);
			this.say(" " + dec.id);
			if(size > 0)
				this.say(", ");
		}
		this.sayln(")");
		this.sayln("{");
		this.indent();
		this.printSpaces();
		this.sayln("struct " + m.classId + "_" + m.id + "_gc_frame frame;");
		this.printSpaces();
		this.sayln("frame.prev = previous;");
		this.printSpaces();
		this.sayln("previous = &frame;");
		this.printSpaces();
		this.sayln("frame.arguments_gc_map = " + m.classId + "_" + m.id + "_arguments_gc_map;");
		this.printSpaces();
		this.sayln("frame.arguments_base_address = (int *)&thiss;");
		this.printSpaces();
		this.sayln("frame.locals_gc_map = " + m.classId + "_" + m.id + "_locals_gc_map;");
		
		for(codegen.C.dec.T d : m.locals) 
		{
			codegen.C.dec.Dec dec = (codegen.C.dec.Dec)d;
			if(dec.type instanceof codegen.C.type.Class || dec.type instanceof codegen.C.type.IntArray)
			{
				this.printSpaces();
				this.sayln("frame." + dec.id + " = 0;");
				this.reId.add(dec.id);
			}
			else if(!(dec.type instanceof codegen.C.type.Class) && !(dec.type instanceof codegen.C.type.IntArray))
			{
				this.printSpaces();
				dec.type.accept(this);
				this.say(" " + dec.id + ";\n");
			}
			/*else
				this.reId.add(dec.id);*/
		}
		this.sayln("");
		
		for(codegen.C.stm.T s : m.stms)
			s.accept(this);
		
		this.printSpaces();
		this.sayln("previous = frame.prev;");
		this.printSpaces();
		this.say("return ");
		m.retExp.accept(this);
		this.sayln(";");
		this.unIndent();
		this.sayln("}\n");
		return;
	}
	
	public void outputGCStackFrame(codegen.C.mainMethod.MainMethod m)
	{
		this.sayln("struct Tiger_main_gc_frame");
		this.sayln("{");
		this.indent();
		this.printSpaces();
		this.sayln("void *prev;");
		this.printSpaces();
		this.sayln("char *arguments_gc_map;");
		this.printSpaces();
		this.sayln("int *arguments_base_address;");
		this.printSpaces();
		this.sayln("int locals_gc_map;");
		
		for(codegen.C.dec.T d : m.locals)
		{
			codegen.C.dec.Dec dec = (codegen.C.dec.Dec)d;
			if(dec.type instanceof codegen.C.type.Class || dec.type instanceof codegen.C.type.IntArray)
			{
				this.printSpaces();
				dec.type.accept(this);
				this.say(" " + dec.id + ";\n");
			}
		}
		this.unIndent();
		this.sayln("};\n");
	}
	
	public void outputGCStackFrame(codegen.C.method.Method m)
	{
		this.sayln("struct " + m.classId + "_" + m.id + "_gc_frame");
		this.sayln("{");
		this.indent();
		this.printSpaces();
		this.sayln("void *prev;");
		this.printSpaces();
		this.sayln("char *arguments_gc_map;");
		this.printSpaces();
		this.sayln("int *arguments_base_address;");
		this.printSpaces();
		this.sayln("int locals_gc_map;");
		
		for(codegen.C.dec.T d : m.locals)
		{
			codegen.C.dec.Dec dec = (codegen.C.dec.Dec)d;
			if(dec.type instanceof codegen.C.type.Class || dec.type instanceof codegen.C.type.IntArray)
			{
				this.printSpaces();
				dec.type.accept(this);
				this.say(" " + dec.id + ";\n");
			}
		}
		this.unIndent();
		this.sayln("};\n");
	}
	
	public void outputMemoryGCMap(codegen.C.mainMethod.MainMethod m)
	{
		// memory GC map for locals
		int reSize = 0;
		for(codegen.C.dec.T d : m.locals)
		{
			codegen.C.type.T type = ((codegen.C.dec.Dec)d).type;
			if(type instanceof codegen.C.type.Class || type instanceof codegen.C.type.IntArray)
				reSize++;
		}
		this.sayln("int Tiger_main_locals_gc_map = " + reSize + ";\n");
	}
	
	public void outputMemoryGCMap(codegen.C.method.Method m)
	{
		// memory GC map for arguments
		this.say("char *" + m.classId + "_" + m.id + "_arguments_gc_map = \"");
		int size = m.formals.size();
		for(codegen.C.dec.T d : m.formals)
		{
			codegen.C.type.T type = ((codegen.C.dec.Dec)d).type;
			size--;
			if(type instanceof codegen.C.type.Class || type instanceof codegen.C.type.IntArray)
				this.say("1");
			else
				this.say("0");
			if(size == 0)
				this.sayln("\";");
		}
		
		// memory GC map for locals
		int reSize = 0;
		for(codegen.C.dec.T d : m.locals)
		{
			codegen.C.type.T type = ((codegen.C.dec.Dec)d).type;
			if(type instanceof codegen.C.type.Class || type instanceof codegen.C.type.IntArray)
				reSize++;
		}
		this.sayln("int " + m.classId + "_" + m.id + "_locals_gc_map = " + reSize + ";\n");
	}

	@Override
	public void visit(codegen.C.mainMethod.MainMethod m) 
	{
		this.sayln("int Tiger_main()");
		this.sayln("{");
		this.indent();
		this.printSpaces();
		this.sayln("struct Tiger_main_gc_frame frame;");
		this.printSpaces();
		this.sayln("frame.prev = previous;");
		this.printSpaces();
		this.sayln("previous = &frame;");
		this.printSpaces();
		this.sayln("frame.arguments_gc_map = 0;");
		this.printSpaces();
		this.sayln("frame.arguments_base_address = 0;");
		this.printSpaces();
		this.sayln("frame.locals_gc_map = Tiger_main_locals_gc_map;");
		
		for(codegen.C.dec.T dec : m.locals) 
		{
			codegen.C.dec.Dec d = (codegen.C.dec.Dec)dec;
			if(!(d.type instanceof codegen.C.type.Class) && !(d.type instanceof codegen.C.type.IntArray))
			{
				this.printSpaces();
				d.type.accept(this);
				this.say(" ");
				this.sayln(d.id + ";");
			}
		}
		m.stm.accept(this);
		this.unIndent();
		this.sayln("}\n");
		return;
	}

	// vtables
	@Override
	public void visit(codegen.C.vtable.Vtable v) 
	{
		this.sayln("struct " + v.id + "_vtable");
		this.sayln("{");
		this.indent();
		
		// class GC map
		if(!v.ms.isEmpty())
		{
			this.printSpaces();
			this.sayln("char *" + v.id + "_gc_map;");
		}
		
		for(codegen.C.Ftuple t : v.ms) 
		{
			this.printSpaces();
			t.ret.accept(this);
			
			this.say("(*" + t.id + ")(struct " + t.className + "*");
			int size = t.args.size();
			if(size != 0)
				this.say(", ");
			for(codegen.C.dec.T d : t.args)
			{
				codegen.C.dec.Dec dec = (codegen.C.dec.Dec)d;
				size--;
				dec.type.accept(this);
				if(size > 0)
					this.say(", ");
			}
			this.sayln(");");
		}
		
		this.unIndent();
		this.sayln("};\n");
		return;
	}

	private void outputVtable(codegen.C.vtable.Vtable v) 
	{
		this.sayln("struct " + v.id + "_vtable " + v.id + "_vtable_ = ");
		this.sayln("{");
		this.indent();
		
		// class GC map
		if(!v.ms.isEmpty())
		{
			this.printSpaces();
			this.sayln("\"" + v.gc_map + "\",");
		}
		
		for(codegen.C.Ftuple t : v.ms) 
		{
			this.printSpaces();
			this.sayln(t.className + "_" + t.id + ",");
		}
		this.unIndent();
		this.sayln("};\n");
		return;
	}

	// class
	@Override
	public void visit(codegen.C.classes.Class c)
	{
		this.sayln("struct " + c.id);
		this.sayln("{");
		this.indent();
		this.printSpaces();
		this.sayln("struct " + c.id + "_vtable *vptr;");
		this.printSpaces();
		this.sayln("int isObjOrArray;");
		this.printSpaces();
		this.sayln("unsigned length;");
		this.printSpaces();
		this.sayln("void *forwarding;");
		for(codegen.C.Tuple t : c.decs) 
		{
			this.printSpaces();
			t.type.accept(this);
			this.say(" ");
			this.sayln(t.id + ";");
		}
		this.unIndent();
		this.sayln("};\n");
		return;
	}

        // program
        @Override
        public void visit(codegen.C.program.Program p) 
        {
                // we'd like to output to a file, rather than the "stdout"
                try 
                {
                        String outputName = null;
                        if(Control.outputName != null)
                                outputName = Control.outputName;
                        else if(Control.fileName != null)
                                outputName = Control.fileName + ".c";
                        else
                                outputName = "a.c";

                        this.writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputName)));
                } 
                catch(Exception e) 
                {
                        e.printStackTrace();
                        System.exit(1);
                }

                this.sayln("// This is automatically generated by the Tiger compiler.");
                this.sayln("// Do NOT modify!\n");

                this.sayln("// structures");
                for(codegen.C.classes.T c : p.classes) 
                {
                        c.accept(this);
                }

                this.sayln("// vtables structures");
                for(codegen.C.vtable.T v : p.vtables) 
                {
                        v.accept(this);
                }
                
                this.sayln("// method declarations");
                for(codegen.C.method.T m : p.methods)
                {
                        codegen.C.method.Method me = (codegen.C.method.Method)m;
                        me.retType.accept(this);
                        this.say(" " + me.classId + "_" + me.id + "(");
                        int size = me.formals.size();
                        for(codegen.C.dec.T d : me.formals) 
                        {
                                codegen.C.dec.Dec dec = (codegen.C.dec.Dec)d;
                                size--;
                                dec.type.accept(this);
                                this.say(" " + dec.id);
                                if(size > 0)
                                        this.say(", ");
                        }
                        this.sayln(");");
                }
                this.sayln("");

                this.sayln("// vtables");
                for(codegen.C.vtable.T v : p.vtables) 
                {
                        outputVtable((codegen.C.vtable.Vtable)v);
                }
                
		this.sayln("// GC stack frames");
		outputGCStackFrame((codegen.C.mainMethod.MainMethod)p.mainMethod);
		for(codegen.C.method.T m : p.methods)
		{
			outputGCStackFrame((codegen.C.method.Method)m);
		}
		
		this.sayln("// memory GC maps");
		outputMemoryGCMap((codegen.C.mainMethod.MainMethod)p.mainMethod);
		for(codegen.C.method.T m : p.methods)
		{
			outputMemoryGCMap((codegen.C.method.Method)m);
		}
		
		this.sayln("extern void *previous;\n");
		
                this.sayln("// methods");
                for(codegen.C.method.T m : p.methods) 
                {
                        m.accept(this);
                }
                
                this.sayln("// main method");
                p.mainMethod.accept(this);

                try 
                {
                        this.writer.close();
                } 
                catch (Exception e) 
                {
                        e.printStackTrace();
                        System.exit(1);
                }
        }
}