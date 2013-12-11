package ast.exp;

public class Paren extends T
{
	public T exp;
	
	public Paren(T exp)
	{
		this.exp = exp;
	}
	
	public Paren(T exp, int lineNum)
	{
		this(exp);
		this.lineNum = lineNum;
	}
	
	@Override
	public void accept(ast.Visitor v) 
	{
		v.visit(this);
		return;
	}
}