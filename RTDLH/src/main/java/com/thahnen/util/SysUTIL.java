package com.thahnen.util;

public final class SysUTIL {

    public enum OS {
        WINDOWS,
        LINUX,
        MACOSX,
        OTHER
        // ...
    }

    public static OS getOS() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) return OS.WINDOWS;
        else if (os.contains("linux")) return OS.LINUX;
        else if (os.contains("mac")) return OS.MACOSX;
        else return OS.OTHER;
    }

    public static String getHomeDir() {
        switch (getOS()) {
            case WINDOWS:
                return System.getenv("HOMEPATH");
            case LINUX:
            case MACOSX:
                return System.getenv("HOME");
        }

        // ist ok, da IMMER nach getOS aufgerufen!
        return null;
    }
}
