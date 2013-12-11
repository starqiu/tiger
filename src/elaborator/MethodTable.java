package elaborator;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

public class MethodTable 
{
	private Hashtable<String, VariableBinding> table;

	public MethodTable()
	{
		this.table = new Hashtable<String, VariableBinding>();
	}

	// Duplication is not allowed
	public void put(LinkedList<ast.dec.T> formals, LinkedList<ast.dec.T> locals)
	{
		for(ast.dec.T dec : formals) 
		{
			ast.dec.Dec decc = (ast.dec.Dec)dec;
			if(this.table.get(decc.id) != null) 
			{
				System.out.println("duplicated parameter: " + decc.id);
				System.exit(1);
			}
			this.table.put(decc.id, new VariableBinding(decc.type, true, false));// the formals can be regarded as initialized
		}
		
		for(ast.dec.T dec : locals) 
		{
			ast.dec.Dec decc = (ast.dec.Dec)dec;
			if(this.table.get(decc.id) != null) 
			{
				System.out.println("duplicated variable: " + decc.id);
				System.exit(1);
			}
			this.table.put(decc.id, new VariableBinding(decc.type, false, false, decc.lineNum));
		}
	}

	public void putInit(String id, boolean isInitialized)
	{
		VariableBinding vb = this.table.get(id);
		vb.isInitialized = isInitialized;
	}
	
	public void putUse(String id, boolean isUsed)
	{
		VariableBinding vb = this.table.get(id);
		vb.isUsed = isUsed;
	}
	
	public Hashtable<String, VariableBinding> getTable()
	{
		return this.table;
	}
	
	// return null for non-existing keys
	public ast.type.T get(String id) 
	{
		if(this.table.get(id) != null)
			return this.table.get(id).type;
		else
			return null;
	}

	public boolean getInit(String id)
	{
		return this.table.get(id).isInitialized;
	}
	
	public boolean getUse(String id)
	{
		return this.table.get(id).isUsed;
	}
	
	// return null for non-existing variable
	public void dump() 
	{
		Iterator<String> it = this.table.keySet().iterator();
		while(it.hasNext())
		{
			String key = (String)it.next();
			ast.type.T value = (ast.type.T)this.table.get(key).type;
			System.out.println(key + ": " + value.toString());
		}
		System.out.println();
	}
	
	@Override
	public String toString() 
	{
		return this.table.toString();
	}
}