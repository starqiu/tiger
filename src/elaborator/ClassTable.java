package elaborator;

import java.util.Hashtable;
import java.util.Iterator;

public class ClassTable 
{
	// map each class name(a string) to the class bindings
	private Hashtable<String, ClassBinding> table;

	public ClassTable() 
	{
		this.table = new Hashtable<String, ClassBinding>();
	}

	// Duplication is not allowed
	public void put(String c, ClassBinding cb) 
	{
		if(this.table.get(c) != null) 
		{
			System.out.println("duplicated class: " + c);
			System.exit(1);
		}
		this.table.put(c, cb);
	}

	// put a field into this table
	// Duplication is not allowed
	public void put(String c, String id, VariableBinding vb)
	{
		ClassBinding cb = this.table.get(c);
		cb.put(id, vb);
		return;
	}
	
	// put a method into this table
	// Duplication is not allowed.
	// Also note that MiniJava does NOT allow overloading.
	public void put(String c, String id, MethodType mtype) 
	{
		ClassBinding cb = this.table.get(c);
		cb.put(id, mtype);
		return;
	}

	public void putUse(String c, String id, boolean isUsed)
	{
		ClassBinding cb = this.table.get(c);
		cb.put(id, isUsed);
	}
	
	public Hashtable<String, ClassBinding> getTable()
	{
		return this.table;
	}
	
	// return null for non-existing class
	public ClassBinding get(String className) 
	{
		return this.table.get(className);
	}

	// get type of some field
	// return null for non-existing field
	public ast.type.T get(StringBuffer className, String xid) 
	{
		ClassBinding cb = this.table.get(className.toString());
		if(cb == null)
			return null;
		
		VariableBinding vb = cb.fields.get(xid);
		
		while(vb == null)// search all parent classes until found or fail
		{ 
			if(cb.extendss == null)
				return null;

			String temp = cb.extendss;
			cb = this.table.get(cb.extendss);
			vb = cb.fields.get(xid);
			className.setLength(0);
			className.append(temp);
		}
		
		return vb.type;
	}

	// get type of some method
	// return null for non-existing method
	public MethodType getm(String className, String mid) 
	{
		ClassBinding cb = this.table.get(className);
		if(cb == null)
			return null;
		
		MethodType type = cb.methods.get(mid);
		while(type == null)// search all parent classes until found or fail
		{ 
			if(cb.extendss == null)
				return type;

			cb = this.table.get(cb.extendss);
			type = cb.methods.get(mid);
		}
		return type;
	}
	
	public boolean getUse(String className, String xid)
	{
		ClassBinding cb = this.table.get(className);
		VariableBinding vb = cb.fields.get(xid);
		return vb.isUsed;
	}
	
	public void dump() 
	{
		Iterator<String> it = this.table.keySet().iterator();
		while(it.hasNext())
		{
			String key = (String)it.next();
			ClassBinding value = (ClassBinding)this.table.get(key);
			System.out.println("class " + key + ":");
			value.toString();
		}
	}

	@Override
	public String toString() 
	{
		return this.table.toString();
	}
}