package elaborator;

import java.util.Hashtable;
import java.util.Iterator;

public class ClassBinding 
{
	public String extendss; // null for non-existing extends
	public Hashtable<String, VariableBinding> fields;
	public Hashtable<String, MethodType> methods;

	public ClassBinding(String extendss) 
	{
		this.extendss = extendss;
		this.fields = new Hashtable<String, VariableBinding>();
		this.methods = new Hashtable<String, MethodType>();
	}

	public ClassBinding(String extendss, Hashtable<String, VariableBinding> fields, Hashtable<String, MethodType> methods) 
	{
		this.extendss = extendss;
		this.fields = fields;
		this.methods = methods;
	}

	public void put(String xid, VariableBinding vb)
	{
		if(this.fields.get(xid) != null) 
		{
			System.out.println("duplicated class field: " + xid);
			System.exit(1);
		}
		this.fields.put(xid, vb);
	}
	
	public void put(String mid, MethodType mtype) 
	{
		if(this.methods.get(mid) != null) 
		{
			System.out.println("duplicated class method: " + mid);
			System.exit(1);
		}
		this.methods.put(mid, mtype);
	}
	
	public void put(String xid, boolean isUsed)
	{
		VariableBinding vb = this.fields.get(xid);
		vb.isUsed = isUsed;
	}
	
	@Override
	public String toString() 
	{
		System.out.print("extends: ");
		if(this.extendss != null)
			System.out.println(this.extendss);
		else
			System.out.println("<>");
		
		System.out.print("\nfields: ");
		Iterator<String> it = this.fields.keySet().iterator();
		if(!it.hasNext())
			System.out.print("<>");
		System.out.println();
		while(it.hasNext())
		{
			String key = (String)it.next();
			ast.type.T value = (ast.type.T)this.fields.get(key).type;
			System.out.println(key + ": " + value.toString());
		}
		
		System.out.print("\nmethods: ");
		it = this.methods.keySet().iterator();
		if(!it.hasNext())
			System.out.print("<>");
		System.out.println();
		while(it.hasNext())
		{
			String key = (String)it.next();
			MethodType value = (MethodType)this.methods.get(key);
			System.out.println(key + ": " + value.toString());
		}
		System.out.println();
		return "";
	}
}