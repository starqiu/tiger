package ast.stm;

public class Throw extends T {

	public String bufId;
	public int match=-1;
	
	public Throw() {
	}
	
	public Throw(String bufId) {
		this.bufId = bufId;
	}
	
	public Throw(int match) {
		this.match = match;
	}

	@Override
	public void accept(ast.Visitor v) {
		v.visit(this);
	}
}