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
package pixelitor.tools;

import pixelitor.tools.brushes.AngledShapeBrush;
import pixelitor.tools.brushes.Brush;
import pixelitor.tools.brushes.BrushShapeProvider;
import pixelitor.tools.brushes.CalligraphyBrush;
import pixelitor.tools.brushes.IdealBrush;
import pixelitor.tools.brushes.ImageBrushType;
import pixelitor.tools.brushes.OutlineCircleBrush;
import pixelitor.tools.brushes.OutlineSquareBrush;
import pixelitor.tools.brushes.UniformImageBrush;
import pixelitor.tools.brushes.WobbleBrush;

/**
 * The brush types the user can use
 */
enum BrushType {
    IDEAL {
        @Override
        public String toString() {
            return "Hard";
        }

        @Override
        public Brush createBrush() {
            return new IdealBrush();
        }

    }, SOFT {
        @Override
        public String toString() {
            return "Soft";
        }

        @Override
        public Brush createBrush() {
            return new UniformImageBrush(ImageBrushType.SOFT, 0.25, false);
        }
    }, WOBBLE {
        @Override
        public String toString() {
            return "Wobble";
        }

        @Override
        public Brush createBrush() {
            return new WobbleBrush();
        }
    }, CALLIGRAPHY {
        @Override
        public String toString() {
            return "Calligraphy";
        }

        @Override
        public Brush createBrush() {
            return new CalligraphyBrush();
        }
    }, REALISTIC {
        @Override
        public String toString() {
            return "Realistic";
        }

        @Override
        public Brush createBrush() {
            return new UniformImageBrush(ImageBrushType.REAL, 0.05, false);
        }
    }, HAIR {
        @Override
        public String toString() {
            return "Hair";
        }

        @Override
        public Brush createBrush() {
            return new UniformImageBrush(ImageBrushType.HAIR, 0.02, false);
        }
    }, HEART {
        @Override
        public String toString() {
            return "Heart";
        }

        @Override
        public Brush createBrush() {
            return new AngledShapeBrush(BrushShapeProvider.HEART, 2.3);
        }


//    }, ARROW {
//        @Override
//        public String toString() {
//            return "Image-Based Arrow";
//        }
//
//        @Override
//        public Brush getBrush() {
//            return new UniformImageBrush(ImageBrushType.ARROW, 2.5, true);
//        }
//
//    }, GREEK {
//        @Override
//        public String toString() {
//            return "Image-Based Greek";
//        }
//
//        @Override
//        public Brush getBrush() {
//            return new UniformImageBrush(ImageBrushType.GREEK, 2.0, true);
//        }


    }, OUTLINE_CIRCLE {
        private OutlineCircleBrush outlineCircleBrush;

        @Override
        public String toString() {
            return "Circles";
        }

        @Override
        public Brush createBrush() {
            return new OutlineCircleBrush();
        }
    }, OUTLINE_SQUARE {
        private OutlineSquareBrush outlineSquareBrush;

        @Override
        public String toString() {
            return "Squares";
        }

        @Override
        public Brush createBrush() {
            return new OutlineSquareBrush();
        }
    };

    public abstract Brush createBrush();
}
