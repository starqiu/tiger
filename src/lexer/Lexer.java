package lexer;

import java.io.InputStream;
import java.util.HashMap;

import util.Error;

import com.sun.org.apache.xpath.internal.compiler.Keywords;

import lexer.Token.Kind;

public class Lexer
{
  String fname; // the input file name to be compiled
  InputStream fstream; // input stream for the above file
  int lineNum = 1;  //line number start with 0
  String lexeme = ""; 
  public HashMap<String, Token> keyWords = new HashMap<String, Token>();

  public Lexer(String fname, InputStream fstream)
  {
    this.fname = fname;
    this.fstream = fstream;
    
    keyWords.put("boolean", new Token(Kind.TOKEN_BOOLEAN,lineNum));
    keyWords.put("class", new Token(Kind.TOKEN_CLASS,lineNum));
    keyWords.put("else", new Token(Kind.TOKEN_ELSE,lineNum));
    keyWords.put("extends", new Token(Kind.TOKEN_EXTENDS,lineNum));
    keyWords.put("false", new Token(Kind.TOKEN_FALSE,lineNum));
    keyWords.put("if", new Token(Kind.TOKEN_IF,lineNum));
    keyWords.put("int", new Token(Kind.TOKEN_INT,lineNum));
    keyWords.put("length", new Token(Kind.TOKEN_LENGTH,lineNum));
    keyWords.put("main", new Token(Kind.TOKEN_MAIN,lineNum));
    keyWords.put("new", new Token(Kind.TOKEN_NEW,lineNum));
    
    keyWords.put("out", new Token(Kind.TOKEN_OUT,lineNum));
    keyWords.put("println", new Token(Kind.TOKEN_PRINTLN,lineNum));
    keyWords.put("public", new Token(Kind.TOKEN_PUBLIC,lineNum));
    keyWords.put("return", new Token(Kind.TOKEN_RETURN,lineNum));
    keyWords.put("static", new Token(Kind.TOKEN_STATIC,lineNum));
    keyWords.put("String", new Token(Kind.TOKEN_STRING,lineNum));
    keyWords.put("System", new Token(Kind.TOKEN_SYSTEM,lineNum));
    keyWords.put("this", new Token(Kind.TOKEN_THIS,lineNum));
    keyWords.put("true", new Token(Kind.TOKEN_TRUE,lineNum));
    keyWords.put("void", new Token(Kind.TOKEN_VOID,lineNum));
    keyWords.put("while", new Token(Kind.TOKEN_WHILE,lineNum));
    
    keyWords.put("float", new Token(Kind.TOKEN_FLOAT,lineNum));
  }

