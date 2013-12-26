package ast.stm;

public class TryCatch extends T {
	public String bufId;
	public T tryy;
	public T catchh;
	public int match=-1;

	public TryCatch(T tryy, T catchh) {
		this.tryy = tryy;
		this.catchh = catchh;
	}
	
	public TryCatch(int match, T tryy, T catchh) {
		this.match = match;
		this.tryy = tryy;
		this.catchh = catchh;
	}
	
	public TryCatch(String bufId, T tryy, T catchh) {
		this.bufId = bufId;
		this.tryy = tryy;
		this.catchh = catchh;
	}

	@Override
	public void accept(ast.Visitor v) {
		v.visit(this);
	}
}