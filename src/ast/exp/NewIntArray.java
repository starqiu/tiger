package ast.exp;

public class NewIntArray extends T
{
  public T exp;
  public String type="int";

  public NewIntArray(T length)
  {
	this.exp = length;
  }

  @Override
  public void accept(ast.Visitor v)
  {
    v.visit(this);
    return;
  }
}
