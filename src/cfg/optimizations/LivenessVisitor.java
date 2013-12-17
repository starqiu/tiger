package cfg.optimizations;

import java.util.HashMap;
import java.util.HashSet;

public class LivenessVisitor implements cfg.Visitor {
	// gen, kill for one statement
	private HashSet<String> oneStmGen;
	private HashSet<String> oneStmKill;

	// gen, kill for one transfer
	private HashSet<String> oneTransferGen;
	private HashSet<String> oneTransferKill;

	// gen, kill for statements
	private HashMap<cfg.stm.T, HashSet<String>> stmGen;
	private HashMap<cfg.stm.T, HashSet<String>> stmKill;

	// gen, kill for transfers
	private HashMap<cfg.transfer.T, HashSet<String>> transferGen;
	private HashMap<cfg.transfer.T, HashSet<String>> transferKill;

	// gen, kill for blocks
	private HashMap<cfg.block.T, HashSet<String>> blockGen;
	private HashMap<cfg.block.T, HashSet<String>> blockKill;

	// liveIn, liveOut for blocks
	private HashMap<cfg.block.T, HashSet<String>> blockLiveIn;
	private HashMap<cfg.block.T, HashSet<String>> blockLiveOut;

	// liveIn, liveOut for statements
	public HashMap<cfg.stm.T, HashSet<String>> stmLiveIn;
	public HashMap<cfg.stm.T, HashSet<String>> stmLiveOut;

	// liveIn, liveOut for transfer
	public HashMap<cfg.transfer.T, HashSet<String>> transferLiveIn;
	public HashMap<cfg.transfer.T, HashSet<String>> transferLiveOut;

	// As you will walk the tree for many times, so
	// it will be useful to recored which is which:
	enum Liveness_Kind_t {
		None, StmGenKill, BlockGenKill, BlockInOut, StmInOut,
	}

	private Liveness_Kind_t kind = Liveness_Kind_t.None;

	public LivenessVisitor() {
		this.oneStmGen = new HashSet();
		this.oneStmKill = new HashSet();

		this.oneTransferGen = new HashSet();
		this.oneTransferKill = new HashSet();

		this.stmGen = new HashMap();
		this.stmKill = new HashMap();

		this.transferGen = new HashMap();
		this.transferKill = new HashMap();

		this.blockGen = new HashMap();
		this.blockKill = new HashMap();

		this.blockLiveIn = new HashMap();
		this.blockLiveOut = new HashMap();

		this.stmLiveIn = new HashMap();
		this.stmLiveOut = new HashMap();

		this.transferLiveIn = new HashMap();
		this.transferLiveOut = new HashMap();

		this.kind = Liveness_Kind_t.None;
	}

	// /////////////////////////////////////////////////////
	// utilities

	private HashSet<String> getOneStmGenAndClear() {
		HashSet<String> temp = this.oneStmGen;
		this.oneStmGen = new HashSet();
		return temp;
	}

	private HashSet<String> getOneStmKillAndClear() {
		HashSet<String> temp = this.oneStmKill;
		this.oneStmKill = new HashSet();
		return temp;
	}

	private HashSet<String> getOneTransferGenAndClear() {
		HashSet<String> temp = this.oneTransferGen;
		this.oneTransferGen = new HashSet();
		return temp;
	}

	private HashSet<String> getOneTransferKillAndClear() {
		HashSet<String> temp = this.oneTransferKill;
		this.oneTransferKill = new HashSet();
		return temp;
	}

	// /////////////////////////////////////////////////////
	// operand
	@Override
	public void visit(cfg.operand.Int operand) {
		return;
	}

	@Override
	public void visit(cfg.operand.Var operand) {
		this.oneStmGen.add(operand.id);
		return;
	}

	// statements
	@Override
	public void visit(cfg.stm.Add s) {
		this.oneStmKill.add(s.dst);
		// Invariant: accept() of operand modifies "gen"
		s.left.accept(this);
		s.right.accept(this);
		return;
	}

	@Override
	public void visit(cfg.stm.InvokeVirtual s) {
		this.oneStmKill.add(s.dst);
		this.oneStmGen.add(s.obj);
		for (cfg.operand.T arg : s.args) {
			arg.accept(this);
		}
		return;
	}

	@Override
	public void visit(cfg.stm.Lt s) {
		this.oneStmKill.add(s.dst);
		// Invariant: accept() of operand modifies "gen"
		s.left.accept(this);
		s.right.accept(this);
		return;
	}

	@Override
	public void visit(cfg.stm.Move s) {
		this.oneStmKill.add(s.dst);
		// Invariant: accept() of operand modifies "gen"
		s.src.accept(this);
		return;
	}

	@Override
	public void visit(cfg.stm.NewObject s) {
		this.oneStmKill.add(s.dst);
		return;
	}

	@Override
	public void visit(cfg.stm.Print s) {
		s.arg.accept(this);
		return;
	}

