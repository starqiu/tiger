package ast.stm;

public class Assign extends T 
{
	public ast.exp.Id id;
	public ast.exp.T exp;
	public ast.type.T type;// type of the id
	public int lineNum;

	public Assign(ast.exp.Id id, ast.exp.T exp) 
	{
		this.id = id;
		this.exp = exp;
		this.type = null;
	}
	
	public Assign(ast.exp.Id id, ast.exp.T exp, int lineNum) 
	{
		this(id, exp);
		this.lineNum = lineNum;
	}

	@Override
	public void accept(ast.Visitor v) 
	{
		v.visit(this);
	}
}