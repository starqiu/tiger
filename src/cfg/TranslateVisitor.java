package cfg;

import codegen.C.exp.Paren;
import java.util.LinkedList;
import java.util.ArrayList;

// Traverse the C AST, and generate
// a control-flow graph.
public class TranslateVisitor implements codegen.C.Visitor {
	private String classId;
	private cfg.type.T type; // type after translation
	private cfg.operand.T operand;
	private cfg.dec.T dec;
	// A dirty hack. Can hold stm, transfer, or label.
	private ArrayList<Object> stmOrTransfer;
	private util.Label entry;
	private LinkedList<cfg.dec.T> newLocals;
	private cfg.method.T method;
	private cfg.classs.T classs;
	private cfg.vtable.T vtable;
	private cfg.mainMethod.T mainMethod;
	public cfg.program.T program;

	public TranslateVisitor() {
		this.classId = null;
		this.type = null;
		this.dec = null;
		this.stmOrTransfer = new ArrayList<Object>();
		this.newLocals = new LinkedList<cfg.dec.T>();
		this.method = null;
		this.classs = null;
		this.vtable = null;
		this.mainMethod = null;
		this.program = null;
	}

	// /////////////////////////////////////////////////////
	// utility functions
	private LinkedList<cfg.block.T> cookBlocks() {
		LinkedList<cfg.block.T> blocks = new LinkedList<cfg.block.T>();

		int i = 0;
		int size = this.stmOrTransfer.size();
		while (i < size) {
			util.Label label;
			cfg.block.Block b;
			LinkedList<cfg.stm.T> stms = new LinkedList<cfg.stm.T>();
			cfg.transfer.T transfer;

			if (!(this.stmOrTransfer.get(i) instanceof util.Label)) {
				new util.Error();
			}
			label = (util.Label) this.stmOrTransfer.get(i++);
			while (i < size && this.stmOrTransfer.get(i) instanceof cfg.stm.T) {
				stms.add((cfg.stm.T) this.stmOrTransfer.get(i++));
			}
			transfer = (cfg.transfer.T) this.stmOrTransfer.get(i++);
			b = new cfg.block.Block(label, stms, transfer);
			blocks.add(b);
		}
		this.stmOrTransfer = new ArrayList<Object>();
		return blocks;
	}

	private void emit(Object obj) {
		this.stmOrTransfer.add(obj);
	}

	private String genVar() {
		String fresh = util.Temp.next();
		cfg.dec.Dec dec = new cfg.dec.Dec(new cfg.type.Int(), fresh);
		this.newLocals.add(dec);
		return fresh;
	}

	private String genVar(cfg.type.T ty) {
		String fresh = util.Temp.next();
		cfg.dec.Dec dec = new cfg.dec.Dec(ty, fresh);
		this.newLocals.add(dec);
		return fresh;
	}

	// /////////////////////////////////////////////////////
	// expressions
	@Override
	public void visit(codegen.C.exp.Add e) {
	}

	@Override
	public void visit(codegen.C.exp.And e) {
	}

	@Override
	public void visit(codegen.C.exp.ArraySelect e) {
	}

	@Override
	public void visit(codegen.C.exp.Call e) {
		e.retType.accept(this);
		String dst = genVar(this.type);
		String obj = null;
		e.exp.accept(this);
		cfg.operand.T objOp = this.operand;
		if (objOp instanceof cfg.operand.Var) {
			cfg.operand.Var var = (cfg.operand.Var) objOp;
			obj = var.id;
		} else {
			new util.Error();
		}

		LinkedList<cfg.operand.T> newArgs = new LinkedList<cfg.operand.T>();
		for (codegen.C.exp.T x : e.args) {
			x.accept(this);
			newArgs.add(this.operand);
		}
		emit(new cfg.stm.InvokeVirtual(dst, obj, e.id, newArgs));
		this.operand = new cfg.operand.Var(dst);
		return;
	}

	@Override
	public void visit(codegen.C.exp.Id e) {
		this.operand = new cfg.operand.Var(e.id);
		return;
	}

	@Override
	public void visit(codegen.C.exp.Length e) {
	}

	@Override
	public void visit(codegen.C.exp.Lt e) {
		String dst = genVar();
		e.left.accept(this);
		cfg.operand.T left = this.operand;
		e.right.accept(this);
		emit(new cfg.stm.Lt(dst, null, left, this.operand));
		this.operand = new cfg.operand.Var(dst);
		return;
	}

	@Override
	public void visit(codegen.C.exp.NewIntArray e) {
	}

	@Override
	public void visit(codegen.C.exp.NewObject e) {
		String dst = genVar(new cfg.type.Class(e.id));
		emit(new cfg.stm.NewObject(dst, e.id));
		this.operand = new cfg.operand.Var(dst);
		return;
	}

	@Override
	public void visit(codegen.C.exp.Not e) {
	}

	@Override
	public void visit(codegen.C.exp.Num e) {
		this.operand = new cfg.operand.Int(e.num);
		return;
	}

	@Override
	public void visit(codegen.C.exp.Sub e) {
		String dst = genVar();
		e.left.accept(this);
		cfg.operand.T left = this.operand;
		e.right.accept(this);
		emit(new cfg.stm.Sub(dst, null, left, this.operand));
		this.operand = new cfg.operand.Var(dst);
		return;
	}

	@Override
	public void visit(codegen.C.exp.This e) {
		this.operand = new cfg.operand.Var("this");
		return;
	}

	@Override
	public void visit(codegen.C.exp.Times e) {
		String dst = genVar();
		e.left.accept(this);
		cfg.operand.T left = this.operand;
		e.right.accept(this);
		emit(new cfg.stm.Times(dst, null, left, this.operand));
		this.operand = new cfg.operand.Var(dst);
		return;
	}

