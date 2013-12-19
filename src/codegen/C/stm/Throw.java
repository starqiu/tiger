package codegen.C.stm;

import codegen.C.Visitor;

public class Throw extends T {
	
	public String bufId;

	public Throw(String bufId) {
		this.bufId = bufId;
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}