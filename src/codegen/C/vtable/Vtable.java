package codegen.C.vtable;

import codegen.C.Visitor;

import java.util.ArrayList;

public class Vtable extends T 
{
	public String id;// name of the class
	public String gc_map;
	public ArrayList<codegen.C.Ftuple> ms;// all methods

	public Vtable(String id, ArrayList<codegen.C.Ftuple> ms) 
	{
		this.id = id;
		this.ms = ms;
	}
	
	public Vtable(String id, String gc_map, ArrayList<codegen.C.Ftuple> ms) 
	{
		this.id = id;
		this.gc_map = gc_map;
		this.ms = ms;
	}

	@Override
	public void accept(Visitor v) 
	{
		v.visit(this);
	}
}