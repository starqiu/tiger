package cfg.stm;

import cfg.Visitor;

public class Paren extends T
{
	public T exp;
	public String dst;
	
	public Paren(T exp)
	{
		this.exp = exp;
	}
	
	public Paren(String dst,T exp)
	{
		this.dst = dst;
		this.exp = exp;
	}
	
	@Override
	public void accept(Visitor v) 
	{
		v.visit(this);
		return;
	}
}