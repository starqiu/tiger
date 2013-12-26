package parser;

import java.io.PushbackReader;
import java.util.LinkedList;

import lexer.Lexer;
import lexer.Token;
import lexer.Token.Kind;
import ast.exp.T;

public class Parser {
	String fname;
	Lexer lexer;
	Token current;
	boolean isField;

	public Parser(String fname, PushbackReader fstream) {
		this.fname = fname;
		this.lexer = new Lexer(fname, fstream);
		this.current = this.lexer.nextToken();
		while (this.current.kind == Kind.TOKEN_NOTE)
			this.current = this.lexer.nextToken();
		this.isField = false;
	}

	// /////////////////////////////////////////////
	// utility methods to connect the lexer
	// and the parser
	private void advance() {
		this.current = this.lexer.nextToken();
		while (this.current.kind == Kind.TOKEN_NOTE)
			this.current = this.lexer.nextToken();
	}

	private void eatToken(Kind kind) {
		if (kind == this.current.kind)
			this.advance();
		else {
			System.out.println(this.fname + ": at line " + this.current.lineNum
					+ ": Expect: " + kind.toString());
			System.out.println("But got: " + this.current.kind.toString());
			System.exit(1);
		}
	}

	private void error() {
		System.out.println(this.fname + ": at line " + this.current.lineNum
				+ ": Syntax error: There are some syntax errors near "
				+ this.current.kind.toString());
		System.exit(1);
	}

	// ////////////////////////////////////////////////////////////
	// below are method for parsing

	// A bunch of parsing methods to parse expressions. The messy
	// parts are to deal with precedence and associativity.

	// ExpList -> Exp ExpRest*
	// ->
	// ExpRest -> , Exp
	private LinkedList<ast.exp.T> parseExpList() {
		LinkedList<ast.exp.T> expList = new LinkedList<ast.exp.T>();
		if (this.current.kind == Kind.TOKEN_RPAREN)
			return expList;

		expList.add(parseExp());
		while (this.current.kind == Kind.TOKEN_COMMER) {
			this.advance();
			expList.add(this.parseExp());
		}
		return expList;
	}

	// AtomExp -> (exp)
	// -> INTEGER_LITERAL
	// -> true
	// -> false
	// -> this
	// -> id
	// -> new int[exp]
	// -> new id()
	private ast.exp.T parseAtomExp() {
		int lineNum = this.current.lineNum;
		switch (this.current.kind) {
		case TOKEN_LPAREN: {
			this.advance();
			ast.exp.T exp = this.parseExp();
			this.eatToken(Kind.TOKEN_RPAREN);
			return new ast.exp.Paren(exp);
		}
		case TOKEN_NUM: {
			String num = this.current.lexeme;
			this.advance();
			return new ast.exp.Num(Integer.parseInt(num), lineNum);
		}
		case TOKEN_TRUE: {
			this.advance();
			return new ast.exp.True(lineNum);
		}
		case TOKEN_FALSE: {
			this.advance();
			return new ast.exp.False(lineNum);
		}
		case TOKEN_THIS: {
			this.advance();
			return new ast.exp.This(lineNum);
		}
		case TOKEN_ID: {
			String id = this.current.lexeme;
			this.advance();
			return new ast.exp.Id(id, lineNum);
		}
		case TOKEN_NEW: {
			this.advance();
			switch (this.current.kind) {
			case TOKEN_INT:
				this.advance();
				this.eatToken(Kind.TOKEN_LBRACK);
				ast.exp.T exp = this.parseExp();
				this.eatToken(Kind.TOKEN_RBRACK);
				return new ast.exp.NewIntArray(exp);
			case TOKEN_ID: {
				String id = this.current.lexeme;
				this.advance();
				this.eatToken(Kind.TOKEN_LPAREN);
				this.eatToken(Kind.TOKEN_RPAREN);
				return new ast.exp.NewObject(id);
			}
			default:
				this.error();
				return null;
			}
		}
		default:
			this.error();
			return null;
		}
	}

