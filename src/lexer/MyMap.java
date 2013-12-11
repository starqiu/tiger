package lexer;

import java.util.*;

import lexer.Token.Kind;

public class MyMap
{
	public Map<String, Kind> map;
	
	public MyMap()
	{
		map = new HashMap<String, Kind>();
		map.put("boolean", Kind.TOKEN_BOOLEAN);
		map.put("class", Kind.TOKEN_CLASS);
		map.put("else", Kind.TOKEN_ELSE);
		map.put("extends", Kind.TOKEN_EXTENDS);
		map.put("false", Kind.TOKEN_FALSE);
		map.put("if", Kind.TOKEN_IF);
		map.put("int", Kind.TOKEN_INT);
		map.put("length", Kind.TOKEN_LENGTH);
		map.put("main", Kind.TOKEN_MAIN);
		map.put("new", Kind.TOKEN_NEW);
		map.put("out", Kind.TOKEN_OUT);
		map.put("println", Kind.TOKEN_PRINTLN);
		map.put("public", Kind.TOKEN_PUBLIC);
		map.put("return", Kind.TOKEN_RETURN);
		map.put("static", Kind.TOKEN_STATIC);
		map.put("String", Kind.TOKEN_STRING);
		map.put("System", Kind.TOKEN_SYSTEM);
		map.put("this", Kind.TOKEN_THIS);
		map.put("true", Kind.TOKEN_TRUE);
		map.put("void", Kind.TOKEN_VOID);
		map.put("while", Kind.TOKEN_WHILE);
	}
	
	public Kind getValue(String key)
	{
		return map.get(key);
	}
}