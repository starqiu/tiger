package codegen.C.stm;

import codegen.C.Visitor;

public class TryCatch extends T {
	public int match = -1;
	public String bufId;
	public T tryy;
	public T catchh;

	public TryCatch(String bufId, T tryy, T catchh) {
		this.bufId = bufId;
		this.tryy = tryy;
		this.catchh = catchh;
	}
	
	public TryCatch(int match, T tryy, T catchh) {
		this.match = match;
		this.tryy = tryy;
		this.catchh = catchh;
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}