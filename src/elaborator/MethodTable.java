package elaborator;

import java.util.Set;
import java.util.Map.Entry;


public class MethodTable
{
  private java.util.Hashtable<String, ast.type.T> table;

  /**
 * @return the table
 */
public java.util.Hashtable<String, ast.type.T> getTable() {
	return table;
}

public MethodTable()
  {
    this.table = new java.util.Hashtable<String, ast.type.T>();
  }

  // Duplication is not allowed
  public void put(java.util.LinkedList<ast.dec.T> formals,
      java.util.LinkedList<ast.dec.T> locals)
  {
    for (ast.dec.T dec : formals) {
      ast.dec.Dec decc = (ast.dec.Dec) dec;
      if (this.table.get(decc.id) != null) {
        System.err.println("error: duplicated parameter: " + decc.id);
        //System.exit(1);
      }
      this.table.put(decc.id, decc.type);
    }

    for (ast.dec.T dec : locals) {
      ast.dec.Dec decc = (ast.dec.Dec) dec;
      if (this.table.get(decc.id) != null) {
        System.err.println("error: duplicated variable: " + decc.id);
        //System.exit(1);
      }
      this.table.put(decc.id, decc.type);
    }

  }

  // return null for non-existing keys
  public ast.type.T get(String id)
  {
	ast.type.T type = this.table.get(id);
	if (null != type) {
		type.callTimes++; 
	}
    return type;
  }

  public void dump()
  {
      //new Todo();
	  System.out.println("dump the method table start!");
	  Set<Entry<String, ast.type.T>> mSet = table.entrySet();
	    for (Entry<String, ast.type.T> typeEnr : mSet) {
			System.out.print("  ");
			System.out.println(typeEnr.getKey()+" : "+typeEnr.getValue() +" call "
					+typeEnr.getValue().callTimes +" times ");
		}
	    System.out.println("dump the method table end!");
  }

  @Override
  public String toString()
  {
    return this.table.toString();
  }
}
