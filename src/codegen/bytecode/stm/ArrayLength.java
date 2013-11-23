package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

public class ArrayLength extends T {
	public int arrayIndex;
	public int index;

	public ArrayLength(int arrayIndex, int index) {
		this.arrayIndex = arrayIndex;
		this.index = index;
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
}
