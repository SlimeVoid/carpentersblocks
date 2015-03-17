package com.carpentersblocks.util.slope.type;

import com.carpentersblocks.data.Slope;
import com.carpentersblocks.util.slope.SlopeType;

public class PrismWedge implements SlopeType {

	@Override
	public int onHammerLeftClick(Slope slope, int slopeID) {
		if (++slopeID > Slope.ID_PRISM_WEDGE_POS_E) {
            slopeID = Slope.ID_PRISM_WEDGE_POS_N;
        }
		return slopeID;
	}

	@Override
	public int onHammerRightClick(Slope slope, int slopeID) {
		slopeID = Slope.ID_WEDGE_SE;
		return slopeID;
	}

}
