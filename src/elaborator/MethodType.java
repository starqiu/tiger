package elaborator;

import java.util.LinkedList;

public class MethodType
{
	public ast.type.T retType;// the type of return
	public LinkedList<ast.dec.T> argsType;// the type of arguments

	public MethodType(ast.type.T retType, LinkedList<ast.dec.T> decs) 
	{
		this.retType = retType;
		this.argsType = decs;
	}

	@Override
	public String toString() 
	{
		String s = "";
		for(ast.dec.T dec : this.argsType) 
		{
			ast.dec.Dec decc = (ast.dec.Dec)dec;
			s = decc.type.toString() + "*" + s;
		}
		s = s + " -> " + this.retType.toString();
		return s;
	}
}