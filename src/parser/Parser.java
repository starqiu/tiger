package parser;

import java.io.BufferedInputStream;

import lexer.Lexer;
import lexer.Token;
import lexer.Token.Kind;

public class Parser
{
  Lexer lexer;
  Token current;
  String filename;
  
  public Parser(String fname, BufferedInputStream fstream)
  {
	filename = fname;
    lexer = new Lexer(fname, fstream);
    current = lexer.nextToken();
  }

  // /////////////////////////////////////////////
  // utility methods to connect the lexer
  // and the parser.

  private void advance()
  {
    current = lexer.nextToken();
  }

  private void eatToken(Kind kind)
  {
    if (kind == current.kind)
      advance();
    else {
    	

//        System.out.println("Expects: " + kind.toString());
//        System.out.println("But got: " + current.kind.toString());
        error(kind.toString(),this.current.kind.toString());
//      System.exit(1);
    }
  }

  private void error(String mstr)
  {
	String  tokenstr=this.current.kind.toString();
	String  classstr=this.getClass().getName();
	Integer linenum=this.current.lineNum;
	String methodstr = mstr ;
    System.out.println("Syntax error: compilation aborting...\n");
    System.out.println("Syntax error in file <"+filename+">\n");
    System.out.println("   The error exist in the line "+linenum+"\n");
    System.out.println("   The error near by the token "+tokenstr+"\n");
    System.out.println("       The thread aborted in the method "+classstr+"."+methodstr+"()"+"\n");
    
    System.exit(1);
    return;
  }
  private void error(String e,String b){
	  String  tokenstr=this.current.kind.toString();
		String  classstr=this.getClass().getName();
		Integer linenum=this.current.lineNum;
	    System.out.println("Syntax error: compilation aborting...\n");
	    System.out.println("Syntax error in file <"+filename+">\n");
	    System.out.println("   The error exist in the line "+linenum+"\n");
	    System.out.println("   The error near by the token "+tokenstr+"\n");
	    System.out.println("       The thread aborted in the method "+classstr+"."+Thread.currentThread().getStackTrace()[2].getMethodName()+"()"+"\n");
	    System.out.println("			The method expects: " + e);
	    System.out.println("			           but got: " + b);
	    System.exit(1);
	    return;
  }

  // ////////////////////////////////////////////////////////////
  // below are method for parsing.

  // A bunch of parsing methods to parse expressions. The messy
  // parts are to deal with precedence and associativity.

  // ExpList -> Exp ExpRest*
  // ->
  // ExpRest -> , Exp
  private void parseExpList()
  {
    if (current.kind == Kind.TOKEN_RPAREN)//")"
      return;
    parseExp();
    while (current.kind == Kind.TOKEN_COMMER) {//","
      advance();
      parseExp();
    }
    return;
  }

  // AtomExp -> (exp)
  // -> INTEGER_LITERAL
  // -> true
  // -> false
  // -> this
  // -> id
  // -> new int [exp]
  // -> new id ()
  private void parseAtomExp()
  {
    switch (current.kind) {
    case TOKEN_LPAREN:
      advance();
      parseExp();
      eatToken(Kind.TOKEN_RPAREN);
      return;
    case TOKEN_NUM:
      advance();
      return;
    case TOKEN_TRUE:
      advance();
      return;
    case TOKEN_FALSE:
      this.advance();
      return;
    case TOKEN_THIS:
      advance();
      return;
    case TOKEN_ID:
      advance();
      return;
    case TOKEN_NEW: {
      advance();
      switch (current.kind) {
      case TOKEN_INT:
        advance();
        eatToken(Kind.TOKEN_LBRACK);
        parseExp();
        eatToken(Kind.TOKEN_RBRACK);
        return;
      case TOKEN_ID:
        advance();
        eatToken(Kind.TOKEN_LPAREN);
        eatToken(Kind.TOKEN_RPAREN);
        return;
      default:
    	
        error(Thread.currentThread().getStackTrace()[2].getMethodName());
        return;
      }
    }
    default:
      error(Thread.currentThread().getStackTrace()[2].getMethodName());
      return;
    }
  }

