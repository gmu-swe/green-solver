package za.ac.sun.cs.green.service.bounder;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import za.ac.sun.cs.green.Instance;
import za.ac.sun.cs.green.Green;
import za.ac.sun.cs.green.expr.*;
import za.ac.sun.cs.green.util.Configuration;

public class BounderTest {

	public static Green solver;

	@BeforeClass
	public static void initialize() {
		solver = new Green();
		Properties props = new Properties();
		props.setProperty("green.services", "bound");
		props.setProperty("green.service.bound", "(bounder sink)");
		props.setProperty("green.service.bound.bounder",
				"za.ac.sun.cs.green.service.bounder.BounderService");
		props.setProperty("green.service.bound.sink",
				"za.ac.sun.cs.green.service.sink.SinkService");
		Configuration config = new Configuration(solver, props);
		config.configure();
	}

	private void finalCheck(String observed, String[] expected) {
		for (String s : expected) {
			int p = observed.indexOf(s);
			assertTrue(p >= 0);
			if (p == 0) {
				observed = observed.substring(p + s.length());
			} else if (p > 0) {
				observed = observed.substring(0, p - 1)
						+ observed.substring(p + s.length());
			}
		}
		observed = observed.replaceAll("[()&]", "");
		assertEquals("", observed);
	}

	private void check(Expression expression, String full,
			String... expected) {
		Instance i = new Instance(solver, null, expression);
		Expression e = i.getExpression();
		assertTrue(e.equals(expression));
		assertEquals(expression.toString(), e.toString());
		assertEquals(full, i.getFullExpression().toString());
		Object result = i.request("bound");
		assertNotNull(result);
		assertEquals(Instance.class, result.getClass());
		Instance j = (Instance) result;
		finalCheck(j.getFullExpression().toString(), expected);
	}

	@Test
	public void test01() {
		IntVariable v = new IntVariable("v", 0, 99);
		IntConstant c = new IntConstant(0);
		Operation o = new BinaryOperation(Operation.Operator.EQ, v, c);
		check(o, "v==0", "v==0", "v>=0", "v<=99");
	}

	@Test
	public void test02() {
		IntVariable v1 = new IntVariable("v1", 0, 99);
		IntVariable v2 = new IntVariable("v2", 9, 19);
		IntConstant c = new IntConstant(0);
		Operation o1 = new BinaryOperation(Operation.Operator.EQ, v1, c);
		Operation o2 = new BinaryOperation(Operation.Operator.EQ, v2, c);
		Operation o = new BinaryOperation(Operation.Operator.AND, o1, o2);
		check(o, "(v1==0)&&(v2==0)", "v1==0", "v1>=0", "v1<=99", "v2==0", "v2>=9", "v2<=19");
	}
	
}

