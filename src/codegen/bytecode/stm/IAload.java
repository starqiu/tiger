package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

public class IAload extends T {
	public int arrayIndex;
	public int index;

	public IAload(int arrayIndex, int index) {
		this.arrayIndex = arrayIndex;
		this.index = index;
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
