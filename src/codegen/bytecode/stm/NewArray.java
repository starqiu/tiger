package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

public class NewArray extends T
{
  public int count;

  public NewArray(int count)
  {
    this.count = count;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
