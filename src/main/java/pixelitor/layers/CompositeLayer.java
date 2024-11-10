/*
 * Copyright 2024 Laszlo Balazs-Csiki and Contributors
 *
 * This file is part of Pixelitor. Pixelitor is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License, version 3 as published by the Free
 * Software Foundation.
 *
 * Pixelitor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Pixelitor. If not, see <http://www.gnu.org/licenses/>.
 */

package pixelitor.layers;

import pixelitor.Composition;

import java.awt.EventQueue;
import java.io.Serial;

/**
 * A {@link ContentLayer} that is also a {@link LayerHolder},
 * and therefore can contain nested layers.
 */
public abstract class CompositeLayer extends ContentLayer implements LayerHolder {
    @Serial
    private static final long serialVersionUID = 8962987501686783478L;

    CompositeLayer(Composition comp, String name) {
        super(comp, name);
    }

    // initialization that requires a deserialized canvas
    public abstract void afterDeserialization();

    public void updateChildrenUI() {
        if (ui == null) { // in some unit tests
            return;
        }
        ui.updateChildrenPanel();
        EventQueue.invokeLater(this::revalidateUI);
    }

    private void revalidateUI() {
        LayersPanel layersPanel = comp.getView().getLayersPanel();
        if (layersPanel != null) { // null in unit tests
            layersPanel.revalidate();
        }
    }
}
