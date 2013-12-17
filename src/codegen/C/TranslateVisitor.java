package codegen.C;

import java.util.LinkedList;

// Given a Java ast, translate it into a C ast and outputs it.

public class TranslateVisitor implements ast.Visitor 
{
        private ClassTable table;
        private String classId;
        private codegen.C.type.T type;// type after translation
        private codegen.C.dec.T dec;
        private codegen.C.stm.T stm;
        private codegen.C.exp.T exp;
        private codegen.C.method.T method;
        private LinkedList<codegen.C.dec.T> tmpVars;
        private LinkedList<codegen.C.classes.T> classes;
        private LinkedList<codegen.C.vtable.T> vtables;
        private LinkedList<codegen.C.method.T> methods;
        private codegen.C.mainMethod.T mainMethod;
        public codegen.C.program.T program;

        public TranslateVisitor() 
        {
                this.table = new ClassTable();
                this.classId = null;
                this.type = null;
                this.dec = null;
                this.stm = null;
                this.exp = null;
                this.method = null;
                this.classes = new LinkedList<codegen.C.classes.T>();
                this.vtables = new LinkedList<codegen.C.vtable.T>();
                this.methods = new LinkedList<codegen.C.method.T>();
                this.mainMethod = null;
                this.program = null;
        }

        // //////////////////////////////////////////////////////
        //
        public String genId() 
        {
                return util.Temp.next();
        }

        // /////////////////////////////////////////////////////
        // expressions
        // left + right
        @Override
        public void visit(ast.exp.Add e) 
        {
                e.left.accept(this);
                codegen.C.exp.T left = this.exp;
                e.right.accept(this);
                codegen.C.exp.T right = this.exp;
                this.exp = new codegen.C.exp.Add(left, right);
                return;
        }

        // left && right
        @Override
        public void visit(ast.exp.And e) 
        {
                e.left.accept(this);
                codegen.C.exp.T left = this.exp;
                e.right.accept(this);
                codegen.C.exp.T right = this.exp;
                this.exp = new codegen.C.exp.And(left, right);
                return;
        }

        // array[index]
        @Override
        public void visit(ast.exp.ArraySelect e) 
        {
                e.array.accept(this);
                codegen.C.exp.T array = this.exp;
                e.index.accept(this);
                codegen.C.exp.T index = this.exp;
                this.exp = new codegen.C.exp.ArraySelect(array, index);
                return;
        }

        // exp.id(expList)
        @Override
        public void visit(ast.exp.Call e) 
        {
                e.exp.accept(this);
                String newid = this.genId();
                this.tmpVars.add(new codegen.C.dec.Dec(new codegen.C.type.Class(e.type), newid));
                codegen.C.exp.T exp = this.exp;
                LinkedList<codegen.C.exp.T> args = new LinkedList<codegen.C.exp.T>();
                for(ast.exp.T x : e.args) 
                {
                        x.accept(this);
                        args.add(this.exp);
                }
                
                LinkedList<codegen.C.type.T> at = new LinkedList<codegen.C.type.T>();
                for(ast.type.T t : e.at)
                {
                        t.accept(this);
                        at.add(this.type);
                }
                
                this.exp = new codegen.C.exp.Call(newid, exp, e.id, args, at);
                return;
        }

        @Override
        public void visit(ast.exp.False e) 
        {
                this.exp = new codegen.C.exp.Num(0);
                return;
        }

        // id
        @Override
        public void visit(ast.exp.Id e) 
        {
                this.exp = new codegen.C.exp.Id(e.id, e.isField);
                return;
        }

        // array.length
        @Override
        public void visit(ast.exp.Length e) 
        {
                e.array.accept(this);
                this.exp = new codegen.C.exp.Length(this.exp);
                return;
        }

        // left < right
        @Override
        public void visit(ast.exp.Lt e) 
        {
                e.left.accept(this);
                codegen.C.exp.T left = this.exp;
                e.right.accept(this);
                codegen.C.exp.T right = this.exp;
                this.exp = new codegen.C.exp.Lt(left, right);
                return;
        }

        // new int[exp]
        @Override
        public void visit(ast.exp.NewIntArray e) 
        {
                e.exp.accept(this);
                this.exp = new codegen.C.exp.NewIntArray(this.exp);
                return;
        }

        // new id()
        @Override
        public void visit(ast.exp.NewObject e) 
        {
                this.exp = new codegen.C.exp.NewObject(e.id);
                return;
        }

        // !exp
        @Override
        public void visit(ast.exp.Not e) 
        {
                e.exp.accept(this);
                this.exp = new codegen.C.exp.Not(this.exp);
                return;
        }

        @Override
        public void visit(ast.exp.Num e)
        {
                this.exp = new codegen.C.exp.Num(e.num);
                return;
        }