  // When called, return the next token (refer to the code "Token.java")
  // from the input stream.
  // Return TOKEN_EOF when reaching the end of the input stream.
  private Token nextTokenInternal() throws Exception
  {
    int c = this.fstream.read();
    if (-1 == c)
      // The value for "lineNum" is now "null",
      // you should modify this to an appropriate
      // line number for the "EOF" token.
      return new Token(Kind.TOKEN_EOF, lineNum);

    // skip all kinds of "blanks"
    while (' ' == c || '\t' == c || '\n' == c|| '\r' == c) {
		if ('\n' == c) {
			lineNum++;
		}
		c = this.fstream.read();
    }
    if (-1 == c)
      return new Token(Kind.TOKEN_EOF, lineNum);

    switch (c) {
	    case '!':
	    	if('=' == (c=this.fstream.read())){
	    		return new Token(Kind.TOKEN_NQ, lineNum); 
	    	}else {
	    		return new Token(Kind.TOKEN_NOT, lineNum);
			}
	    case '=':
	    	if('=' == (c=this.fstream.read())){
	    		return new Token(Kind.TOKEN_EQUAL, lineNum); 
	    	}else {
	    		return new Token(Kind.TOKEN_ASSIGN, lineNum);
			}
	    case '<':
	    	if('=' == (c=this.fstream.read())){
	    		return new Token(Kind.TOKEN_LQ, lineNum); 
	    	}else {
	    		return new Token(Kind.TOKEN_LT, lineNum);
			}
	    case '>':
	    	if('=' == (c=this.fstream.read())){
	    		return new Token(Kind.TOKEN_GQ, lineNum); 
	    	}else {
	    		return new Token(Kind.TOKEN_GT, lineNum);
			}
	    case '&':
	    	if('&' == (c=this.fstream.read())){
	    		return new Token(Kind.TOKEN_AND, lineNum); 
	    	}else {
	    		Error.bug();
				return null;
			}
	    case '|':
	    	if('|' == (c=this.fstream.read())){
	    		return new Token(Kind.TOKEN_OR, lineNum); 
	    	}else {
	    		Error.bug();
				return null;
			}
	    case '+':
	    	return new Token(Kind.TOKEN_ADD, lineNum);
	    case '-':
	    	return new Token(Kind.TOKEN_SUB, lineNum);
	    case '*':
	    	return new Token(Kind.TOKEN_TIMES, lineNum);
	    case '/':
	    	if('/' == (c=this.fstream.read())){//comment, ingnore it
	    		while('\n' != c){
	    			c=this.fstream.read();
	    		}
	    		return new Token(Kind.TOKEN_COMMENT, lineNum++);
	    	}else {
	    		return new Token(Kind.TOKEN_DIV, lineNum);
	    	}
	    case ';':
	    	return new Token(Kind.TOKEN_SEMI, lineNum);
	    case ',':
	    	return new Token(Kind.TOKEN_COMMER, lineNum);
	    case '.':
	    	return new Token(Kind.TOKEN_DOT, lineNum);
	    case '{':
	    	return new Token(Kind.TOKEN_LBRACE, lineNum);
	    case '}':
	    	return new Token(Kind.TOKEN_RBRACE, lineNum);
	    case '[':
	    	return new Token(Kind.TOKEN_LBRACK, lineNum);
	    case ']':
	    	return new Token(Kind.TOKEN_RBRACK, lineNum);
	    case '(':
	    	return new Token(Kind.TOKEN_LPAREN, lineNum);
	    case ')':
	    	return new Token(Kind.TOKEN_RPAREN, lineNum);
	    default:
	      // Lab 1, exercise 2: supply missing code to
	      // lex other kinds of tokens.
	      // Hint: think carefully about the basic
	      // data structure and algorithms. The code
	      // is not that much and may be less than 50 lines. If you
	      // find you are writing a lot of code, you
	      // are on the wrong way.
	      //new Todo();
	    	//is Num or Real
	    	if (Character.isDigit(c)) {
				int v = 0;
				do {
					v =10*v + Character.digit(c, 10);
					c=this.fstream.read();
				} while (Character.isDigit(c));
				if ('.' != c) {
					return new Token(Kind.TOKEN_NUM,lineNum,String.valueOf(v));
				}else {
					float f = v;
					float d = 10;
					while (Character.isDigit((c=this.fstream.read()))){
						f += Character.digit(c, 10);
						d *= 10;
					}
					return new Token(Kind.TOKEN_REAL,lineNum,String.valueOf(f));
				}
			}
	    	
	    	//is letter
	    	if (Character.isLetter(c)) {
				StringBuffer buf = new StringBuffer();
				do {
					buf.append((char)c);
					c=this.fstream.read();
				} while (Character.isLetter(c)||('_'==c)||Character.isDigit(c));//letter or '_' or digit 
				String str = buf.toString();
				//is keyword?
				Token keyword = keyWords.get(str);
				if (null == keyword) {
					return new Token(Kind.TOKEN_ID, lineNum, str);
				}else {
					keyword.lineNum =lineNum;
					return keyword;
				}
			}
	    	Error.bug();
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
    if (control.Control.lex)
      System.out.println(t.toString());
    return t;
  }
}