	// NotExp -> AtomExp
	// -> AtomExp.id(expList)
	// -> AtomExp[exp]
	// -> AtomExp.length
	private ast.exp.T parseNotExp() {
		ast.exp.T atomExp = this.parseAtomExp();
		int lineNum = this.current.lineNum;
		while (this.current.kind == Kind.TOKEN_DOT
				|| this.current.kind == Kind.TOKEN_LBRACK) {
			if (this.current.kind == Kind.TOKEN_DOT) {
				this.advance();
				if (this.current.kind == Kind.TOKEN_LENGTH) {
					ast.exp.T length = new ast.exp.Length(atomExp, lineNum);
					atomExp = length;
					this.advance();
				} else if (this.current.kind == Kind.TOKEN_ID) {
					String id = this.current.lexeme;
					this.eatToken(Kind.TOKEN_ID);
					this.eatToken(Kind.TOKEN_LPAREN);
					LinkedList<T> args = this.parseExpList();
					this.eatToken(Kind.TOKEN_RPAREN);
					ast.exp.Call call = new ast.exp.Call(atomExp, id, args,
							lineNum);
					atomExp = call;
				}
			} else {
				this.advance();
				ast.exp.T index = this.parseExp();
				this.eatToken(Kind.TOKEN_RBRACK);
				ast.exp.ArraySelect arraySelect = new ast.exp.ArraySelect(
						atomExp, index, lineNum);
				atomExp = arraySelect;
			}
		}
		return atomExp;
	}

	// TimesExp -> !TimesExp
	// -> NotExp
	private ast.exp.T parseTimesExp() {
		Token temp = null;
		int lineNum = this.current.lineNum;
		while (this.current.kind == Kind.TOKEN_NOT) {
			temp = this.current;
			this.advance();
		}
		ast.exp.T notExp = this.parseNotExp();
		if (temp != null && temp.kind == Kind.TOKEN_NOT)
			notExp = new ast.exp.Not(notExp, lineNum);
		return notExp;
	}

	// AddSubExp -> TimesExp * TimesExp
	// -> TimesExp
	private ast.exp.T parseAddSubExp() {
		ast.exp.T left = this.parseTimesExp();
		while (this.current.kind == Kind.TOKEN_TIMES) {
			int lineNum = this.current.lineNum;
			this.advance();
			ast.exp.T right = this.parseTimesExp();
			left = new ast.exp.Times(left, right, lineNum);
		}
		return left;
	}

	// LtExp -> AddSubExp + AddSubExp
	// -> AddSubExp - AddSubExp
	// -> AddSubExp
	private ast.exp.T parseLtExp() {
		ast.exp.T left = this.parseAddSubExp();
		while (this.current.kind == Kind.TOKEN_ADD
				|| this.current.kind == Kind.TOKEN_SUB) {
			int lineNum = this.current.lineNum;
			if (this.current.kind == Kind.TOKEN_ADD) {
				this.advance();
				ast.exp.T right = this.parseAddSubExp();
				left = new ast.exp.Add(left, right, lineNum);
			} else {
				this.advance();
				ast.exp.T right = this.parseAddSubExp();
				left = new ast.exp.Sub(left, right, lineNum);
			}
		}
		return left;
	}

	// AndExp -> LtExp < LtExp
	// -> LtExp
	private ast.exp.T parseAndExp() {
		ast.exp.T left = this.parseLtExp();
		while (current.kind == Kind.TOKEN_LT) {
			int lineNum = this.current.lineNum;
			this.advance();
			ast.exp.T right = this.parseLtExp();
			left = new ast.exp.Lt(left, right, lineNum);
		}
		return left;
	}

	// Exp -> AndExp && AndExp
	// -> AndExp
	private ast.exp.T parseExp() {
		ast.exp.T left = this.parseAndExp();
		while (this.current.kind == Kind.TOKEN_AND) {
			int lineNum = this.current.lineNum;
			this.advance();
			ast.exp.T right = this.parseAndExp();
			left = new ast.exp.And(left, right, lineNum);
		}
		return left;
	}