  // NotExp -> AtomExp
  // -> AtomExp .id (expList)
  // -> AtomExp [exp]
  // -> AtomExp .length
  private void parseNotExp()
  {
    parseAtomExp();
    while (current.kind == Kind.TOKEN_DOT || current.kind == Kind.TOKEN_LBRACK) {//[
      if (current.kind == Kind.TOKEN_DOT) {
        advance();
        if (current.kind == Kind.TOKEN_LENGTH) {
          advance();
          return;
        }
        eatToken(Kind.TOKEN_ID);
        eatToken(Kind.TOKEN_LPAREN);
        this.parseExpList();
        eatToken(Kind.TOKEN_RPAREN);
      } else {
        advance();
        parseExp();
        eatToken(Kind.TOKEN_RBRACK);
      }
    }
    return;
  }

  // TimesExp -> ! TimesExp
  // -> NotExp
  private void parseTimesExp()
  {
    while (current.kind == Kind.TOKEN_NOT) {
      advance();
    }
    parseNotExp();
    return;
  }

  // AddSubExp -> TimesExp * TimesExp
  // -> TimesExp
  private void parseAddSubExp()
  {
    parseTimesExp();
    while (current.kind == Kind.TOKEN_TIMES) {
      advance();
      parseTimesExp();
    }
    return;
  }

  // LtExp -> AddSubExp + AddSubExp
  // -> AddSubExp - AddSubExp
  // -> AddSubExp
  private void parseLtExp()
  {
    parseAddSubExp();
    while (current.kind == Kind.TOKEN_ADD || current.kind == Kind.TOKEN_SUB) {
      advance();
      parseAddSubExp();
    }
    return;
  }

  // AndExp -> LtExp < LtExp
  // -> LtExp
  private void parseAndExp()
  {
    parseLtExp();
    while (current.kind == Kind.TOKEN_LT) {
      advance();
      parseLtExp();
    }
    return;
  }

  // Exp -> AndExp && AndExp
  // -> AndExp
  private void parseExp()
  {
    parseAndExp();
    while (current.kind == Kind.TOKEN_AND) {
      advance();
      parseAndExp();
    }
    return;
  }

  // Statement -> { Statement* }
  // -> if ( Exp ) Statement else Statement
  // -> while ( Exp ) Statement
  // -> System.out.println ( Exp ) ;
  // -> id = Exp ;
  // -> id [ Exp ]= Exp ;
  private void parseStatement()
  {
	switch(current.kind){
	case TOKEN_LBRACE:
		this.advance();
		this.parseStatements();
		this.eatToken(Kind.TOKEN_RBRACE	);
		return ;
	case TOKEN_IF:
		this.advance();
		this.eatToken(Kind.TOKEN_LPAREN);
		this.parseExp();
		this.eatToken(Kind.TOKEN_RPAREN);
		this.parseStatement();
		this.eatToken(Kind.TOKEN_ELSE);
		this.parseStatement();
		return ;
	case TOKEN_WHILE:
		this.advance();
		this.eatToken(Kind.TOKEN_LPAREN);
		this.parseExp();
		this.eatToken(Kind.TOKEN_RPAREN);
		this.parseStatement();
		return ;
	case TOKEN_SYSTEM:
		this.advance();
		this.eatToken(Kind.TOKEN_DOT);
		this.eatToken(Kind.TOKEN_OUT);
		this.eatToken(Kind.TOKEN_DOT);
		this.eatToken(Kind.TOKEN_PRINTLN);
		this.eatToken(Kind.TOKEN_LPAREN);
		this.parseExp();
		this.eatToken(Kind.TOKEN_RPAREN);
		this.eatToken(Kind.TOKEN_SEMI);
		return ;
	case TOKEN_ID:
		this.advance();
		switch(this.current.kind){
		case TOKEN_ASSIGN:
			this.advance();
			this.parseExp();
			this.eatToken(Kind.TOKEN_SEMI);
			return;
		case TOKEN_LBRACK:
			this.advance();
			this.parseExp();
			this.eatToken(Kind.TOKEN_RBRACK);
			this.eatToken(Kind.TOKEN_ASSIGN);
			this.parseExp();
			this.eatToken(Kind.TOKEN_SEMI);
			return;
		default:
			error(Thread.currentThread().getStackTrace()[2].getMethodName());
			return ;
		}
	case TOKEN_ASSIGN:
		this.advance();
		this.parseExpList();
		this.eatToken(Kind.TOKEN_SEMI);
		return ;
	case TOKEN_LBRACK:
		this.parseExp();
		this.eatToken(Kind.TOKEN_RBRACK);
		this.eatToken(Kind.TOKEN_ASSIGN);
		this.parseExp();
		this.eatToken(Kind.TOKEN_SEMI);
		return;
		
	default:
		error(Thread.currentThread().getStackTrace()[2].getMethodName());
		return ;
	}
    // Lab1. Exercise 4: Fill in the missing code
    // to parse a statement.
    //new util.Todo();
  }

