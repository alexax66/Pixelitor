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
import pixelitor.Views;
import pixelitor.filters.Colorize;
import pixelitor.filters.Filter;
import pixelitor.filters.GradientMap;
import pixelitor.filters.HueSat;
import pixelitor.filters.curves.ToneCurvesFilter;
import pixelitor.filters.lookup.ColorBalance;
import pixelitor.filters.util.FilterAction;
import pixelitor.filters.util.FilterSearchPanel;
import pixelitor.gui.utils.AbstractViewEnabledAction;
import pixelitor.gui.utils.TaskAction;
import pixelitor.gui.utils.ThemedImageIcon;
import pixelitor.utils.Icons;
import pixelitor.utils.Messages;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.function.Supplier;

import static java.awt.event.ActionEvent.CTRL_MASK;

/**
 * An Action that adds a new adjustment layer to the active composition.
 */
public class AddAdjLayerAction extends AbstractViewEnabledAction {
    public static final AddAdjLayerAction INSTANCE = new AddAdjLayerAction();

    public static final List<Action> actions = List.of(
        createAction(ColorBalance::new, ColorBalance.NAME),
        createAction(Colorize::new, Colorize.NAME),
        createAction(ToneCurvesFilter::new, ToneCurvesFilter.NAME),
        createAction(GradientMap::new, GradientMap.NAME),
        createAction(HueSat::new, HueSat.NAME)
//        createAction(Levels::new, Levels.NAME)
    );

    private AddAdjLayerAction() {
        super("Add Adjustment Layer",
            Icons.loadThemed("add_adj_layer.png", ThemedImageIcon.GREEN));
        setToolTip("Adds a new adjustment layer.");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            boolean ctrlPressed = (e.getModifiers() & CTRL_MASK) == CTRL_MASK;
            if (ctrlPressed) {
                FilterAction action = FilterSearchPanel.showInDialog("Find Adjustment Layer");
                if (action != null) {
                    Filter filter = action.createNewFilterInstance();
                    addAdjustmentLayer(filter, filter.getName());
                }
                return;
            }

            JPopupMenu popup = createActionsPopup();
            Dimension size = popup.getPreferredSize();
            popup.show((JButton) e.getSource(), 0, -size.height);
        } catch (Exception ex) {
            Messages.showException(ex);
        }
    }

    @Override
    protected void onClick(Composition comp) {
        // never called, because this class overrides actionPerformed
        throw new UnsupportedOperationException();
    }

    private static JPopupMenu createActionsPopup() {
        JPopupMenu popup = new JPopupMenu();
        for (Action action : actions) {
            popup.add(action);
        }
        popup.pack();
        return popup;
    }

    private static Action createAction(Supplier<Filter> factory, String name) {
        return new TaskAction("New " + name, () ->
            addAdjustmentLayer(factory, name));
    }

    private static void addAdjustmentLayer(Supplier<Filter> factory, String name) {
        Filter filter = factory.get();
        filter.setName(name);
        addAdjustmentLayer(filter, name);
    }

    private static void addAdjustmentLayer(Filter filter, String name) {
        var comp = Views.getActiveComp();
        var adjustmentLayer = new AdjustmentLayer(comp, name, filter);

        comp.getHolderForNewLayers()
            .addWithHistory(adjustmentLayer, "New Adjustment Layer");

        adjustmentLayer.edit();
    }
}