	// Statements -> Statement Statements
	// ->
	private LinkedList<ast.stm.T> parseStatements() {
		LinkedList<ast.stm.T> stms = new LinkedList<ast.stm.T>();
		while (this.current.kind == Kind.TOKEN_LBRACE
				|| this.current.kind == Kind.TOKEN_IF
				|| this.current.kind == Kind.TOKEN_WHILE
				|| this.current.kind == Kind.TOKEN_TRY
				|| this.current.kind == Kind.TOKEN_THROW
				|| this.current.kind == Kind.TOKEN_SYSTEM
				|| this.current.kind == Kind.TOKEN_ID) {
			stms.add(this.parseStatement());
		}
		return stms;
	}

	// Statement -> { Statement* }
	// -> if(Exp) Statement else Statement
	// -> while(Exp) Statement
	// -> System.out.println(Exp) ;
	// -> id = Exp ;
	// -> id[Exp] = Exp ;
	// -> try Statement catch Statement
	// -> try Statement catch(INTEGER_LITERAL) Statement
	// -> throw;
	// -> throw(INTEGER_LITERAL);
	private ast.stm.T parseStatement() {
		// Lab1. Exercise 4: Fill in the missing code
		// to parse a statement.
		switch (this.current.kind) {
		case TOKEN_LBRACE: {
			this.advance();
			LinkedList<ast.stm.T> stms = this.parseStatements();
			this.eatToken(Kind.TOKEN_RBRACE);
			return new ast.stm.Block(stms);
		}
		case TOKEN_IF: {
			this.advance();
			this.eatToken(Kind.TOKEN_LPAREN);
			ast.exp.T condition = this.parseExp();
			this.eatToken(Kind.TOKEN_RPAREN);
			ast.stm.T thenn = this.parseStatement();
			this.eatToken(Kind.TOKEN_ELSE);
			ast.stm.T elsee = this.parseStatement();
			return new ast.stm.If(condition, thenn, elsee);
		}
		case TOKEN_WHILE: {
			this.advance();
			this.eatToken(Kind.TOKEN_LPAREN);
			ast.exp.T condition = this.parseExp();
			this.eatToken(Kind.TOKEN_RPAREN);
			ast.stm.T body = this.parseStatement();
			return new ast.stm.While(condition, body);
		}
		case TOKEN_SYSTEM: {
			this.advance();
			this.eatToken(Kind.TOKEN_DOT);
			this.eatToken(Kind.TOKEN_OUT);
			this.eatToken(Kind.TOKEN_DOT);
			this.eatToken(Kind.TOKEN_PRINTLN);
			this.eatToken(Kind.TOKEN_LPAREN);
			ast.exp.T exp = this.parseExp();
			this.eatToken(Kind.TOKEN_RPAREN);
			this.eatToken(Kind.TOKEN_SEMI);
			return new ast.stm.Print(exp);
		}
		case TOKEN_ID: {
			String id = current.lexeme;
			this.advance();
			switch (this.current.kind) {
			case TOKEN_ASSIGN: {
				this.advance();
				ast.exp.T exp = this.parseExp();
				this.eatToken(Kind.TOKEN_SEMI);
				return new ast.stm.Assign(new ast.exp.Id(id), exp);
			}
			case TOKEN_LBRACK: {
				this.advance();
				ast.exp.T index = this.parseExp();
				this.eatToken(Kind.TOKEN_RBRACK);
				this.eatToken(Kind.TOKEN_ASSIGN);
				ast.exp.T exp = this.parseExp();
				this.eatToken(Kind.TOKEN_SEMI);
				return new ast.stm.AssignArray(new ast.exp.Id(id), index, exp);
			}
			default:
				this.error();
				return null;
			}
		}
		case TOKEN_TRY:{
			this.advance();
			ast.stm.T tryy = this.parseStatement();
			this.eatToken(Kind.TOKEN_CATCH);
			String match =null;
			if (this.current.kind == Kind.TOKEN_LPAREN) {
				this.eatToken(Kind.TOKEN_LPAREN);
				match = this.current.lexeme;
				this.eatToken(Kind.TOKEN_NUM);
				this.eatToken(Kind.TOKEN_RPAREN);
			}
			ast.stm.T catchh = this.parseStatement();
			if (null == match) {
				return new ast.stm.TryCatch(tryy,catchh);
			}else {
				return new ast.stm.TryCatch(Integer.valueOf(match).intValue(),tryy,catchh);
			}
		}
		case TOKEN_THROW:{
			this.advance();
			if (current.kind == Kind.TOKEN_SEMI) {
				this.eatToken(Kind.TOKEN_SEMI);
				return new ast.stm.Throw();
			}else {
				this.eatToken(Kind.TOKEN_LPAREN);
				if (this.current.kind == Kind.TOKEN_NUM) {
					String match = this.current.lexeme;
					this.advance();
					this.eatToken(Kind.TOKEN_RPAREN);
					this.eatToken(Kind.TOKEN_SEMI);
					return new ast.stm.Throw(Integer.valueOf(match).intValue());
				}else {
					this.error();
					return null;
				}
			}
		}
		default:
			this.error();
			return null;
		}
	}

