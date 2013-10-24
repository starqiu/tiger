package elaborator;

import java.util.Set;
import java.util.Map.Entry;


public class ClassTable
{
  // map each class name (a string), to the class bindings.
  private java.util.Hashtable<String, ClassBinding> table;

  /**
 * @return the table
 */
public java.util.Hashtable<String, ClassBinding> getTable() {
	return table;
}

public ClassTable()
  {
    this.table = new java.util.Hashtable<String, ClassBinding>();
  }

  // Duplication is not allowed
  public void put(String c, ClassBinding cb)
  {
    if (this.table.get(c) != null) {
      System.err.println("error: duplicated class: " + c);
      //System.exit(1);
    }
    this.table.put(c, cb);
  }

  // put a field into this table
  // Duplication is not allowed
  public void put(String c, String id, ast.type.T type)
  {
    ClassBinding cb = this.table.get(c);
    cb.put(id, type);
    return;
  }

  // put a method into this table
  // Duplication is not allowed.
  // Also note that MiniJava does NOT allow overloading.
  public void put(String c, String id, MethodType type)
  {
    ClassBinding cb = this.table.get(c);
    cb.put(id, type);
    return;
  }

  // return null for non-existing class
  public ClassBinding get(String className)
  {
    return this.table.get(className);
  }

  // get type of some field
  // return null for non-existing field.
  public ast.type.T get(String className, String xid)
  {
    ClassBinding cb = this.table.get(className);
    ast.type.T type = cb.fields.get(xid);
    while (type == null) { // search all parent classes until found or fail
      if (cb.extendss == null)
        return type;

      cb = this.table.get(cb.extendss);
      type = cb.fields.get(xid);
    }
    if (null != type) {
		type.callTimes++; 
	}
    return type;
  }

  // get type of some method
  // return null for non-existing method
  public MethodType getm(String className, String mid)
  {
    ClassBinding cb = this.table.get(className);
    MethodType type = cb.methods.get(mid);
    while (type == null) { // search all parent classes until found or fail
      if (cb.extendss == null)
        return type;

      cb = this.table.get(cb.extendss);
      type = cb.methods.get(mid);
    }
    if (null != type) {
		type.callTimes++; 
	}
    return type;
  }

  public void dump()
  {
      //new Todo();
	  System.out.println("dump the class table!!");
	  Set<Entry<String, ClassBinding>> cSet = table.entrySet();
	    for (Entry<String, ClassBinding> entry : cSet) {
	    	System.out.println("class name :"+entry.getKey()+" --");
	    	Set<Entry<String, ast.type.T>> fieldSet = entry.getValue().fields.entrySet();
	    	System.out.println("  field:");
	    	for (Entry<String, ast.type.T> typeEnr : fieldSet) {
	    		System.out.print("  ");
				System.out.println(typeEnr.getKey()+" : "+typeEnr.getValue()+" call "
						+typeEnr.getValue().callTimes +" times ");
			}
	    	System.out.println("  method:");
			Set<Entry<String, elaborator.MethodType>> methodSet = entry.getValue().methods.entrySet();
			for (Entry<String, elaborator.MethodType> typeEnr : methodSet) {
				System.out.print("  ");
				System.out.println(typeEnr.getKey()+" : "+typeEnr.getValue()+" call "
						+typeEnr.getValue().callTimes +" times ");
			}
		} 
	    System.out.println("dump the class table end!");
  }

  @Override
  public String toString()
  {
    return this.table.toString();
  }
}
