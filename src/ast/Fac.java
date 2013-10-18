package ast;

import com.sun.org.apache.bcel.internal.generic.NEW;

import util.Flist;
import ast.exp.*;
import ast.classs.*;
import ast.dec.*;
import ast.method.*;
import ast.program.*;
import ast.mainClass.*;
import ast.stm.*;
import ast.type.*;

public class Fac
{
  // Lab2, exercise 2: read the following code and make
  // sure you understand how the sample program "test/Fac.java" is represented.

  // /////////////////////////////////////////////////////
  // To represent the "Fac.java" program in memory manually  
  // this is for demonstration purpose only, and
  // no one would want to do this in reality (boring and error-prone).
  /*
   * class Factorial { public static void main(String[] a) {
   * System.out.println(new Fac().ComputeFac(10)); } } class Fac { public int
   * ComputeFac(int num) { int num_aux; if (num < 1) num_aux = 1; else num_aux =
   * num * (this.ComputeFac(num-1)); return num_aux; } }
   */

  // // main class: "Factorial"
  static MainClass factorial = new MainClass(
      "Factorial", "a", new Print(new Call(
          new NewObject("Fac"), "ComputeFac",
          new Flist<ast.exp.T>().addAll(new Num(10)))));

  // // class "Fac"
  static ast.classs.Class fac = new ast.classs.Class("Fac", null,
      new Flist<ast.dec.T>().addAll(),
      new Flist<ast.method.T>().addAll(new Method(
          new Int(), "ComputeFac", new Flist<ast.dec.T>()
              .addAll(new Dec(new Int(), "num")),
          new Flist<ast.dec.T>().addAll(new Dec(
              new Int(), "num_aux")), new Flist<ast.stm.T>()
              .addAll(new If(new Lt(new Id("num"),
                  new Num(1)), new Assign("num_aux",
                  new Num(1)), new Assign("num_aux",
                  new Times(new Id("num"), new Call(
                      new This(), "ComputeFac",
                      new Flist<ast.exp.T>().addAll(new Sub(
                          new Id("num"), new Num(1)))))))),
          new Id("num_aux"))));

  // program
  public static Program prog = new Program(factorial,
      new Flist<ast.classs.T>().addAll(fac));

  // Lab2, exercise 2: you should write some code to
  // represent the program "test/Sum.java".
  // Your code here:
  
  //main class: "Sum"
  static MainClass sum = new MainClass("Sum", "a", 
		  new Print(new Call(new NewObject("Doit"), "doit",
				  new Flist<ast.exp.T>().addAll(new Num(101)))));
  //class : "Doit"
  static ast.classs.Class doit = new ast.classs.Class("Doit", null, 
		  new Flist<ast.dec.T>().addAll() ,
		  new Flist<ast.method.T>().addAll(
				  new Method(new Int(), "doit", 
						  new Flist<ast.dec.T>().addAll(new Dec(new Int(), "n")),
						  new Flist<ast.dec.T>().addAll(
								  new Dec(new Int(),"sum"),
								  new Dec(new Int(),"i")),
						  new Flist<ast.stm.T>().addAll(
								  new Assign("i", new Num(0)),
								  new Assign("sum", new Num(0)),
								  new While(new Lt(new Id("i"), new Id("sum")),
										  new Assign("sum", new Add(new Id("sum"), new Id("i"))))),
						  new Id("sum"))));
  
}