	// Type -> int[]
	// -> boolean
	// -> int
	// -> id
	private ast.type.T parseType() {
		// Lab1. Exercise 4: Fill in the missing code
		// to parse a type.
		switch (this.current.kind) {
		case TOKEN_INT: {
			this.advance();
			switch (this.current.kind) {
			case TOKEN_LBRACK:
				this.advance();
				this.eatToken(Kind.TOKEN_RBRACK);
				return new ast.type.IntArray();
			case TOKEN_ID:
				return new ast.type.Int();
			default:
				this.error();
				return null;
			}
		}
		case TOKEN_BOOLEAN:
			this.advance();
			return new ast.type.Boolean();
		case TOKEN_ID: {
			String id = this.current.lexeme;
			this.advance();
			return new ast.type.Class(id);
		}
		default:
			this.error();
			return null;
		}
	}

	// VarDecl -> Type id ;
	private ast.dec.T parseVarDecl() {
		// to parse the "Type" nonterminal in this method, instead of writing
		// a fresh one.
		ast.type.T type = this.parseType();
		String id = this.current.lexeme;
		int lineNum = this.current.lineNum;
		this.eatToken(Kind.TOKEN_ID);
		this.eatToken(Kind.TOKEN_SEMI);
		return new ast.dec.Dec(type, id, lineNum, this.isField);
	}

	// VarDecls -> VarDecl VarDecls
	// ->
	private LinkedList<ast.dec.T> parseVarDecls() {
		LinkedList<ast.dec.T> formals = new LinkedList<ast.dec.T>();
		while (this.current.kind == Kind.TOKEN_INT
				|| this.current.kind == Kind.TOKEN_BOOLEAN
				|| (this.current.kind == Kind.TOKEN_ID && this.current.isType == true)) {
			formals.add(this.parseVarDecl());
		}
		return formals;
	}

	// FormalList -> Type id FormalRest*
	// ->
	// FormalRest -> , Type id
	private LinkedList<ast.dec.T> parseFormalList() {
		LinkedList<ast.dec.T> formals = new LinkedList<ast.dec.T>();
		ast.dec.T dec = null;
		ast.type.T type = null;
		String id = null;
		if (this.current.kind == Kind.TOKEN_INT
				|| this.current.kind == Kind.TOKEN_BOOLEAN
				|| this.current.kind == Kind.TOKEN_ID) {
			type = this.parseType();
			id = this.current.lexeme;
			this.eatToken(Kind.TOKEN_ID);
			dec = new ast.dec.Dec(type, id);
			formals.add(dec);
			while (this.current.kind == Kind.TOKEN_COMMER) {
				this.advance();
				type = this.parseType();
				id = this.current.lexeme;
				this.eatToken(Kind.TOKEN_ID);
				dec = new ast.dec.Dec(type, id);
				formals.add(dec);
			}
		}
		return formals;
	}

