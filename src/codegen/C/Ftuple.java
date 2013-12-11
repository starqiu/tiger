package codegen.C;

import java.util.LinkedList;

public class Ftuple
{
	public String className;// name of the class
	public codegen.C.type.T ret;// type of the return
	public LinkedList<codegen.C.dec.T> args;// type of args
	public String id;// name of the field or method

	public Ftuple(String className, codegen.C.type.T ret,
			LinkedList<codegen.C.dec.T> args, String id) 
	{
		this.className = className;
		this.ret = ret;
		this.args = args;
		this.id = id;
	}
	
	@Override
	// This is a specialized version of "equals", for
	// it compares whether the second field is equal,
	// but ignores the first field.
	public boolean equals(Object t) 
	{
		if (t == null)
			return false;

		if (!(t instanceof Ftuple))
			return false;

		return this.id.equals(((Ftuple)t).id);
	}
}