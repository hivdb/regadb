package expressions;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class) @Suite.SuiteClasses( { ExpressionTest.class,
	BasicExpressionTest.class, IntegerLiteralTest.class, MemoryCellTest.class,
	ComposedExpressionTest.class, UnaryExpressionTest.class,
	NegationTest.class, BinaryExpressionTest.class, AdditionTest.class,
	MultiplicationTest.class, SubtractionTest.class }) public class AllTests {

}
