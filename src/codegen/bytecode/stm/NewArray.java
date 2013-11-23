package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

public class NewArray extends T {
	public String type = "int";

	public NewArray(String type) {
		this.type = type;
	}

	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}

}
