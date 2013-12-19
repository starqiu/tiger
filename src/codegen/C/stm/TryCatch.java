package codegen.C.stm;

import codegen.C.Visitor;

public class TryCatch extends T {
	public String bufId;
	public T tryy;
	public T catchh;

	public TryCatch(String bufId, T tryy, T catchh) {
		this.bufId = bufId;
		this.tryy = tryy;
		this.catchh = catchh;
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}