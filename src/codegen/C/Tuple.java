package codegen.C;

public class Tuple 
{
	public String className;// name of the class
	public codegen.C.type.T type;// type of the field
	public String id;// name of the field or method
	
	public Tuple(String className, codegen.C.type.T type, String id) 
	{
		this.className = className;
		this.type = type;
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

		if (!(t instanceof Tuple))
			return false;

		return this.id.equals(((Tuple) t).id);
	}
}