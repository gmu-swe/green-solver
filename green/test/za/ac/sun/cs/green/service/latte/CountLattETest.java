package za.ac.sun.cs.green.service.latte;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import org.apfloat.Apint;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import za.ac.sun.cs.green.EntireSuite;
import za.ac.sun.cs.green.Green;
import za.ac.sun.cs.green.Instance;
import za.ac.sun.cs.green.expr.*;
import za.ac.sun.cs.green.util.Configuration;

public class CountLattETest {

	public static Green solver = null;

	@BeforeClass
	public static void initialize() {
		if (!EntireSuite.HAS_LATTE) {
			Assume.assumeTrue(false);
			return;
		}		
		solver = new Green();
		Properties props = new Properties();
		props.setProperty("green.services", "count");
		props.setProperty("green.service.count", "latte");
		props.setProperty("green.service.count.latte",
				"za.ac.sun.cs.green.service.latte.CountLattEService");
		props.setProperty("green.latte.path", EntireSuite.LATTE_PATH);
		Configuration config = new Configuration(solver, props);
		config.configure();
	}

	@AfterClass
	public static void report() {
		if (solver != null) {
			solver.report();
		}
	}

	private void check(Expression expression, Expression parentExpression, Apint expected) {
		Instance p = (parentExpression == null) ? null : new Instance(solver, null, parentExpression);
		Instance i = new Instance(solver, p, expression);
		Object result = i.request("count");
		assertNotNull(result);
		assertEquals(Apint.class, result.getClass());
		assertEquals(expected, result);
	}

	private void check(Expression expression, Apint expected) {
		check(expression, null, expected);
	}
	
	/**
	 * Problem:
	 *   1 * aa == 0
	 * Count:
	 *   1
	 */
	@Test
	public void test01() {
		IntConstant a = new IntConstant(1);
		IntVariable v = new IntVariable("aa", 0, 99);
		Operation t = new BinaryOperation(Operation.Operator.MUL, a, v);
		IntConstant c = new IntConstant(0);
		Operation o = new BinaryOperation(Operation.Operator.EQ, t, c);
		check(o, new Apint(1));
	}

	/**
	 * Problem:
	 *   1 * aa > 0
	 *   1 * aa + -10 < 0
	 * Count:
	 *   9
	 */
	@Test
	public void test02() {
		IntConstant zz = new IntConstant(0);
		IntConstant oo = new IntConstant(1);
		IntVariable vv = new IntVariable("aa", 0, 99);
		
		Operation at = new BinaryOperation(Operation.Operator.MUL, oo, vv);
		Operation ao = new BinaryOperation(Operation.Operator.GT, at, zz);
		
		Operation bt1 = new BinaryOperation(Operation.Operator.MUL, oo, vv);
		Operation bt2 = new BinaryOperation(Operation.Operator.ADD, bt1, new IntConstant(-10));
		Operation bo = new BinaryOperation(Operation.Operator.LT, bt2, zz);
		
		Operation o = new BinaryOperation(Operation.Operator.AND, ao, bo);
		check(o, new Apint(9));
	}
	
	/**
	 * Problem:
	 *   3 * aa + -6 > 0
	 *   1 * aa + -10 < 0
	 * Count:
	 *   7
	 */
	@Test
	public void test03() {
		IntConstant zz = new IntConstant(0);
		IntConstant oo = new IntConstant(1);
		IntConstant tt = new IntConstant(3);
		IntVariable vv = new IntVariable("aa", 0, 99);
		
		Operation at1 = new BinaryOperation(Operation.Operator.MUL, tt, vv);
		Operation at2 = new BinaryOperation(Operation.Operator.ADD, at1, new IntConstant(-6));
		Operation ao = new BinaryOperation(Operation.Operator.GT, at2, zz);
		
		Operation bt1 = new BinaryOperation(Operation.Operator.MUL, oo, vv);
		Operation bt2 = new BinaryOperation(Operation.Operator.ADD, bt1, new IntConstant(-10));
		Operation bo = new BinaryOperation(Operation.Operator.LT, bt2, zz);
		
		Operation o = new BinaryOperation(Operation.Operator.AND, ao, bo);
		check(o, new Apint(7));
	}

	/**
	 * Problem:
	 *   1 * aa + -1 * bb < 0
	 *   1 * aa + 1 > 0
	 *   1 * aa + -10 < 0
	 *   1 * bb + 1 > 0
	 *   1 * bb + -10 < 0
	 * Count:
	 *   45
	 */
	@Test
	public void test04() {
		IntConstant zero = new IntConstant(0);
		IntConstant one = new IntConstant(1);
		IntConstant minone = new IntConstant(-1);
		IntConstant minten = new IntConstant(-10);
		IntVariable aa = new IntVariable("aa", 0, 9);
		IntVariable bb = new IntVariable("bb", 0, 9);

		Operation plusaa = new BinaryOperation(Operation.Operator.MUL, one, aa);
		Operation plusbb = new BinaryOperation(Operation.Operator.MUL, one, bb);
		Operation minbb = new BinaryOperation(Operation.Operator.MUL, minone, bb);

		Operation oab1 = new BinaryOperation(Operation.Operator.ADD, plusaa, minbb);
		Operation oab = new BinaryOperation(Operation.Operator.LT, oab1, zero);
		Operation oa1 = new BinaryOperation(Operation.Operator.GT, new BinaryOperation(Operation.Operator.ADD, plusaa, one), zero);
		Operation oa2 = new BinaryOperation(Operation.Operator.LT, new BinaryOperation(Operation.Operator.ADD, plusaa, minten), zero);
		Operation ob1 = new BinaryOperation(Operation.Operator.GT, new BinaryOperation(Operation.Operator.ADD, plusbb, one), zero);
		Operation ob2 = new BinaryOperation(Operation.Operator.LT, new BinaryOperation(Operation.Operator.ADD, plusbb, minten), zero);

		Operation o3 = new BinaryOperation(Operation.Operator.AND, oab, oa1);
		Operation o2 = new BinaryOperation(Operation.Operator.AND, o3, oa2);
		Operation o1 = new BinaryOperation(Operation.Operator.AND, o2, ob1);
		Operation o = new BinaryOperation(Operation.Operator.AND, o1, ob2);

		check(o, new Apint(45));
	}
	
}
