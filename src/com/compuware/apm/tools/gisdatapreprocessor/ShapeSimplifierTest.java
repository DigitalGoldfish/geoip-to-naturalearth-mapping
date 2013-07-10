/**
 *
 */
package com.compuware.apm.tools.gisdatapreprocessor;

import org.junit.Test;

import com.vividsolutions.jts.geom.Point;

/**
 * @author cwat-moehler
 *
 */
public class ShapeSimplifierTest extends PolygonBorderPreservingSimplifier {

	@Test
	public void testPointToString() {
		Point p = new Point();
		String stringRepresentation = PolygonBorderPreservingSimplifier.generateStringIdForPoint(p);
	}



}
