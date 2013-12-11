package lexer;

import java.io.PushbackReader;

import util.Todo;
import lexer.Token.Kind;

public class Lexer
{
	String fname; // the input file name to be compiled
	PushbackReader fstream;
	Integer lineNum;
	MyMap map;
  
	public Lexer(String fname, PushbackReader fstream)
	{
		this.fname = fname;
		this.fstream = fstream;
		this.lineNum = 1;
		this.map = new MyMap();
	}

	// When called, return the next token (refer to the code "Token.java")
	// from the input stream.
	// Return TOKEN_EOF when reaching the end of the input stream.
	private Token nextTokenInternal() throws Exception
	{
		int curChar = this.fstream.read();
		if(curChar == -1)
			return new Token(Kind.TOKEN_EOF, this.lineNum);

		// skip all kinds of "blanks"
		while((char)curChar == ' ' || (char)curChar == '\t' || (char)curChar == '\n' || (char)curChar == '\r') 
		{
			if((char)curChar == '\n')// the line break of Linux is \n
				this.lineNum++;
			else if((char)curChar == '\r')// the line break of Windows is \r\n
			{
				curChar = this.fstream.read();
				this.lineNum++;
			}
			curChar = this.fstream.read();
		}
		if(curChar == -1)
			return new Token(Kind.TOKEN_EOF, this.lineNum);
   
		switch((char)curChar) 
		{
		case '+':
			return new Token(Kind.TOKEN_ADD, this.lineNum, "+");
		case '&':
			curChar = this.fstream.read();
			if((char)curChar == '&')
				return new Token(Kind.TOKEN_AND, this.lineNum, "&&");
			else
			{
				System.out.println(this.fname + ": at line " + this.lineNum + ": Expect: '&' after '&'");
				new Todo();
				return null;
			}
		case '=':
			return new Token(Kind.TOKEN_ASSIGN, this.lineNum, "=");
		case ',':
			return new Token(Kind.TOKEN_COMMER, this.lineNum, ",");
		case '.':
			return new Token(Kind.TOKEN_DOT, this.lineNum, ".");
		case '{':
			return new Token(Kind.TOKEN_LBRACE, this.lineNum, "{");
		case '[':
			return new Token(Kind.TOKEN_LBRACK, this.lineNum, "[");
		case '(':
			return new Token(Kind.TOKEN_LPAREN, this.lineNum, "(");
		case '<':
			return new Token(Kind.TOKEN_LT, this.lineNum, "<");
		case '!':
			return new Token(Kind.TOKEN_NOT, this.lineNum, "!");
		case '}':
			return new Token(Kind.TOKEN_RBRACE, this.lineNum, "}");
		case ']':
			return new Token(Kind.TOKEN_RBRACK, this.lineNum, "]");
		case ')':
			return new Token(Kind.TOKEN_RPAREN, this.lineNum, ")");
		case ';':
			return new Token(Kind.TOKEN_SEMI, this.lineNum, ";");
		case '/':
		{
			int currentLine = this.lineNum;
			curChar = this.fstream.read();
			if((char)curChar != '/' && (char)curChar != '*')
			{
				System.out.println(this.fname + ": at line " + currentLine + ": Expect: '/' or '*' after '/'");
				new Todo();
				return null;
			}
			else if((char)curChar == '/')
			{
				do
				{
					curChar = this.fstream.read();
				}while(curChar != -1 && (char)curChar != '\n' && (char)curChar != '\r');
				
				this.lineNum++;
				
				if(curChar == -1)
					this.lineNum--;
				else if((char)curChar == '\r')
					curChar = this.fstream.read();// skip '\n'
				
				return new Token(Kind.TOKEN_NOTE, currentLine, "//");
			}
			else if((char)curChar == '*')
			{
				curChar = this.fstream.read();
				while(curChar != -1 && (char)curChar != '*')
				{
					curChar = this.fstream.read();
					if((char)curChar == '\n')
					{
						this.lineNum++;
						curChar = this.fstream.read();
					}
					else if((char)curChar == '\r')
					{
						this.lineNum++;
						curChar = this.fstream.read();
						curChar = this.fstream.read();
					}
				}
				
				if(curChar == -1)
				{
					System.out.println(this.fname + ": at line " + currentLine + ": Expect: \"*/\"");
					new Todo();
					return null;
				}
				else if((char)curChar == '*')
				{
					curChar = this.fstream.read();// skip '/'
					if((char)curChar == '/')
						return new Token(Kind.TOKEN_NOTE, currentLine, "/**/");
					else
					{
						System.out.println(this.fname + ": at line " + currentLine + ": Expect: \"*/\"");
						new Todo();
						return null;
					}
				}
			}
		}
		case '-':
			return new Token(Kind.TOKEN_SUB, this.lineNum, "-");
		case '*':
			return new Token(Kind.TOKEN_TIMES, this.lineNum, "*");
		default:
		{
			if((char)curChar == '_')
			{
				System.out.println(this.fname + ": at line " + this.lineNum + ": Expect: a letter before '_'");
				new Todo();
				return null;
			}
			else if(Character.isLetter((char)curChar))
			{
				StringBuilder buffer = new StringBuilder();
				buffer.append((char)curChar);
				curChar = this.fstream.read();
        	
				while(Character.isLetter((char)curChar) || Character.isDigit((char)curChar) || (char)curChar == '_' && curChar != -1)
				{
					buffer.append((char)curChar);
					curChar = this.fstream.read();
				}					
				
				// back a char
				if(curChar != -1)
					this.fstream.unread(curChar);
        	
				if(map.getValue(buffer.toString()) != null)
				{
					return new Token((Kind)map.getValue(buffer.toString()), this.lineNum, buffer.toString());
				}
				else
				{
					/* get the next char to decide it's a Type or an id */
					curChar = this.fstream.read();
					while((char)curChar == ' ' || (char)curChar == '\t') 
						curChar = this.fstream.read();

					if(Character.isLetter((char)curChar))// if the next is a letter, it's a Type
					{
						if(curChar != -1)
							this.fstream.unread(curChar);
						return new Token(Kind.TOKEN_ID, this.lineNum, buffer.toString(), true);
					}
					else// if not, it's an id
					{
						if(curChar != -1)
							this.fstream.unread(curChar);
						return new Token(Kind.TOKEN_ID, this.lineNum, buffer.toString(), false);
					}
				}
			}
			else if(Character.isDigit((char)curChar))
			{
				StringBuilder buffer = new StringBuilder();
				buffer.append((char)curChar);
				curChar = this.fstream.read();
        	
				if(Character.isLetter((char)curChar))
				{
					new Todo();
					return null;
				}
        	
				while(Character.isDigit((char)curChar))
				{
					buffer.append((char)curChar);
					curChar = this.fstream.read();
				}
        	
				if(curChar != -1)
					this.fstream.unread((char)curChar);
        	
				return new Token(Kind.TOKEN_NUM, this.lineNum, buffer.toString());
			}
			else
			{
				new Todo();
				return null;
			}
		}
		}
	}

	public Token nextToken()
	{
		Token token = null;

		try 
		{
			token = this.nextTokenInternal();
		} 
		catch(Exception ex) 
		{
			ex.printStackTrace();
			System.exit(1);
		}
    
		if(control.Control.lex)
			System.out.println(token.toString());
    
		return token;
	}
}