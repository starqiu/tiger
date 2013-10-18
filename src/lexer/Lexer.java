package lexer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

import util.Todo;

import lexer.Token.Kind;
import util.*;

public class Lexer
{
  public static final int maxtoken = 20;
  String fname; // the input file name to be compiled
  BufferedInputStream fstream; // input stream for the above file
  int lineNum;
  String buf =null ;
 
  //BufferedReader breader;

  public Lexer(String fname, BufferedInputStream fstream)
  {
    this.fname = fname;
    this.fstream = fstream;
    this.lineNum = 1;
  }
  public int bfRead(){
	 try {
		return this.fstream.read();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 return -1; 
  }
  
  public void bfMark(){
	  try {
		this.fstream.mark(maxtoken);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
  
  public void bfReset(){
	  try {
		this.fstream.reset();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
  // When called, return the next token (refer to the code "Token.java")
  // from the input stream.
  // Return TOKEN_EOF when reaching the end of the input stream.
  private Token nextTokenInternal() throws Exception
  {
	//BufferedReader b =new BufferedReader();
    int c = this.bfRead();
    
    if (-1 == c)
      // The value for "lineNum" is now "null",
      // you should modify this to an appropriate
      // line number for the "EOF" token.
      return new Token(Kind.TOKEN_EOF, null);

    // skip all kinds of "blanks"
    while (' ' == c || '\t' == c || '\n' == c || '\r'==c||c=='/') {
      if(c=='/')
    	  while(c!='\n')
    		  c =this.bfRead();  
      if(c=='\n')
    	  lineNum++;
      
      c = this.bfRead();
    } 
       
    switch (c) {
    case '+':
    	return new Token(Kind.TOKEN_ADD, lineNum);
    case '=':
    	return new Token(Kind.TOKEN_ASSIGN,lineNum);
    case ',':
    	return new Token(Kind.TOKEN_COMMER,lineNum);
    case '.':
    	return new Token(Kind.TOKEN_DOT,lineNum);
    case '{':
    	return new Token(Kind.TOKEN_LBRACE,lineNum);
    case '[':
    	return new Token(Kind.TOKEN_LBRACK,lineNum);
    case '(':
    	return new Token(Kind.TOKEN_LPAREN,lineNum);
    case '<':
    	return new Token(Kind.TOKEN_LT,lineNum);
    case '!':
    	return new Token(Kind.TOKEN_NOT,lineNum);
    case '}':
    	return new Token(Kind.TOKEN_RBRACE,lineNum);
    case ']':
    	return new Token(Kind.TOKEN_RBRACK,lineNum);
    case ')':
    	return new Token(Kind.TOKEN_RPAREN,lineNum);
    case ';':
    	return new Token(Kind.TOKEN_SEMI,lineNum);
    case '-':
    	return new Token(Kind.TOKEN_SUB,lineNum);
    case '*':
    	return new Token(Kind.TOKEN_TIMES,lineNum);
    case '&':
    	this.bfMark();
    	if(this.bfRead()=='&'){
    		return new Token(Kind.TOKEN_AND,lineNum);
    	}else this.bfReset();

   	
    default:
      // Lab 1, exercise 2: supply missing code to
      // lex other kinds of tokens.
      // Hint: think carefully about the basic
      // data structure and algorithms. The code
      // is not that much and may be less than 50 lines. If you
      // find you are writing a lot of code, you
      // are on the wrong way.

      Todo todo=new Todo();  
      
      if(todo.isNum(c)){
    	  char cc[]=new char[20];
    	  for(int i= 0;todo.isNum(c)|c=='.';i++){
    		  cc[i]=(char)c;
    		  this.bfMark();
    		  c = this.bfRead();
    	  }
    	  this.bfReset();
    	  this.buf = String.valueOf(cc).trim();
    	  return new Token(Kind.TOKEN_NUM,lineNum,buf);
      }
      
      
      if(todo.isAlp(c)){
    	  char cc[]=new char [20];
    	  for(int i = 0;todo.isAlp(c)|todo.isNum(c)|c=='_';i++){
    		  cc[i] = (char)c;
    		  this.bfMark();
    		  c = this.bfRead();
    	  } 
    	  this.bfReset();
    	  this.buf = String.valueOf(cc).trim();
   
	      if(buf .equals("boolean"))
	    	  return new Token(Kind.TOKEN_BOOLEAN,lineNum);
	      if(buf .equals("class"))
	    	  return new Token(Kind.TOKEN_CLASS,lineNum);
	      if(buf .equals("else"))
	    	  return new Token(Kind.TOKEN_ELSE,lineNum);
	      if(buf .equals("extends"))
	    	  return new Token(Kind.TOKEN_EXTENDS,lineNum);
	      if(buf .equals("false"))
	    	  return new Token(Kind.TOKEN_FALSE,lineNum);
	      if(buf .equals("if"))
	    	  return new Token(Kind.TOKEN_IF,lineNum);
	      if(buf .equals("int"))
	    	  return new Token(Kind.TOKEN_INT,lineNum);
	      if(buf .equals("length"))
	    	  return new Token(Kind.TOKEN_LENGTH,lineNum);
	      if(buf .equals("main"))
	    	  return new Token(Kind.TOKEN_MAIN,lineNum);
	      if(buf .equals("new"))
	    	  return new Token(Kind.TOKEN_NEW,lineNum);
	      if(buf .equals("out"))
	    	  return new Token(Kind.TOKEN_OUT,lineNum);
	      if(buf .equals("println"))
	    	  return new Token(Kind.TOKEN_PRINTLN,lineNum);
	      if(buf .equals("public"))
	    	  return new Token(Kind.TOKEN_PUBLIC,lineNum);
	      if(buf .equals("return"))
	    	  return new Token(Kind.TOKEN_RETURN,lineNum);
	      if(buf .equals("static"))
	    	  return new Token(Kind.TOKEN_STATIC,lineNum);
	      if(buf .equals("String"))
	    	  return new Token(Kind.TOKEN_STRING,lineNum);
	      if(buf .equals("System"))
	    	  return new Token(Kind.TOKEN_SYSTEM,lineNum);
	      if(buf .equals("this"))
	    	  return new Token(Kind.TOKEN_THIS,lineNum);
	      if(buf .equals("true"))
	    	  return new Token(Kind.TOKEN_TRUE,lineNum);
	      if(buf .equals("void"))
	    	  return new Token(Kind.TOKEN_VOID,lineNum);
	      if(buf .equals("while"))
	    	  return new Token(Kind.TOKEN_WHILE,lineNum);    
	      if(buf != null) 
	    	  return new Token(Kind.TOKEN_ID,lineNum,buf);
      }else return new Token(Kind.TOKEN_EOF,lineNum,buf);
      return null;
    }
  }

  public Token nextToken()
  {
    Token t = null;
  
    try {
      t = this.nextTokenInternal();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    if (control.Control.lex&&t.kind!=Kind.TOKEN_EOF)
      System.out.println(t.toString());
    return t;
  }
}