        // (exp)
        @Override
        public void visit(ast.exp.Paren e) 
        {
                e.exp.accept(this);
                this.exp = new codegen.C.exp.Paren(this.exp);
                return;
        }

        // left - right
        @Override
        public void visit(ast.exp.Sub e) 
        {
                e.left.accept(this);
                codegen.C.exp.T left = this.exp;
                e.right.accept(this);
                codegen.C.exp.T right = this.exp;
                this.exp = new codegen.C.exp.Sub(left, right);
                return;
        }

        // this
        @Override
        public void visit(ast.exp.This e) 
        {
                this.exp = new codegen.C.exp.This();
                return;
        }

        // left * right
        @Override
        public void visit(ast.exp.Times e)
        {
                e.left.accept(this);
                codegen.C.exp.T left = this.exp;
                e.right.accept(this);
                codegen.C.exp.T right = this.exp;
                this.exp = new codegen.C.exp.Times(left, right);
                return;
        }

        @Override
        public void visit(ast.exp.True e) 
        {
                this.exp = new codegen.C.exp.Num(1);
                return;
        }

        // statements
        // id = Exp;
        @Override
        public void visit(ast.stm.Assign s) 
        {
                s.id.accept(this);
                codegen.C.exp.T id = this.exp;
                s.exp.accept(this);
                codegen.C.exp.T exp = this.exp;
                
                this.stm = new codegen.C.stm.Assign((codegen.C.exp.Id)id, exp);
                return;
        }

        // id[index] = Exp;
        @Override
        public void visit(ast.stm.AssignArray s) 
        {
                s.id.accept(this);
                codegen.C.exp.T id = this.exp;
                
                s.index.accept(this);
                codegen.C.exp.T index = this.exp;
                
                s.exp.accept(this);
                codegen.C.exp.T exp = this.exp;
                
                this.stm = new codegen.C.stm.AssignArray((codegen.C.exp.Id)id, index, exp);
                return;
        }

        // { Statement* }
        @Override
        public void visit(ast.stm.Block s) 
        {
                LinkedList<codegen.C.stm.T> stms = new LinkedList<codegen.C.stm.T>();
                for(ast.stm.T b : s.stms)
                {
                        b.accept(this);
                        stms.add(this.stm);
                }
                this.stm = new codegen.C.stm.Block(stms);
                return;
        }

        // if(condition) 
        //                thenn 
        // else 
        //                elsee
        @Override
        public void visit(ast.stm.If s) 
        {
                s.condition.accept(this);
                codegen.C.exp.T condition = this.exp;
                
                s.thenn.accept(this);
                codegen.C.stm.T thenn = this.stm;
                
                s.elsee.accept(this);
                codegen.C.stm.T elsee = this.stm;
                
                this.stm = new codegen.C.stm.If(condition, thenn, elsee);
                return;
        }

        // System.out.println(Exp);
        @Override
        public void visit(ast.stm.Print s) 
        {
                s.exp.accept(this);
                this.stm = new codegen.C.stm.Print(this.exp);
                return;
        }

        // while(condition) 
        //                 body
        @Override
        public void visit(ast.stm.While s) 
        {
                s.condition.accept(this);
                codegen.C.exp.T condition = this.exp;
                
                s.body.accept(this);
                codegen.C.stm.T body = this.stm;
                
                this.stm = new codegen.C.stm.While(condition, body);
                return;
        }

        // type
        // boolean
        @Override
        public void visit(ast.type.Boolean t) 
        {
                this.type = new codegen.C.type.Int();
                return;
        }

        @Override
        public void visit(ast.type.Class t) 
        {
                this.type = new codegen.C.type.Class(t.id);
                return;
        }

        // int
        @Override
        public void visit(ast.type.Int t) 
        {
                this.type = new codegen.C.type.Int();
                return;
        }

        // int[]
        @Override
        public void visit(ast.type.IntArray t)
        {
                this.type = new codegen.C.type.IntArray();
                return;
        }

        // dec
        @Override
        public void visit(ast.dec.Dec d)
        {
                d.type.accept(this);
                this.dec = new codegen.C.dec.Dec(this.type, d.id);
                return;
        }

