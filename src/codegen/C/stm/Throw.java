package codegen.C.stm;

import codegen.C.Visitor;

public class Throw extends T {
	
	public int match=-1;//the type of exception

	public Throw() {
	}
	
	public Throw(int match) {
		this.match = match;
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}