  // Statements -> Statement Statements
  // ->
  private void parseStatements()
  {
	if(current.kind == Kind.TOKEN_ASSIGN||current.kind == Kind.TOKEN_LBRACK)
		this.parseStatement();
    while (current.kind == Kind.TOKEN_LBRACE || current.kind == Kind.TOKEN_IF
        || current.kind == Kind.TOKEN_WHILE
        || current.kind == Kind.TOKEN_SYSTEM || current.kind == Kind.TOKEN_ID) {
      parseStatement();
    }
    return;
  }

  // Type -> int []
  // -> boolean
  // -> int
  // -> id
  
  private void parseType()
  {
	switch(this.current.kind){
	case TOKEN_INT:
		this.advance();
		if(this.current.kind==Kind.TOKEN_LBRACK){
			this.advance();
			this.eatToken(Kind.TOKEN_RBRACK);
			return;
		}
		else {
			//this.advance();
			return;
		}
	case TOKEN_BOOLEAN:
		this.advance();
		return;
	case TOKEN_ID:
		this.advance();
		return;
	default:
		error(Thread.currentThread().getStackTrace()[2].getMethodName());
		return;
	}
    // Lab1. Exercise 4: Fill in the missing code
    // to parse a type.
    //new util.Todo();
  }

  // VarDecl -> Type id ;
  private void parseVarDecl()
  {
    // to parse the "Type" nonterminal in this method, instead of writing
    // a fresh one.
	  
//	if(current.kind ==Kind.TOKEN_ID){
//		this.advance();
//		if(current.kind == Kind.TOKEN_ASSIGN)
//			return;
//	}
	if(this.current.kind==Kind.TOKEN_ID){
		eatToken(Kind.TOKEN_ID);
		eatToken(Kind.TOKEN_SEMI);
		return;
	}
	else{
	    parseType();
	    eatToken(Kind.TOKEN_ID);
	    eatToken(Kind.TOKEN_SEMI);
	    return;
	}
  }

  // VarDecls -> VarDecl VarDecls
  // ->
  private void parseVarDecls()
  {//||current.kind == Kind.TOKEN_ID   
    while (current.kind == Kind.TOKEN_INT || current.kind == Kind.TOKEN_BOOLEAN
    		||current.kind == Kind.TOKEN_ID) {
      if(this.current.kind==Kind.TOKEN_ID){
    	this.advance();
    	if(this.current.kind==Kind.TOKEN_ID)
    		this.parseVarDecl();
    	else return;
      }
      else
    	  parseVarDecl();
    }
    return;
  }

  // FormalList -> Type id FormalRest*
  // ->
  // FormalRest -> , Type id
  private void parseFormalList()
  {
    if (current.kind == Kind.TOKEN_INT || current.kind == Kind.TOKEN_BOOLEAN
        || current.kind == Kind.TOKEN_ID) {
      parseType();
      eatToken(Kind.TOKEN_ID);
      while (current.kind == Kind.TOKEN_COMMER) {
        advance();
        parseType();
        eatToken(Kind.TOKEN_ID);
      }
    }
    return;
  }

