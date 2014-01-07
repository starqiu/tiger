package cfg.optimizations;

import java.util.HashMap;
import java.util.HashSet;

public class ReachingDefinition implements cfg.Visitor {
	// gen, kill for one statement
	private HashSet<cfg.stm.T> oneStmGen;
	private HashSet<cfg.stm.T> oneStmKill;

	// gen, kill for one transfer
	private HashSet<cfg.stm.T> oneTransferGen;
	private HashSet<cfg.stm.T> oneTransferKill;

	// gen, kill for statements
	private HashMap<cfg.stm.T, HashSet<cfg.stm.T>> stmGen;
	private HashMap<cfg.stm.T, HashSet<cfg.stm.T>> stmKill;

	// gen, kill for transfers
	private HashMap<cfg.transfer.T, HashSet<cfg.stm.T>> transferGen;
	private HashMap<cfg.transfer.T, HashSet<cfg.stm.T>> transferKill;

	// gen, kill for blocks
	private HashMap<cfg.block.T, HashSet<cfg.stm.T>> blockGen;
	private HashMap<cfg.block.T, HashSet<cfg.stm.T>> blockKill;

	// in, out for blocks
	private HashMap<cfg.block.T, HashSet<cfg.stm.T>> blockIn;
	private HashMap<cfg.block.T, HashSet<cfg.stm.T>> blockOut;

	// in, out for statements
	public HashMap<cfg.stm.T, HashSet<cfg.stm.T>> stmIn;
	public HashMap<cfg.stm.T, HashSet<cfg.stm.T>> stmOut;

	// liveIn, liveOut for transfer
	public HashMap<cfg.transfer.T, HashSet<cfg.stm.T>> transferIn;
	public HashMap<cfg.transfer.T, HashSet<cfg.stm.T>> transferOut;

	public ReachingDefinition() {
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

		this.blockIn = new HashMap();
		this.blockOut = new HashMap();

		this.stmIn = new HashMap();
		this.stmOut = new HashMap();

		this.transferIn = new HashMap();
		this.transferOut = new HashMap();
	}

	// /////////////////////////////////////////////////////
	// utilities

	// /////////////////////////////////////////////////////
	// operand
	@Override
	public void visit(cfg.operand.Int operand) {
		return;
	}

	@Override
	public void visit(cfg.operand.Var operand) {
		return;
	}

	// statements
	@Override
	public void visit(cfg.stm.Add s) {
		oneStmGen.add(s);
	}

	@Override
	public void visit(cfg.stm.InvokeVirtual s) {
		oneStmGen.add(s);
	}

	@Override
	public void visit(cfg.stm.Lt s) {
		oneStmGen.add(s);
	}

	@Override
	public void visit(cfg.stm.Move s) {
		oneStmGen.add(s);
	}

	@Override
	public void visit(cfg.stm.NewObject s) {
		oneStmGen.add(s);
	}

	@Override
	public void visit(cfg.stm.Print s) {
		oneStmGen.add(s);
	}

	@Override
	public void visit(cfg.stm.Sub s) {
		oneStmGen.add(s);
	}

	@Override
	public void visit(cfg.stm.Times s) {
		oneStmGen.add(s);
	}

	// transfer
	@Override
	public void visit(cfg.transfer.If s) {
	}

	@Override
	public void visit(cfg.transfer.Goto s) {
		return;
	}

	@Override
	public void visit(cfg.transfer.Return s) {
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

	// block
	@Override
	public void visit(cfg.block.Block b) {
	}

	// method
	@Override
	public void visit(cfg.method.Method m) {
		// Five steps:
		// Step 0: for each argument or local variable "x" in the
		// method m, calculate x's definition site set def(x).
		// Your code here:

		// Step 1: calculate the "gen" and "kill" sets for each
		// statement and transfer

		// Step 2: calculate the "gen" and "kill" sets for each block.
		// For this, you should visit statements and transfers in a
		// block sequentially.
		// Your code here:

		// Step 3: calculate the "in" and "out" sets for each block
		// Note that to speed up the calculation, you should use
		// a topo-sort order of the CFG blocks, and
		// crawl through the blocks in that order.
		// And also you should loop until a fix-point is reached.
		// Your code here:

		// Step 4: calculate the "in" and "out" sets for each
		// statement and transfer
		// Your code here:

	}

	@Override
	public void visit(cfg.mainMethod.MainMethod m) {
		// Five steps:
		// Step 0: for each argument or local variable "x" in the
		// method m, calculate x's definition site set def(x).
		// Your code here:

		// Step 1: calculate the "gen" and "kill" sets for each
		// statement and transfer

		// Step 2: calculate the "gen" and "kill" sets for each block.
		// For this, you should visit statements and transfers in a
		// block sequentially.
		// Your code here:

		// Step 3: calculate the "in" and "out" sets for each block
		// Note that to speed up the calculation, you should use
		// a topo-sort order of the CFG blocks, and
		// crawl through the blocks in that order.
		// And also you should loop until a fix-point is reached.
		// Your code here:

		// Step 4: calculate the "in" and "out" sets for each
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
	}

	@Override
	public void visit(cfg.type.Boolean t) {
	}

	@Override
	public void visit(cfg.stm.And and) {
	}

	@Override
	public void visit(cfg.stm.ArraySelect arraySelect) {
	}

	@Override
	public void visit(cfg.stm.Length length) {
	}

	@Override
	public void visit(cfg.stm.NewIntArray newIntArray) {
	}

	@Override
	public void visit(cfg.stm.Not not) {
	}

	@Override
	public void visit(cfg.stm.AssignArray assignArray) {
	}

	@Override
	public void visit(cfg.stm.Paren s) {
	}

}
