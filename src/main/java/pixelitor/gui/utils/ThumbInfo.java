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

package pixelitor.gui.utils;

import org.jdesktop.swingx.painter.TextPainter;

import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;

/**
 * Information associated with a thumbnail image
 */
public class ThumbInfo {
    public static final String PREVIEW_ERROR = "Preview Error";
    public static final String NO_PREVIEW = "No Preview";

    private final BufferedImage thumb;

    // not null if the thumb wasn't generated successfully
    private final String errMsg;

    // these sizes refer to the original image, not to the thumb!
    private final int fullWidth;
    private final int fullHeight;

    private ThumbInfo(BufferedImage thumb, int fullWidth, int fullHeight, String errMsg) {
        this.thumb = thumb;
        this.fullWidth = fullWidth;
        this.fullHeight = fullHeight;
        this.errMsg = errMsg;
    }

    public static ThumbInfo success(BufferedImage thumb, int origWidth, int origHeight) {
        return new ThumbInfo(thumb, origWidth, origHeight, null);
    }

    // success, but no original size info
    public static ThumbInfo success(BufferedImage thumb) {
        return new ThumbInfo(thumb, -1, -1, null);
    }

    public static ThumbInfo failure(int origWidth, int origHeight, String errMsg) {
        return new ThumbInfo(null, origWidth, origHeight, errMsg);
    }

    public static ThumbInfo failure(String errMsg) {
        return failure(-1, -1, errMsg);
    }

    public void paint(Graphics2D g, JPanel panel) {
        int width = panel.getWidth();
        int height = panel.getHeight();
        if (errMsg != null) {
            g.setColor(WHITE);
            g.fillRect(0, 0, width, height);
            new TextPainter(errMsg, panel.getFont(), Color.RED)
                .paint(g, null, width, height);
            paintImageSize(g, panel);
            return;
        }

        int x = (width - thumb.getWidth()) / 2 + ImagePreviewPanel.EMPTY_SPACE_AT_LEFT;
        int y = (height - thumb.getHeight()) / 2;
        g.drawImage(thumb, x, y, null);

        paintImageSize(g, panel);
    }

    private void paintImageSize(Graphics2D g, JPanel panel) {
        if (fullWidth == -1 || fullHeight == -1) {
            return;
        }

        String msg = "Size: " + fullWidth + " x " + fullHeight + " pixels";

        Font font = panel.getFont();
        FontMetrics fontMetrics = g.getFontMetrics(font);
        int stringWidth = fontMetrics.stringWidth(msg);
        int stringHeight = fontMetrics.getHeight();
        int drawX = (panel.getWidth() - stringWidth) / 2;
        int drawY = stringHeight + 3;

        g.setColor(BLACK);
        g.drawString(msg, drawX, drawY);

        g.setColor(WHITE);
        g.drawString(msg, drawX - 1, drawY - 1);
    }

    public boolean isSuccess() {
        return errMsg != null;
    }
}