	// MethodDecls -> MethodDecl MethodDecls
	// ->
	private LinkedList<ast.method.T> parseMethodDecls() {
		LinkedList<ast.method.T> methods = new LinkedList<ast.method.T>();
		while (this.current.kind == Kind.TOKEN_PUBLIC) {
			methods.add(this.parseMethod());
		}
		return methods;
	}

	// Method -> public Type id ( FormalList )
	// { VarDecl* Statement* return Exp ;}
	private ast.method.T parseMethod() {
		// Lab1. Exercise 4: Fill in the missing code
		// to parse a method.
		this.eatToken(Kind.TOKEN_PUBLIC);
		ast.type.T retType = this.parseType();
		String id = current.lexeme;
		this.eatToken(Kind.TOKEN_ID);
		this.eatToken(Kind.TOKEN_LPAREN);
		LinkedList<ast.dec.T> formals = this.parseFormalList();
		this.eatToken(Kind.TOKEN_RPAREN);
		this.eatToken(Kind.TOKEN_LBRACE);
		this.isField = false;
		LinkedList<ast.dec.T> locals = this.parseVarDecls();
		LinkedList<ast.stm.T> stms = this.parseStatements();
		this.eatToken(Kind.TOKEN_RETURN);
		ast.exp.T retExp = this.parseExp();
		this.eatToken(Kind.TOKEN_SEMI);
		this.eatToken(Kind.TOKEN_RBRACE);
		return new ast.method.Method(retType, id, formals, locals, stms, retExp);
	}

	// ClassDecls -> ClassDecl ClassDecls
	// ->
	private LinkedList<ast.classs.T> parseClassDecls() {
		LinkedList<ast.classs.T> decs = new LinkedList<ast.classs.T>();
		while (this.current.kind == Kind.TOKEN_CLASS) {
			decs.add(this.parseClassDecl());
		}
		return decs;
	}

	// ClassDecl -> class id { VarDecl* MethodDecl* }
	// -> class id extends id { VarDecl* MethodDecl* }
	private ast.classs.T parseClassDecl() {
		this.eatToken(Kind.TOKEN_CLASS);
		String id = this.current.lexeme;
		this.eatToken(Kind.TOKEN_ID);
		String extendss = null;
		if (this.current.kind == Kind.TOKEN_EXTENDS) {
			this.eatToken(Kind.TOKEN_EXTENDS);
			extendss = this.current.lexeme;
			this.eatToken(Kind.TOKEN_ID);
		}
		this.eatToken(Kind.TOKEN_LBRACE);
		this.isField = true;
		LinkedList<ast.dec.T> decs = this.parseVarDecls();
		LinkedList<ast.method.T> methods = this.parseMethodDecls();
		this.eatToken(Kind.TOKEN_RBRACE);
		return new ast.classs.Class(id, extendss, decs, methods);
	}

	// MainClass -> class id
	// {
	// public static void main (String [] id)
	// {
	// Statement
	// }
	// }
	private ast.mainClass.T parseMainClass() {
		// Lab1. Exercise 4: Fill in the missing code
		// to parse a main class as described by the
		// grammar above.
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
		String args = this.current.lexeme;
		this.eatToken(Kind.TOKEN_ID);
		this.eatToken(Kind.TOKEN_RPAREN);
		this.eatToken(Kind.TOKEN_LBRACE);
		ast.stm.T stm = this.parseStatement();
		this.eatToken(Kind.TOKEN_RBRACE);
		this.eatToken(Kind.TOKEN_RBRACE);
		return new ast.mainClass.MainClass(id, args, stm);
	}

	// Program -> MainClass ClassDecl*
	private ast.program.T parseProgram() {
		ast.mainClass.T mainClass = this.parseMainClass();
		LinkedList<ast.classs.T> classes = this.parseClassDecls();
		this.eatToken(Kind.TOKEN_EOF);
		return new ast.program.Program(mainClass, classes);
	}

	public ast.program.T parse() {
		return this.parseProgram();
	}
}