	@Override
	public void visit(cfg.stm.Sub s) {
		this.oneStmKill.add(s.dst);
		// Invariant: accept() of operand modifies "gen"
		s.left.accept(this);
		s.right.accept(this);
		return;
	}

	@Override
	public void visit(cfg.stm.Times s) {
		this.oneStmKill.add(s.dst);
		// Invariant: accept() of operand modifies "gen"
		s.left.accept(this);
		s.right.accept(this);
		return;
	}

	// transfer
	@Override
	public void visit(cfg.transfer.If s) {
		// Invariant: accept() of operand modifies "gen"
		s.operand.accept(this);
		return;
	}

	@Override
	public void visit(cfg.transfer.Goto s) {
		return;
	}

	@Override
	public void visit(cfg.transfer.Return s) {
		// Invariant: accept() of operand modifies "gen"
		s.operand.accept(this);
		return;
	}

	// type
	@Override
	public void visit(cfg.type.Class t) {
	}

	@Override
	public void visit(cfg.type.Int t) {
	}

	@Override
	public void visit(cfg.type.IntArray t) {
	}

	// dec
	@Override
	public void visit(cfg.dec.Dec d) {
	}

	// utility functions:
	private void calculateStmTransferGenKill(cfg.block.Block b) {
		for (cfg.stm.T s : b.stms) {
			this.oneStmGen = new HashSet();
			this.oneStmKill = new HashSet();
			s.accept(this);
			this.stmGen.put(s, this.oneStmGen);
			this.stmKill.put(s, this.oneStmKill);
			if (control.Control.isTracing("liveness.step1")) {
				System.out.print("\ngen, kill for statement:");
				s.toString();
				System.out.print("\ngen is:");
				for (String str : this.oneStmGen) {
					System.out.print(str + ", ");
				}
				System.out.print("\nkill is:");
				for (String str : this.oneStmKill) {
					System.out.print(str + ", ");
				}
			}
		}
		this.oneTransferGen = new HashSet();
		this.oneTransferKill = new HashSet();
		b.transfer.accept(this);
		this.transferGen.put(b.transfer, this.oneTransferGen);
		this.transferKill.put(b.transfer, this.oneTransferGen);
		if (control.Control.isTracing("liveness.step1")) {
			System.out.print("\ngen, kill for transfer:");
			b.toString();
			System.out.print("\ngen is:");
			for (String str : this.oneTransferGen) {
				System.out.print(str + ", ");
			}
			System.out.println("\nkill is:");
			for (String str : this.oneTransferKill) {
				System.out.print(str + ", ");
			}
		}
		return;
	}

	// block
	@Override
	public void visit(cfg.block.Block b) {
		switch (this.kind) {
		case StmGenKill:
			calculateStmTransferGenKill(b);
			break;
		default:
			// Your code here:
			return;
		}
	}

	// method
	@Override
	public void visit(cfg.method.Method m) {
		// Four steps:
		// Step 1: calculate the "gen" and "kill" sets for each
		// statement and transfer
		this.kind = Liveness_Kind_t.StmGenKill;
		for (cfg.block.T block : m.blocks) {
			block.accept(this);
		}

		// Step 2: calculate the "gen" and "kill" sets for each block.
		// For this, you should visit statements and transfers in a
		// block in a reverse order.
		// Your code here:

		// Step 3: calculate the "liveIn" and "liveOut" sets for each block
		// Note that to speed up the calculation, you should first
		// calculate a reverse topo-sort order of the CFG blocks, and
		// crawl through the blocks in that order.
		// And also you should loop until a fix-point is reached.
		// Your code here:

		// Step 4: calculate the "liveIn" and "liveOut" sets for each
		// statement and transfer
		// Your code here:

	}

	@Override
	public void visit(cfg.mainMethod.MainMethod m) {
		// Four steps:
		// Step 1: calculate the "gen" and "kill" sets for each
		// statement and transfer
		this.kind = Liveness_Kind_t.StmGenKill;
		for (cfg.block.T block : m.blocks) {
			block.accept(this);
		}

		// Step 2: calculate the "gen" and "kill" sets for each block.
		// For this, you should visit statements and transfers in a
		// block in a reverse order.
		// Your code here:

		// Step 3: calculate the "liveIn" and "liveOut" sets for each block
		// Note that to speed up the calculation, you should first
		// calculate a reverse topo-sort order of the CFG blocks, and
		// crawl through the blocks in that order.
		// And also you should loop until a fix-point is reached.
		// Your code here:

		// Step 4: calculate the "liveIn" and "liveOut" sets for each
		// statement and transfer
		// Your code here:
	}

	// vtables
	@Override
	public void visit(cfg.vtable.Vtable v) {
	}

	// class
	@Override
	public void visit(cfg.classs.Class c) {
	}

	// program
	@Override
	public void visit(cfg.program.Program p) {
		p.mainMethod.accept(this);
		for (cfg.method.T mth : p.methods) {
			mth.accept(this);
		}
		return;
	}

}
