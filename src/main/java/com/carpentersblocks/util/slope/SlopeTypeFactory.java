package com.carpentersblocks.util.slope;

import com.carpentersblocks.data.Slope.Type;
import com.carpentersblocks.util.slope.type.*;

public class SlopeTypeFactory {

	private static SlopeTypeFactory instance = null;

	public static SlopeTypeFactory getInstance() {
		if (instance == null) {
			instance = new SlopeTypeFactory();
		}
		return instance;
	}

	public SlopeType getSlope(Type type) {
		switch (type) {
		case WEDGE_SIDE:
			return new WedgeSide();
		case WEDGE:
			return new Wedge();
		case WEDGE_INT:
			return new WedgeInt();
		case WEDGE_EXT:
			return new WedgeExt();
		case OBLIQUE_INT:
			return new ObliqueInt();
		case OBLIQUE_EXT:
			return new ObliqueExt();
		case PRISM:
			return new Prism();
		case PRISM_1P:
			return new Prism1P();
		case PRISM_2P:
			return new Prism2P();
		case PRISM_3P:
			return new Prism3P();
		case PRISM_4P:
			return new Prism4P();
		case PRISM_WEDGE:
			return new PrismWedge();
		default:
			return null;
		}
	}

}