	// statements
	@Override
	public void visit(codegen.C.stm.Assign s) {
		s.exp.accept(this);
		emit(new cfg.stm.Move(s.id.id, null, this.operand));
		return;
	}

	@Override
	public void visit(codegen.C.stm.AssignArray s) {
	}

	@Override
	public void visit(codegen.C.stm.Block s) {
	}

	@Override
	public void visit(codegen.C.stm.If s) {
		util.Label tl = new util.Label(), fl = new util.Label(), el = new util.Label();
		s.condition.accept(this);
		emit(new cfg.transfer.If(this.operand, tl, fl));
		emit(fl);
		s.elsee.accept(this);
		emit(new cfg.transfer.Goto(el));
		emit(tl);
		s.thenn.accept(this);
		emit(new cfg.transfer.Goto(el));
		emit(el);
		return;
	}

	@Override
	public void visit(codegen.C.stm.Print s) {
		s.exp.accept(this);
		emit(new cfg.stm.Print(this.operand));
		return;
	}

	@Override
	public void visit(codegen.C.stm.While s) {
	}

	// type
	@Override
	public void visit(codegen.C.type.Class t) {
		this.type = new cfg.type.Class(t.id);
	}

	@Override
	public void visit(codegen.C.type.Int t) {
		this.type = new cfg.type.Int();
	}

	@Override
	public void visit(codegen.C.type.IntArray t) {
	}

	// dec
	@Override
	public void visit(codegen.C.dec.Dec d) {
		d.type.accept(this);
		this.dec = new cfg.dec.Dec(this.type, d.id);
		return;
	}

	// vtable
	@Override
	public void visit(codegen.C.vtable.Vtable v) {
		LinkedList<cfg.Ftuple> newTuples = new LinkedList<cfg.Ftuple>();
		for (codegen.C.Ftuple t : v.ms) {
			t.ret.accept(this);
			cfg.type.T ret = this.type;
			LinkedList<cfg.dec.T> args = new LinkedList();
			for (codegen.C.dec.T dec : t.args) {
				dec.accept(this);
				args.add(this.dec);
			}
			newTuples.add(new cfg.Ftuple(t.className, ret, args, t.id));
		}
		this.vtable = new cfg.vtable.Vtable(v.id, newTuples);
		return;
	}

	// class
	@Override
	public void visit(codegen.C.classes.Class c) {
		LinkedList<cfg.Tuple> newTuples = new LinkedList<cfg.Tuple>();
		for (codegen.C.Tuple t : c.decs) {
			t.type.accept(this);
			newTuples.add(new cfg.Tuple(t.className, this.type, t.id));
		}
		this.classs = new cfg.classs.Class(c.id, newTuples);
		return;
	}

	// method
	@Override
	public void visit(codegen.C.method.Method m) {
		this.newLocals = new LinkedList();

		m.retType.accept(this);
		cfg.type.T retType = this.type;

		LinkedList<cfg.dec.T> newFormals = new LinkedList<cfg.dec.T>();
		for (codegen.C.dec.T c : m.formals) {
			c.accept(this);
			newFormals.add(this.dec);
		}

		LinkedList<cfg.dec.T> locals = new LinkedList<cfg.dec.T>();
		for (codegen.C.dec.T c : m.locals) {
			c.accept(this);
			locals.add(this.dec);
		}

		// a junk label
		util.Label entry = new util.Label();
		this.entry = entry;
		emit(entry);

		for (codegen.C.stm.T s : m.stms)
			s.accept(this);

		m.retExp.accept(this);
		emit(new cfg.transfer.Return(this.operand));

		//
		LinkedList<cfg.block.T> blocks = cookBlocks();

		for (cfg.dec.T d : this.newLocals)
			locals.add(d);

		this.method = new cfg.method.Method(retType, m.id, m.classId,
				newFormals, locals, blocks, entry, null, null);
		return;
	}

	// main method
	@Override
	public void visit(codegen.C.mainMethod.MainMethod m) {
		this.newLocals = new LinkedList();

		LinkedList<cfg.dec.T> locals = new LinkedList<cfg.dec.T>();
		for (codegen.C.dec.T c : m.locals) {
			c.accept(this);
			locals.add(this.dec);
		}

		util.Label entry = new util.Label();
		emit(entry);

		m.stm.accept(this);

		emit(new cfg.transfer.Return(new cfg.operand.Int(0)));

		LinkedList<cfg.block.T> blocks = cookBlocks();
		for (cfg.dec.T d : this.newLocals)
			locals.add(d);
		this.mainMethod = new cfg.mainMethod.MainMethod(locals, blocks);
		return;
	}

	// program
	@Override
	public void visit(codegen.C.program.Program p) {
		LinkedList<cfg.classs.T> newClasses = new LinkedList<cfg.classs.T>();
		for (codegen.C.classes.T c : p.classes) {
			c.accept(this);
			newClasses.add(this.classs);
		}

		LinkedList<cfg.vtable.T> newVtable = new LinkedList<cfg.vtable.T>();
		for (codegen.C.vtable.T v : p.vtables) {
			v.accept(this);
			newVtable.add(this.vtable);
		}

		LinkedList<cfg.method.T> newMethods = new LinkedList<cfg.method.T>();
		for (codegen.C.method.T m : p.methods) {
			m.accept(this);
			newMethods.add(this.method);
		}

		p.mainMethod.accept(this);
		cfg.mainMethod.T newMainMethod = this.mainMethod;

		this.program = new cfg.program.Program(newClasses, newVtable,
				newMethods, newMainMethod);
		return;
	}

	@Override
	public void visit(Paren e) {
	}
}