/*

	@Test
	public void test02a() {
		IntConstant c1 = new IntConstant(2);
		IntConstant c2 = new IntConstant(2);
		Operation o = new BinaryOperation(Operation.Operator.EQ, c1, c2);
		check(o, "2==2", "2==2");
	}
	
	@Test
	public void test02b() {
		IntConstant c1 = new IntConstant(2);
		IntConstant c2 = new IntConstant(2);
		Operation o = new BinaryOperation(Operation.Operator.LT, c1, c2);
		check(o, "2<2", "2<2");
	}
	
	@Test
	public void test03() {
		IntVariable v1 = new IntVariable("v1", 0, 99);
		IntConstant c1 = new IntConstant(0);
		Operation o1 = new BinaryOperation(Operation.Operator.EQ, v1, c1);
		IntVariable v2 = new IntVariable("v2", 0, 99);
		IntConstant c2 = new IntConstant(1);
		Operation o2 = new BinaryOperation(Operation.Operator.NE, v2, c2);
		check(o1, o2, "(v1==0)&&(v2!=1)", "v1==0");
	}

	@Test
	public void test04() {
		IntVariable v1 = new IntVariable("v1", 0, 99);
		IntConstant c1 = new IntConstant(0);
		Operation o1 = new BinaryOperation(Operation.Operator.EQ, v1, c1);
		IntVariable v2 = new IntVariable("v2", 0, 99);
		IntConstant c2 = new IntConstant(1);
		Operation o2 = new BinaryOperation(Operation.Operator.NE, v2, c2);
		check(o1, o2, "(v1==0)&&(v2!=1)", "v1==0");
	}
	
	@Test
	public void test05() {
		IntVariable v1 = new IntVariable("v1", 0, 99);
		IntConstant c1 = new IntConstant(0);
		Operation o1 = new BinaryOperation(Operation.Operator.EQ, v1, c1);
		IntConstant c2 = new IntConstant(1);
		Operation o2 = new BinaryOperation(Operation.Operator.NE, v1, c2);
		check(o1, o2, "(v1==0)&&(v1!=1)", "v1==0", "v1!=1");
	}
	
	@Test
	public void test06() {
		IntVariable v1 = new IntVariable("v1", 0, 99);
		IntVariable v2 = new IntVariable("v2", 0, 99);
		Operation o1 = new BinaryOperation(Operation.Operator.EQ, v1, v2);
		IntVariable v3 = new IntVariable("v3", 0, 99);
		Operation o2 = new BinaryOperation(Operation.Operator.EQ, v2, v3);
		IntVariable v4 = new IntVariable("v4", 0, 99);
		Operation o3 = new BinaryOperation(Operation.Operator.EQ, v3, v4);
		IntVariable v5 = new IntVariable("v5", 0, 99);
		Operation o4 = new BinaryOperation(Operation.Operator.EQ, v4, v5);
		Operation o34 = new BinaryOperation(Operation.Operator.AND, o3, o4);
		Operation o234 = new BinaryOperation(Operation.Operator.AND, o2, o34);
		check(o1, o234, "(v1==v2)&&((v2==v3)&&((v3==v4)&&(v4==v5)))", "v1==v2", "v2==v3", "v3==v4", "v4==v5");
	}
	
	@Test
	public void test07() {
		IntVariable v1 = new IntVariable("v1", 0, 99);
		IntVariable v2 = new IntVariable("v2", 0, 99);
		Operation o1 = new BinaryOperation(Operation.Operator.EQ, v1, v2);
		IntVariable v3 = new IntVariable("v3", 0, 99);
		Operation o2 = new BinaryOperation(Operation.Operator.EQ, v2, v3);
		IntVariable v4 = new IntVariable("v4", 0, 99);
		Operation o3 = new BinaryOperation(Operation.Operator.EQ, v3, v4);
		IntVariable v5 = new IntVariable("v5", 0, 99);
		IntVariable v6 = new IntVariable("v6", 0, 99);
		Operation o4 = new BinaryOperation(Operation.Operator.EQ, v5, v6);
		Operation o34 = new BinaryOperation(Operation.Operator.AND, o3, o4);
		Operation o234 = new BinaryOperation(Operation.Operator.AND, o2, o34);
		check(o1, o234, "(v1==v2)&&((v2==v3)&&((v3==v4)&&(v5==v6)))", "v2==v3", "v3==v4", "v1==v2");
	}
	
	@Test
	public void test08() {
		IntVariable v1 = new IntVariable("v1", 0, 99);
		IntVariable v2 = new IntVariable("v2", 0, 99);
		IntVariable v3 = new IntVariable("v3", 0, 99);
		IntVariable v4 = new IntVariable("v4", 0, 99);
		IntVariable v5 = new IntVariable("v5", 0, 99);
		IntVariable v6 = new IntVariable("v6", 0, 99);
		IntVariable v7 = new IntVariable("v7", 0, 99);
		Operation o1 = new BinaryOperation(Operation.Operator.LT, v1, new BinaryOperation(Operation.Operator.ADD, v2, v3));
		Operation o2 = new BinaryOperation(Operation.Operator.LT, v2, new BinaryOperation(Operation.Operator.ADD, v4, v5));
		Operation o3 = new BinaryOperation(Operation.Operator.LT, v3, new BinaryOperation(Operation.Operator.ADD, v6, v7));
		Operation o23 = new BinaryOperation(Operation.Operator.AND, o2, o3);
		check(o1, o23, "(v1<(v2+v3))&&((v2<(v4+v5))&&(v3<(v6+v7)))", "v1<(v2+v3)", "v3<(v6+v7)", "v2<(v4+v5)");
	}
	
	@Test
	public void test09() {
		IntVariable v1 = new IntVariable("v1", 0, 99);
		IntVariable v2 = new IntVariable("v2", 0, 99);
		IntVariable v3 = new IntVariable("v3", 0, 99);
		IntVariable v4 = new IntVariable("v4", 0, 99);
		IntVariable v5 = new IntVariable("v5", 0, 99);
		IntVariable v6 = new IntVariable("v6", 0, 99);
		IntVariable v7 = new IntVariable("v7", 0, 99);
		IntVariable v8 = new IntVariable("v8", 0, 99);
		Operation o1 = new BinaryOperation(Operation.Operator.LT, v1, new BinaryOperation(Operation.Operator.ADD, v2, v3));
		Operation o2 = new BinaryOperation(Operation.Operator.LT, v2, new BinaryOperation(Operation.Operator.ADD, v4, v5));
		Operation o3 = new BinaryOperation(Operation.Operator.LT, v6, new BinaryOperation(Operation.Operator.ADD, v7, v8));
		Operation o23 = new BinaryOperation(Operation.Operator.AND, o2, o3);
		check(o1, o23, "(v1<(v2+v3))&&((v2<(v4+v5))&&(v6<(v7+v8)))", "v1<(v2+v3)", "v2<(v4+v5)");
	}
	
*/