        // method
        @Override
        public void visit(ast.method.Method m) 
        {
                this.tmpVars = new LinkedList<codegen.C.dec.T>();
                m.retType.accept(this);
                codegen.C.type.T newRetType = this.type;
                
                LinkedList<codegen.C.dec.T> newFormals = new LinkedList<codegen.C.dec.T>();
                newFormals.add(new codegen.C.dec.Dec(new codegen.C.type.Class(this.classId), "this"));
                for(ast.dec.T d : m.formals) 
                {
                        d.accept(this);
                        newFormals.add(this.dec);
                }
                
                LinkedList<codegen.C.dec.T> locals = new LinkedList<codegen.C.dec.T>();
                for(ast.dec.T d : m.locals) 
                {
                        d.accept(this);
                        locals.add(this.dec);
                }
                
                LinkedList<codegen.C.stm.T> newStm = new LinkedList<codegen.C.stm.T>();
                for(ast.stm.T s : m.stms)
                {
                        s.accept(this);
                        newStm.add(this.stm);
                }
                m.retExp.accept(this);
                codegen.C.exp.T retExp = this.exp;
                for(codegen.C.dec.T dec : this.tmpVars) 
                {
                        locals.add(dec);
                }
                this.method = new codegen.C.method.Method(newRetType, this.classId,
                                m.id, newFormals, locals, newStm, retExp);
                return;
        }

        // class
        @Override
        public void visit(ast.classs.Class c) 
        {
                ClassBinding cb = this.table.get(c.id);
                this.classes.add(new codegen.C.classes.Class(c.id, cb.fields));
                this.vtables.add(new codegen.C.vtable.Vtable(c.id, c.gc_map.toString(), cb.methods));
                this.classId = c.id;
                for(ast.method.T m : c.methods) 
                {
                        m.accept(this);
                        this.methods.add(this.method);
                }
                return;
        }

        // main class
        @Override
        public void visit(ast.mainClass.MainClass c) 
        {
                ClassBinding cb = this.table.get(c.id);
                codegen.C.classes.T newc = new codegen.C.classes.Class(c.id, cb.fields);
                this.classes.add(newc);
                this.vtables.add(new codegen.C.vtable.Vtable(c.id, cb.methods));

                this.tmpVars = new LinkedList<codegen.C.dec.T>();

                c.stm.accept(this);
                codegen.C.mainMethod.T mthd = new codegen.C.mainMethod.MainMethod(this.tmpVars, this.stm);
                this.mainMethod = mthd;
                return;
        }

        // /////////////////////////////////////////////////////
        // the first pass
        public void scanMain(ast.mainClass.T m) 
        {
                this.table.init(((ast.mainClass.MainClass)m).id, null);
                // this is a special hacking in that we don't want to
                // enter "main" into the table.
                return;
        }

        public void scanClasses(LinkedList<ast.classs.T> cs) 
        {
                // put empty chuncks into the table
                for(ast.classs.T c : cs) 
                {
                        ast.classs.Class cc = (ast.classs.Class)c;
                        this.table.init(cc.id, cc.extendss);
                }

                // put class fields and methods into the table
                for(ast.classs.T c : cs) 
                {
                        ast.classs.Class cc = (ast.classs.Class)c;
                        LinkedList<codegen.C.dec.T> newDecs = new LinkedList<codegen.C.dec.T>();
                        for(ast.dec.T dec : cc.decs) 
                        {
                                dec.accept(this);
                                newDecs.add(this.dec);
                        }
                        this.table.initDecs(cc.id, newDecs);

                        // all methods
                        LinkedList<ast.method.T> methods = cc.methods;
                        for(ast.method.T mthd : methods) 
                        {
                                ast.method.Method m = (ast.method.Method)mthd;
                                LinkedList<codegen.C.dec.T> newArgs = new LinkedList<codegen.C.dec.T>();
                                for(ast.dec.T arg : m.formals) 
                                {
                                        arg.accept(this);
                                        newArgs.add(this.dec);
                                }
                                m.retType.accept(this);
                                codegen.C.type.T newRet = this.type;
                                this.table.initMethod(cc.id, newRet, newArgs, m.id);
                        }
                }

                // calculate all inheritance information
                for(ast.classs.T c : cs) 
                {
                        ast.classs.Class cc = (ast.classs.Class)c;
                        this.table.inherit(cc.id);
			for(ast.dec.T dec : cc.decs)
			{
				ast.dec.Dec d = (ast.dec.Dec)dec;
				if(d.type instanceof ast.type.Class || d.type instanceof ast.type.IntArray)
					cc.gc_map.append('1');
				else
					cc.gc_map.append('0');
			}
                }
        }

        public void scanProgram(ast.program.T p) 
        {
                ast.program.Program pp = (ast.program.Program)p;
                scanMain(pp.mainClass);
                scanClasses(pp.classes);
                return;
        }

        // end of the first pass
        // ////////////////////////////////////////////////////

        // program
        @Override
        public void visit(ast.program.Program p) 
        {
                // The first pass is to scan the whole program "p", and
                // to collect all information of inheritance
                scanProgram(p);

                // do translations
                p.mainClass.accept(this);
                
                for(ast.classs.T classs : p.classes) 
                {
                        classs.accept(this);
                }
                
                this.program = new codegen.C.program.Program(this.classes, this.vtables, this.methods, this.mainMethod);
                return;
        }
}