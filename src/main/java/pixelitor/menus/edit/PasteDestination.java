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

package pixelitor.menus.edit;

import pixelitor.Canvas;
import pixelitor.Views;
import pixelitor.colors.Colors;
import pixelitor.layers.Layer;
import pixelitor.layers.LayerMask;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static java.awt.image.BufferedImage.TYPE_BYTE_GRAY;

/**
 * Represents the destination of a pasted image
 */
public enum PasteDestination {
    NEW_LAYER(true) {
        @Override
        public String getResourceKey() {
            return "paste_as_new_layer";
        }

        @Override
        void paste(BufferedImage pastedImage) {
            var comp = Views.getActiveComp();
            comp.addExternalImageAsNewLayer(pastedImage,
                "Pasted Layer", "New Pasted Layer");
        }
    }, NEW_IMAGE(false) {
        @Override
        public String getResourceKey() {
            return "paste_as_new_img";
        }

        @Override
        void paste(BufferedImage pastedImage) {
            Views.addNewPasted(pastedImage);
        }

    }, MASK(true) {
        @Override
        public String getResourceKey() {
            return "paste_as_layer_mask";
        }

        @Override
        void paste(BufferedImage pastedImage) {
            var comp = Views.getActiveComp();
            Canvas canvas = comp.getCanvas();
            int canvasWidth = canvas.getWidth();
            int canvasHeight = canvas.getHeight();

            int imgWidth = pastedImage.getWidth();
            int imgHeight = pastedImage.getHeight();

            // the mask image will be canvas-sized, even
            // if the pasted image has a different size
            BufferedImage bwImage = new BufferedImage(
                canvasWidth, canvasHeight, TYPE_BYTE_GRAY);

            Graphics2D g = bwImage.createGraphics();
            // if the pasted image is too small, pad it with white
            if (!canvas.isFullyCoveredBy(pastedImage)) {
                Colors.fillWith(Color.WHITE, g, canvasWidth, canvasHeight);
            }

            // center the pasted image
            int x = (canvasWidth - imgWidth) / 2;
            int y = (canvasHeight - imgHeight) / 2;
            g.drawImage(pastedImage, x, y, null);

            g.dispose();

            Layer layer = comp.getActiveLayer();
            if (layer.hasMask()) {
                LayerMask mask = layer.getMask();
                mask.replaceImage(bwImage, "Replace Mask");
            } else {
                layer.addImageAsMask(bwImage, false, true,
                    true, "Add Pasted Mask", false);
            }
        }
    };

    private final boolean requiresOpenView;

    PasteDestination(boolean requiresOpenView) {
        this.requiresOpenView = requiresOpenView;
    }

    abstract void paste(BufferedImage pastedImage);

    public boolean requiresOpenView() {
        return requiresOpenView;
    }

    abstract String getResourceKey();
}
