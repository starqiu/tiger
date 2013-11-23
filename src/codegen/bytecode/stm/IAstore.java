package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

public class IAstore extends T {
	public int arrayIndex;
	public int index;

	public IAstore(int arrayIndex, int index) {
		this.arrayIndex = arrayIndex;
		this.index = index;
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
