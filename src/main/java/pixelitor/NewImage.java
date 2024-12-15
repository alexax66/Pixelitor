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

package pixelitor;

import pixelitor.colors.FillType;
import pixelitor.filters.Fill;
import pixelitor.filters.gui.DialogMenuBar;
import pixelitor.gui.NewImagePanel;
import pixelitor.gui.utils.DialogBuilder;
import pixelitor.gui.utils.TaskAction;
import pixelitor.utils.AppPreferences;
import pixelitor.utils.ImageUtils;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import static pixelitor.colors.FillType.TRANSPARENT;
import static pixelitor.utils.Texts.i18n;

/**
 * Static methods for creating new images
 */
public final class NewImage {
    private static final String NEW_IMAGE_STRING = i18n("new_image");
    private static int untitledCount = 1;

    public static final Dimension lastSize = AppPreferences.getNewImageSize();
    public static int lastFillTypeIndex = 0; // currently not restored after a restart

    private NewImage() {
    }

    public static Composition addNewImage(FillType bg, int width, int height, String title) {
        var comp = createNewComposition(bg, width, height, title);
        Views.addNew(comp);
        return comp;
    }

    public static Composition createNewComposition(FillType bg, int width, int height, String name) {
        BufferedImage newImage = ImageUtils.createSysCompatibleImage(width, height);
        if (bg != TRANSPARENT) {
            Fill.fillImage(newImage, bg.getColor());
        }

        return Composition.fromImage(newImage, null, name);
    }

    private static void showInDialog() {
        var panel = new NewImagePanel();
        new DialogBuilder()
            .title(NEW_IMAGE_STRING)
            .menuBar(new DialogMenuBar(panel))
            .validatedContent(panel)
            .okAction(panel::okPressedInDialog)
            .show();
    }

    public static Action getAction() {
        return new TaskAction(NEW_IMAGE_STRING + "...", NewImage::showInDialog);
    }

    public static String generateTitle() {
        return "Untitled " + untitledCount++;
    }
}

