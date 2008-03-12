package com.pharmadm.num.dhb.DhbOptimizing;

/**
 * Factory of point/vector & function holders for minimizing functions.
 *
 * @author Didier H. Besset
 */
public class MinimizingPointFactory extends OptimizingPointFactory
{
/**
 * Constructor method.
 */
public MinimizingPointFactory() {
	super();
}
/**
 * @return OptimizingPoint	an minimizing point strategy.
 */
public OptimizingPoint createPoint(double x, com.pharmadm.num.dhb.DhbInterfaces.OneVariableFunction f)
{
	return new MinimizingPoint( x, f);
}
/**
 * @return OptimizingVector	an minimizing vector strategy.
 */
public OptimizingVector createVector(double[] v, com.pharmadm.num.dhb.DhbInterfaces.ManyVariableFunction f)
{
	return new MinimizingVector( v, f);
}
}