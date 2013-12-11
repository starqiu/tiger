package ast.dec;

import ast.Visitor;

public class Dec extends T
{
	public ast.type.T type;
	public String id;
	public boolean isField;

	public Dec(ast.type.T type, String id) 
	{
		this.type = type;
		this.id = id;
	}
	
	public Dec(ast.type.T type, String id, int lineNum, boolean isField)
	{
		this(type, id);
		this.lineNum = lineNum;
		this.isField = isField;
	}

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