  // Method -> public Type id ( FormalList )
  // { VarDecl* Statement* return Exp ;}
  private void parseMethod()
  {
	if(this.current.kind==Kind.TOKEN_PUBLIC){
	
		this.advance();
		this.parseType();
		this.eatToken(Kind.TOKEN_ID);
		this.eatToken(Kind.TOKEN_LPAREN);
		this.parseFormalList();
		this.eatToken(Kind.TOKEN_RPAREN);
		this.eatToken(Kind.TOKEN_LBRACE);
		this.parseVarDecls();
		this.parseStatements();
		this.eatToken(Kind.TOKEN_RETURN);
		this.parseExp();
		this.eatToken(Kind.TOKEN_SEMI);
		this.eatToken(Kind.TOKEN_RBRACE);
		return;
	}
	else{
		this.error(Thread.currentThread().getStackTrace()[2].getMethodName());
		return;
	}
		
	
    // Lab1. Exercise 4: Fill in the missing code
    // to parse a method.
    //new util.Todo();
    //return;
  }

  // MethodDecls -> MethodDecl MethodDecls
  // ->
  private void parseMethodDecls()
  {
    while (current.kind == Kind.TOKEN_PUBLIC) {
      parseMethod();
    }
    return;
  }

  // ClassDecl -> class id { VarDecl* MethodDecl* }
  // -> class id extends id { VarDecl* MethodDecl* }
  private void parseClassDecl()
  {
    eatToken(Kind.TOKEN_CLASS);
    eatToken(Kind.TOKEN_ID);
    if (current.kind == Kind.TOKEN_EXTENDS) {
      eatToken(Kind.TOKEN_EXTENDS);
      eatToken(Kind.TOKEN_ID);
    }
    eatToken(Kind.TOKEN_LBRACE);
    parseVarDecls();
    parseMethodDecls();
    eatToken(Kind.TOKEN_RBRACE);
    return;
  }

  // ClassDecls -> ClassDecl ClassDecls
  // ->
  private void parseClassDecls()
  {
    while (current.kind == Kind.TOKEN_CLASS) {
      parseClassDecl();
    }
    return;
  }

  // MainClass -> class id
  // {
  // public static void main ( String [] id )
  // {
  // Statement
  // }
  // }
  private void parseMainClass()
  {	
	this.eatToken(Kind.TOKEN_CLASS);
	this.eatToken(Kind.TOKEN_ID);
	this.eatToken(Kind.TOKEN_LBRACE);
	this.eatToken(Kind.TOKEN_PUBLIC);
	this.eatToken(Kind.TOKEN_STATIC);
	this.eatToken(Kind.TOKEN_VOID);
	this.eatToken(Kind.TOKEN_MAIN);
	this.eatToken(Kind.TOKEN_LPAREN);
	this.eatToken(Kind.TOKEN_STRING);
	this.eatToken(Kind.TOKEN_LBRACK);
	this.eatToken(Kind.TOKEN_RBRACK);
	this.eatToken(Kind.TOKEN_ID);
	this.eatToken(Kind.TOKEN_RPAREN);
	this.eatToken(Kind.TOKEN_LBRACE);
	this.parseStatements();
	this.eatToken(Kind.TOKEN_RBRACE);
	this.eatToken(Kind.TOKEN_RBRACE);
	return;
    // Lab1. Exercise 4: Fill in the missing code
    // to parse a main class as described by the
    // grammar above.
    //new util.Todo();
  }

  // Program -> MainClass ClassDecl*
  private void parseProgram()
  {
    parseMainClass();
    parseClassDecls();
    eatToken(Kind.TOKEN_EOF);
    return; 
  }

  public void parse()
  {
    parseProgram();
    return;
  }
}
