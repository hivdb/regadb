package com.pharmadm.num.dhb.DhbDataMining;

import com.pharmadm.num.dhb.DhbMatrixAlgebra.DhbVector;
import com.pharmadm.num.dhb.DhbMatrixAlgebra.SymmetricMatrix;
import com.pharmadm.num.dhb.DhbMatrixAlgebra.DhbIllegalDimension;
/**
 * Cluster using Mahalanobis distance.
 * 
 * @author Didier H. Besset
 */
public class CovarianceCluster extends Cluster
{
	private MahalanobisCenter center = null;
/**
 * Default constructor method.
 */
public CovarianceCluster()
{
	super();
}
/**
 * Constructor method.
 * @param DhbVector center of the receiver
 */
public CovarianceCluster(DhbVector v)
{
	super(v);
}
/**
 * Accumulation is delegated to the Mahalanobis center.
 */
public void accumulate(com.pharmadm.num.dhb.DhbMatrixAlgebra.DhbVector dataPoint)
{
	center.accumulate( dataPoint);
}
/**
 * Distance computation is delegated to the Mahalanobis center.
 */
public double distanceTo( DhbVector dataPoint)
{
	return center.distanceTo( dataPoint);
}
/**
 * @return long	number of data points accumulated in the receiver
 */
public long getSampleSize()
{
	return center.getCount();
}
/**
 * @param v DhbVector	center for the receiver
 */
public void initialize(DhbVector v)
{
	center = new MahalanobisCenter( v);
}
/**
 * @return boolean	true if the cluster is in an undefined state.
 */
public boolean isUndefined()
{
	return center == null;
}
public void reset()
{
	super.reset();
	center.computeParameters();
	center.reset();
}
/**
 * @return java.lang.String
 */
public String toString() {
	return center.toString();
}
}