package util;

import java.io.InputStream;

import lexer.Token;
import lexer.Token.Kind;

public class Todo
{
  public Todo(){}
  public Todo(int c)
  {
    System.out.println("You should add your code here:\n");
    throw new java.lang.Error ();
    
  }
  
  public String getString(int c){	
	  return String.valueOf(c);
  }
  
  public boolean isAlp(int c){
	  if((c>=65&c<=90)|(c>=97&c<=122))
		  return true;
	  else return false;
  }
  
  public boolean isNum(int c){
	  if(c>=48&c<=57)
		  return true;
	  else return false;
  }
}
