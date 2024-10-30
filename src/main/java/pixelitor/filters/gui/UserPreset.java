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

package pixelitor.filters.gui;

import com.bric.util.JVM;
import pixelitor.GUIMode;
import pixelitor.colors.Colors;
import pixelitor.filters.Truchet;
import pixelitor.gui.utils.PAction;
import pixelitor.io.FileUtils;
import pixelitor.utils.Messages;

import javax.swing.*;
import java.awt.Color;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static pixelitor.utils.Utils.FILE_SEPARATOR;

/**
 * Represents a user-created preset.
 * It is similar to but different from {@link FilterState} because
 * its internal structure is more suitable for saving as a flat text file.
 */
public class UserPreset implements Preset {
    private final String name;
    private File file;
    private final String presetDirName;
    private boolean loaded; // whether the preset is in the memory
    private final Map<String, String> content = new LinkedHashMap<>();

    public static final String PRESETS_DIR;

    static {
        if (JVM.isWindows) {
            PRESETS_DIR = System.getenv("APPDATA") +
                FILE_SEPARATOR + "Pixelitor" + FILE_SEPARATOR + "presets";
        } else {
            PRESETS_DIR = System.getProperty("user.home") +
                FILE_SEPARATOR + ".pixelitor" + FILE_SEPARATOR + "presets";
        }
    }

    /**
     * Can be used for built-in presets.
     */
    public UserPreset(String name) {
        this(name, null);
    }

    /**
     * Used when a new preset is created by the user
     */
    public UserPreset(String name, String presetDirName) {
        this.name = name;
        this.presetDirName = presetDirName;
        loaded = true;
    }

    /**
     * Used when the existence of a preset file is detected
     */
    public UserPreset(File file, String presetDirName) {
        this.name = FileUtils.removeExtension(file.getName());
        this.file = file;
        this.presetDirName = presetDirName;
        loaded = false;
    }

    public String getName() {
        return name;
    }

    public String get(String key) {
        String value = content.get(key);
        if (value == null) {
            if ("Ray Colors".equals(key)) {
                // oct 2021: temporary hack for compatible color list upgrade of starburst
                value = content.get("Ray Color");
            } else if ("Sides".equals(key)) {
                // sept 2023: 4.3.0 => 4.3.1 migration in Concentric Shapes
                value = content.get("Polygon Sides");
            }

            if (GUIMode.isDevelopment()) {
                System.out.println("UserPreset::get: no value found for the key " + key);
            }
        }

        else {
            // sept 2023: migration in Truchet Tiles
            if (Truchet.migration_helper.containsKey(value)) {
                value = Truchet.migration_helper.get(value);
            }
        }

        return value;
    }

    public void put(String key, String value) {
        assert !key.isBlank();

        content.put(key, value);
    }

    public int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public int getInt(String key, int defaultValue) {
        String value = get(key);
        if (value != null) {
            return Integer.parseInt(value);
        }
        return defaultValue;
    }

    public long getLong(String key, long defaultValue) {
        String value = get(key);
        if (value != null) {
            return Long.parseLong(value);
        }
        return defaultValue;
    }

    public void putInt(String key, int value) {
        put(key, String.valueOf(value));
    }

    public void putLong(String key, long value) {
        put(key, String.valueOf(value));
    }

    public boolean getBoolean(String key) {
        return "yes".equalsIgnoreCase(get(key));
    }

    public void putBoolean(String key, boolean b) {
        put(key, b ? "yes" : "no");
    }

    public float getFloat(String key) {
        return Float.parseFloat(get(key));
    }

    public float getFloat(String key, float defaultValue) {
        String value = get(key);
        if (value != null) {
            return Float.parseFloat(value);
        } else {
            return defaultValue;
        }
    }

    public void putFloat(String key, float f) {
        put(key, String.format(Locale.ENGLISH, "%.4f", f));
    }

    public double getDouble(String key) {
        return Double.parseDouble(get(key));
    }

    public double getDouble(String key, double defaultValue) {
        String value = get(key);
        if (value != null) {
            return Double.parseDouble(value);
        } else {
            return defaultValue;
        }
    }

    public void putDouble(String key, double d) {
        put(key, String.format(Locale.ENGLISH, "%.4f", d));
    }

    public Color getColor(String key) {
        return getColor(key, Color.BLACK);
    }

    public Color getColor(String key, Color defaultValue) {
        String color = get(key);
        if (color != null) {
            return Colors.fromHTMLHex(color);
        }
        return defaultValue;
    }

    public void putColor(String key, Color c) {
        put(key, Colors.toHTMLHex(c, true));
    }

    /**
     * A way to get an enum constant if its toString is overwritten.
     */
    public <T extends Enum<T>> T getEnum(String key, Class<T> clazz) {
        String presetValue = get(key);
        T[] enumConstants = clazz.getEnumConstants();
        for (T constant : enumConstants) {
            if (constant.toString().equals(presetValue)) {
                return constant;
            }
        }
        return enumConstants[0];
    }

    // not using Properties because it is ugly to escape the spaces in keys
    private void loadFromFile() throws IOException {
        assert !loaded;
        InputStream input = new FileInputStream(file);
        Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
        try (BufferedReader br = new BufferedReader(reader)) {
            loadFromReader(br);
        }
        loaded = true;
    }

    private void loadFromReader(BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            int index = line.indexOf('=');
            if (index > 0) {
                String key = line.substring(0, index).trim();
                String value = line.substring(index + 1).trim();
                content.put(key, value);
            }
        }
    }

    public void loadFromString(String s) {
        try {
            loadFromReader(new BufferedReader(new StringReader(s)));
        } catch (IOException e) {
            Messages.showException(e);
        }
    }

    public void save() {
        assert file == null;
        assert loaded;

        File outFile = getSaveFile(true);
        try (PrintWriter writer = new PrintWriter(outFile, StandardCharsets.UTF_8)) {
            saveTo(writer);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        Messages.showStatusMessage("Preset saved to <b>" + outFile.getAbsolutePath() + "</b>");
    }

    private void saveTo(PrintWriter writer) {
        for (Map.Entry<String, String> entry : content.entrySet()) {
            writer.println(entry.getKey() + "=" + entry.getValue());
        }
    }

    public String saveToString() {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        saveTo(printWriter);
        printWriter.flush();
        return writer.toString();
    }

    @Override
    public Action asAction(PresetOwner owner) {
        return new PAction(name, () -> {
            if (!loaded) {
                try {
                    loadFromFile();
                } catch (IOException ex) {
                    throw new UncheckedIOException(ex);
                }
            }
            owner.loadUserPreset(this);
        });
    }

    public static List<UserPreset> loadPresets(String presetDirName) {
        File presetsDir = getSaveDir(presetDirName);
        if (!presetsDir.exists()) {
            return List.of();
        }

        String[] fileNames = presetsDir.list((dir, name) -> name.endsWith(".txt"));
        if (fileNames == null || fileNames.length == 0) {
            return List.of();
        }

        List<UserPreset> list = new ArrayList<>();
        for (String fileName : fileNames) {
            File presetFile = new File(presetsDir, fileName);
            list.add(new UserPreset(presetFile, presetDirName));
        }
        return list;
    }

    public boolean fileExists() {
        return getSaveFile(false).exists();
    }

    private File getSaveFile(boolean createDirs) {
        File dir = getSaveDir(presetDirName);
        if (createDirs && !dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, name + ".txt");
    }

    private static File getSaveDir(String presetDirName) {
        return new File(PRESETS_DIR + FILE_SEPARATOR + presetDirName);
    }

    @Override
    public String toString() {
        return name + " " + content;
    }
}
