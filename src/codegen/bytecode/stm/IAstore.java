package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

public class IAstore extends T {

	public IAstore() {
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
