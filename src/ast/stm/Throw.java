package ast.stm;

public class Throw extends T {

	public String bufId;

	public Throw(String bufId) {
		this.bufId = bufId;
	}

	@Override
	public void accept(ast.Visitor v) {
		v.visit(this);
	}
}