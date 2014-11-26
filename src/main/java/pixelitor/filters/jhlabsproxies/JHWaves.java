/*
 * Copyright 2010-2014 Laszlo Balazs-Csiki
 *
 * This file is part of Pixelitor. Pixelitor is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License, version 3 as published by the Free
 * Software Foundation.
 *
 * Pixelitor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Pixelitor.  If not, see <http://www.gnu.org/licenses/>.
 */
package pixelitor.filters.jhlabsproxies;

import com.jhlabs.image.RippleFilter;
import pixelitor.filters.FilterWithParametrizedGUI;
import pixelitor.filters.gui.CoupledRangeParam;
import pixelitor.filters.gui.IntChoiceParam;
import pixelitor.filters.gui.ParamSet;
import pixelitor.filters.gui.ReseedNoiseActionParam;

import java.awt.image.BufferedImage;

/**
 * Ripple based on the JHLabs RippleFilter
 */
public class JHWaves extends FilterWithParametrizedGUI {
    CoupledRangeParam wavelengthParam = new CoupledRangeParam("Wavelength", 1, 200, 20);
    CoupledRangeParam amplitudeParam = new CoupledRangeParam("Amplitude", 0, 200, 10);

    CoupledRangeParam phaseParam = new CoupledRangeParam("Phase (Time)", 0, 100, 0, false);

//    RangeParam phaseXParam = new RangeParam("Horizontal Phase", 0, 100, 0);
//    RangeParam phaseYParam = new RangeParam("Vertical Phase", 0, 100, 0);


    private final IntChoiceParam edgeAction = IntChoiceParam.getEdgeActionChoices();
    private final IntChoiceParam interpolation = IntChoiceParam.getInterpolationChoices();

    private final IntChoiceParam waveType = IntChoiceParam.getWaveTypeChoices();

    private RippleFilter filter;

    public JHWaves() {
        super("Waves", true, false);
        setParamSet(new ParamSet(
                wavelengthParam.adjustRangeAccordingToImage(0.2),
                amplitudeParam.adjustRangeAccordingToImage(0.2),
                waveType,
                phaseParam,
                edgeAction,
                interpolation,
                new ReseedNoiseActionParam("Reseed Noise")
        ));
    }

    @Override
    public BufferedImage doTransform(BufferedImage src, BufferedImage dest) {
        int xAmplitude = amplitudeParam.getFirstValue();
        int yAmplitude = amplitudeParam.getSecondValue();

        if (xAmplitude == 0 && yAmplitude == 0) {
            return src;
        }

        if (filter == null) {
            filter = new RippleFilter();
        }

        int xWavelength = wavelengthParam.getFirstValue();
        int yWavelength = wavelengthParam.getSecondValue();

        filter.setXAmplitude(xAmplitude);
        filter.setXWavelength(xWavelength);
        filter.setYAmplitude(yAmplitude);
        filter.setYWavelength(yWavelength);
        filter.setWaveType(waveType.getValue());
        filter.setPhaseX(phaseParam.getFirstValueAsPercentage());
        filter.setPhaseY(phaseParam.getSecondValueAsPercentage());

        filter.setEdgeAction(edgeAction.getValue());
        filter.setInterpolation(interpolation.getValue());

        dest = filter.filter(src, dest);
        return dest;
    }
}