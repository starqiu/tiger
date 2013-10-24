package parser;

import java.io.BufferedInputStream;
import java.util.LinkedList;

import ast.dec.Dec;
import ast.exp.Add;
import ast.exp.And;
import ast.exp.ArraySelect;
import ast.exp.Call;
import ast.exp.False;
import ast.exp.Id;
import ast.exp.Length;
import ast.exp.Lt;
import ast.exp.NewIntArray;
import ast.exp.NewObject;
import ast.exp.Not;
import ast.exp.Num;
import ast.exp.Sub;
import ast.exp.This;
import ast.exp.Times;
import ast.exp.True;
import ast.mainClass.MainClass;
import ast.method.Method;
import ast.program.Program;
import ast.program.T;
import ast.stm.Assign;
import ast.stm.AssignArray;
import ast.stm.Block;
import ast.stm.If;
import ast.stm.Print;
import ast.stm.While;
import ast.type.Boolean;
import ast.type.Class;
import ast.type.Int;
import ast.type.IntArray;

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
  private LinkedList<ast.exp.T> parseExpList()
  {
	LinkedList<ast.exp.T> exps = new LinkedList<ast.exp.T>();
    if (current.kind == Kind.TOKEN_RPAREN)//")"
      return null;
    exps.addLast(parseExp());
    while (current.kind == Kind.TOKEN_COMMER) {//","
      advance();
      exps.addLast(parseExp());
    }
    return exps;
  }

  // AtomExp -> (exp)
  // -> INTEGER_LITERAL
  // -> true
  // -> false
  // -> this
  // -> id
  // -> new int [exp]
  // -> new id ()
  private ast.exp.T parseAtomExp()
  {
	ast.exp.T exp = null;
	String id = current.lexeme;
    switch (current.kind) {
	    case TOKEN_LPAREN:
	      advance();
	      exp = parseExp();
	      eatToken(Kind.TOKEN_RPAREN);
	      return exp;
	    case TOKEN_NUM:
	      advance();
	      return new Num(Integer.parseInt(id));
	    case TOKEN_TRUE:
	      advance();
	      return new True();
	    case TOKEN_FALSE:
	      this.advance();
	      return new False();
	    case TOKEN_THIS:
	      advance();
	      return new This();
	    case TOKEN_ID:
	      advance();
	      return new Id(id);
	    case TOKEN_NEW: {
	      advance();
	      switch (current.kind) {
		      case TOKEN_INT:
		        advance();
		        eatToken(Kind.TOKEN_LBRACK);
		        exp = parseExp();
		        eatToken(Kind.TOKEN_RBRACK);
		        return new NewIntArray(exp);
		      case TOKEN_ID:
		    	id = current.lexeme;
		        advance();
		        eatToken(Kind.TOKEN_LPAREN);
		        eatToken(Kind.TOKEN_RPAREN);
		        return new NewObject(id);
		      default:
		        error(Thread.currentThread().getStackTrace()[2].getMethodName());
		        return null;
		  }
    }
    default:
      error(Thread.currentThread().getStackTrace()[2].getMethodName());
      return null;
    }
  }

  // NotExp -> AtomExp
  // -> AtomExp .id (expList)
  // -> AtomExp [exp]
  // -> AtomExp .length
  private ast.exp.T parseNotExp()
  {
	ast.exp.T exp = null;
    exp = parseAtomExp();
    while (current.kind == Kind.TOKEN_DOT || current.kind == Kind.TOKEN_LBRACK) {//[
      if (current.kind == Kind.TOKEN_DOT) {
        advance();
        if (current.kind == Kind.TOKEN_LENGTH) {
          advance();
          return new Length(exp);
        }
        String id = current.lexeme;
        eatToken(Kind.TOKEN_ID);
        eatToken(Kind.TOKEN_LPAREN);
        LinkedList<ast.exp.T> exps = this.parseExpList();
        eatToken(Kind.TOKEN_RPAREN);
        exp = new Call(exp, id, exps);
      } else {
        advance();
        ast.exp.T exp_index = parseExp();
        eatToken(Kind.TOKEN_RBRACK);
        exp = new ArraySelect(exp, exp_index);
      }
    }
    return exp;
  }

  // TimesExp -> ! TimesExp
  // -> NotExp
  private ast.exp.T parseTimesExp()
  {
	ast.exp.T exp = parseNotExp();
    while (current.kind == Kind.TOKEN_NOT) {
      advance();
      exp = new Not(exp);
    }
    return exp;
  }

  // AddSubExp -> TimesExp * TimesExp
  // -> TimesExp
  private ast.exp.T parseAddSubExp()
  {
	ast.exp.T exp = parseTimesExp();
    while (current.kind == Kind.TOKEN_TIMES) {
      advance();
      ast.exp.T exp_rt = parseTimesExp();
      exp = new Times(exp, exp_rt);
    }
    return exp;
  }

  // LtExp -> AddSubExp + AddSubExp
  // -> AddSubExp - AddSubExp
  // -> AddSubExp
  private ast.exp.T parseLtExp()
  {
	  
	ast.exp.T exp = parseAddSubExp();
	Kind kind = null;
    while (current.kind == Kind.TOKEN_ADD || current.kind == Kind.TOKEN_SUB) {
      kind = current.kind;
      advance();
      ast.exp.T exp_rt = parseAddSubExp();
      if (kind == Kind.TOKEN_ADD) {
		exp = new Add(exp, exp_rt);
      }else {
    	exp = new Sub(exp, exp_rt);
      }
    }
    return exp;
  }

  // AndExp -> LtExp < LtExp
  // -> LtExp
  private ast.exp.T parseAndExp()
  {
	
	ast.exp.T exp = parseLtExp();
    while (current.kind == Kind.TOKEN_LT) {
      advance();
      ast.exp.T exp_rt = parseLtExp();
      exp = new Lt(exp, exp_rt);
    }
    return exp;
  }

  // Exp -> AndExp && AndExp
  // -> AndExp
  private ast.exp.T parseExp()
  {
	ast.exp.T exp = null;
	exp = parseAndExp();
    while (current.kind == Kind.TOKEN_AND) {
      advance();
      ast.exp.T exp_rt = parseAndExp();
      exp = new And(exp, exp_rt);
    }
    return exp;
  }

  // Statement -> { Statement* }
  // -> if ( Exp ) Statement else Statement
  // -> while ( Exp ) Statement
  // -> System.out.println ( Exp ) ;
  // -> id = Exp ;
  // -> id [ Exp ]= Exp ;
  private ast.stm.T parseStatement()
  {
	ast.stm.T stm = null;
	ast.exp.T exp = null;
	switch(current.kind){
		case TOKEN_LBRACE:
			this.advance();
			stm = this.parseStatements();
			this.eatToken(Kind.TOKEN_RBRACE	);
			return stm;
		case TOKEN_IF:
			this.advance();
			this.eatToken(Kind.TOKEN_LPAREN);
			exp = this.parseExp();
			this.eatToken(Kind.TOKEN_RPAREN);
			stm = this.parseStatement();
			this.eatToken(Kind.TOKEN_ELSE);
			ast.stm.T stm_else = this.parseStatement();
			return new If(exp, stm, stm_else);
		case TOKEN_WHILE:
			this.advance();
			this.eatToken(Kind.TOKEN_LPAREN);
			exp = this.parseExp();
			this.eatToken(Kind.TOKEN_RPAREN);
			stm = this.parseStatement();
			return new While(exp, stm);
		case TOKEN_SYSTEM:
			this.advance();
			this.eatToken(Kind.TOKEN_DOT);
			this.eatToken(Kind.TOKEN_OUT);
			this.eatToken(Kind.TOKEN_DOT);
			this.eatToken(Kind.TOKEN_PRINTLN);
			this.eatToken(Kind.TOKEN_LPAREN);
			exp = this.parseExp();
			this.eatToken(Kind.TOKEN_RPAREN);
			this.eatToken(Kind.TOKEN_SEMI);
			return new Print(exp);
		case TOKEN_ID:
			String id = current.lexeme;
			this.advance();
			switch(this.current.kind){
				case TOKEN_ASSIGN:
					this.advance();
					exp = this.parseExp();
					this.eatToken(Kind.TOKEN_SEMI);
					return new Assign(id, exp);
				case TOKEN_LBRACK:
					this.advance();
					exp = this.parseExp();
					this.eatToken(Kind.TOKEN_RBRACK);
					this.eatToken(Kind.TOKEN_ASSIGN);
					ast.exp.T exp_rt = this.parseExp();
					this.eatToken(Kind.TOKEN_SEMI);
					return new AssignArray(id, exp, exp_rt);
				default:
					error(Thread.currentThread().getStackTrace()[2].getMethodName());
					return null;
			}
		default:
			error(Thread.currentThread().getStackTrace()[2].getMethodName());
			return null;
    // Lab1. Exercise 4: Fill in the missing code
    // to parse a statement.
    //new util.Todo();
	}
  }

  // Statements -> Statement Statements
  // ->
  private ast.stm.T parseStatements()
  {
	LinkedList<ast.stm.T> stms = new LinkedList<ast.stm.T>();
    while (current.kind == Kind.TOKEN_LBRACE || current.kind == Kind.TOKEN_IF
        || current.kind == Kind.TOKEN_WHILE
        || current.kind == Kind.TOKEN_SYSTEM || current.kind == Kind.TOKEN_ID) {
    	stms.addLast(this.parseStatement());
    }
    return new Block(stms);
  }

  // Type -> int []
  // -> boolean
  // -> int
  // -> id
  
  private ast.type.T parseType()
  {
	switch(this.current.kind){
		case TOKEN_INT:
			this.advance();
			if(this.current.kind==Kind.TOKEN_LBRACK){
				this.advance();
				this.eatToken(Kind.TOKEN_RBRACK);
				return new IntArray();
			}
			else {
				//this.advance();
				return new Int();
			}
		case TOKEN_BOOLEAN:
			this.advance();
			return new Boolean();
		case TOKEN_ID:
			String id = this.current.lexeme;
			this.advance();
			return new Class(id);
		default:
			error(Thread.currentThread().getStackTrace()[2].getMethodName());
			return null;
	}
    // Lab1. Exercise 4: Fill in the missing code
    // to parse a type.
    //new util.Todo();
  }

  // VarDecl -> Type id ;
  private ast.dec.T parseVarDecl()
  {
    // to parse the "Type" nonterminal in this method, instead of writing
    // a fresh one.
    ast.type.T type = parseType();
    String id = this.current.lexeme;
    eatToken(Kind.TOKEN_ID);
    eatToken(Kind.TOKEN_SEMI);
    return new Dec(type, id);
  }

  // VarDecls -> VarDecl VarDecls
  // ->
  private LinkedList<ast.dec.T> parseVarDecls()
  {//||current.kind == Kind.TOKEN_ID   
	LinkedList<ast.dec.T> decs = new LinkedList<ast.dec.T>();
    while (current.kind == Kind.TOKEN_INT || current.kind == Kind.TOKEN_BOOLEAN
    		||(current.kind == Kind.TOKEN_ID && current.isField == true)) {
    		decs.addLast(this.parseVarDecl());
    }
    return decs;
  }

  // FormalList -> Type id FormalRest*
  // ->
  // FormalRest -> , Type id
  private LinkedList<ast.dec.T> parseFormalList()
  {
	LinkedList<ast.dec.T> formals = new LinkedList<ast.dec.T>();
	ast.type.T type = null;
	String id = null;
    if (current.kind == Kind.TOKEN_INT || current.kind == Kind.TOKEN_BOOLEAN
        || current.kind == Kind.TOKEN_ID) {
    	type = parseType();
    	id = this.current.lexeme;
    	eatToken(Kind.TOKEN_ID);
    	formals.addLast(new Dec(type, id));
      while (current.kind == Kind.TOKEN_COMMER) {
        advance();
        type = parseType();
    	id = this.current.lexeme;
    	eatToken(Kind.TOKEN_ID);
    	formals.addLast(new Dec(type, id));
      }
    }
    return formals;
  }

  // Method -> public Type id ( FormalList )
  // { VarDecl* Statement* return Exp ;}
  private ast.method.T parseMethod()
  {
	if(this.current.kind==Kind.TOKEN_PUBLIC){
	
		this.advance();
		ast.type.T retType = this.parseType();
		String id = this.current.lexeme;
		this.eatToken(Kind.TOKEN_ID);
		this.eatToken(Kind.TOKEN_LPAREN);
		LinkedList<ast.dec.T> formals = this.parseFormalList();
		this.eatToken(Kind.TOKEN_RPAREN);
		this.eatToken(Kind.TOKEN_LBRACE);
		LinkedList<ast.dec.T> locals = this.parseVarDecls();
		LinkedList<ast.stm.T> stms = ((Block)this.parseStatements()).stms;
		this.eatToken(Kind.TOKEN_RETURN);
		ast.exp.T retExp = this.parseExp();
		this.eatToken(Kind.TOKEN_SEMI);
		this.eatToken(Kind.TOKEN_RBRACE);
		return new Method(retType, id, formals, locals, stms, retExp);
	}
	else{
		this.error(Thread.currentThread().getStackTrace()[2].getMethodName());
		return null;
	}
		
	
    // Lab1. Exercise 4: Fill in the missing code
    // to parse a method.
    //new util.Todo();
    //return;
  }

  // MethodDecls -> MethodDecl MethodDecls
  // ->
  private LinkedList<ast.method.T> parseMethodDecls()
  {
	LinkedList<ast.method.T> methods = new LinkedList<ast.method.T>();
    while (current.kind == Kind.TOKEN_PUBLIC) {
      methods.addLast(parseMethod());
    }
    return methods;
  }

  // ClassDecl -> class id { VarDecl* MethodDecl* }
  // -> class id extends id { VarDecl* MethodDecl* }
  private ast.classs.T parseClassDecl()
  {
	String id = null;
	String extendss = null;
    eatToken(Kind.TOKEN_CLASS);
    id = this.current.lexeme;
    eatToken(Kind.TOKEN_ID);
    if (current.kind == Kind.TOKEN_EXTENDS) {
      eatToken(Kind.TOKEN_EXTENDS);
      extendss = this.current.lexeme;
      eatToken(Kind.TOKEN_ID);
    }
    eatToken(Kind.TOKEN_LBRACE);
    LinkedList<ast.dec.T> decs = parseVarDecls();
    LinkedList<ast.method.T> methods = parseMethodDecls();
    eatToken(Kind.TOKEN_RBRACE);
    return new ast.classs.Class(id, extendss, decs, methods);
  }

  // ClassDecls -> ClassDecl ClassDecls
  // ->
  private LinkedList<ast.classs.T> parseClassDecls()
  {
	LinkedList<ast.classs.T>  classs = new LinkedList<ast.classs.T>();
    while (current.kind == Kind.TOKEN_CLASS) {
      classs.addLast(parseClassDecl());
    }
    return classs;
  }

  // MainClass -> class id
  // {
  // public static void main ( String [] id )
  // {
  // Statement
  // }
  // }
  private ast.mainClass.T parseMainClass()
  {	
	this.eatToken(Kind.TOKEN_CLASS);
	String id = this.current.lexeme;
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
	String arg = this.current.lexeme;
	this.eatToken(Kind.TOKEN_ID);
	this.eatToken(Kind.TOKEN_RPAREN);
	this.eatToken(Kind.TOKEN_LBRACE);
	ast.stm.T stm = this.parseStatement();
	this.eatToken(Kind.TOKEN_RBRACE);
	this.eatToken(Kind.TOKEN_RBRACE);
	return new MainClass(id, arg, stm);
    // Lab1. Exercise 4: Fill in the missing code
    // to parse a main class as described by the
    // grammar above.
    //new util.Todo();
  }

  // Program -> MainClass ClassDecl*
  private ast.program.T parseProgram()
  {
	ast.mainClass.T mainClass = parseMainClass();
	LinkedList<ast.classs.T> classes = parseClassDecls();
    eatToken(Kind.TOKEN_EOF);
    return new Program(mainClass, classes); 
  }

  public T parse()
  {
    return parseProgram();
  }
}
