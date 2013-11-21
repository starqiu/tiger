package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

public class IAload extends T
{
  public int index;

  public IAload(int index)
  {
	  this.index = index;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
