package elaborator;

public class VariableBinding 
{
	public ast.type.T type;
	public boolean isInitialized;
	public boolean isUsed;
	public int lineNum;
	
	// for fields of a class, because it is ok that the fields are not initialize
	public VariableBinding(ast.type.T type, boolean isUsed, int lineNum)
	{
		this.type = type;
		this.isInitialized = false;
		this.isUsed = isUsed;
		this.lineNum = lineNum;
	}
	
	// for foramls of a method, because there is no need to know their lineNums
	public VariableBinding(ast.type.T type, boolean isInitialized, boolean isUsed)
	{
		this.type = type;
		this.isInitialized = isInitialized;
		this.isUsed = isUsed;
	}
	
	public VariableBinding(ast.type.T type, boolean isInitialized, boolean isUsed, int lineNum)
	{
		this.type = type;
		this.isInitialized = isInitialized;
		this.isUsed = isUsed;
		this.lineNum = lineNum;
	}
}