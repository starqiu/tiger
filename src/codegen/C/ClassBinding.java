package codegen.C;

import java.util.LinkedList;
import java.util.ArrayList;

public class ClassBinding 
{
	public String extendss;// null for non-existing extends
	public boolean visited;// whether or not this class has been visited
	public LinkedList<Tuple> fields;// all fields
	public ArrayList<Ftuple> methods;// all methods

	public ClassBinding(String extendss) 
	{
		this.extendss = extendss;
		this.visited = false;
		this.fields = new LinkedList<Tuple>();
		this.methods = new ArrayList<Ftuple>();
	}

	// put a single field
	public void put(String c, codegen.C.type.T type, String var) 
	{
		this.fields.add(new Tuple(c, type, var));
	}

	public void put(Tuple t) 
	{
		this.fields.add(t);
	}

	public void update(LinkedList<Tuple> fs) 
	{
		this.fields = fs;
	}

	public void update(ArrayList<Ftuple> ms) 
	{
		this.methods = ms;
	}

	public void putm(String c, codegen.C.type.T ret, 
			LinkedList<codegen.C.dec.T> args, String mthd)
	{
		Ftuple t = new Ftuple(c, ret, args, mthd);
		this.methods.add(t);
		return;
	}

	@Override
	public String toString() 
	{
		System.out.print("extends: ");
		if (this.extendss != null)
			System.out.println(this.extendss);
		else
			System.out.println("<>");
		System.out.println("\nfields:\n  ");
		System.out.println(fields.toString());
		System.out.println("\nmethods:\n  ");
		System.out.println(methods.toString());

		return "";
	}
}