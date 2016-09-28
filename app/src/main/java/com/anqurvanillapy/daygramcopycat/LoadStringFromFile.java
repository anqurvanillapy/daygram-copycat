package com.anqurvanillapy.daygramcopycat;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * =================================================================================================
 * File Loader
 * =================================================================================================
 */

public class LoadStringFromFile {
    public String loadStringFromFile(File file) {
        int size;
        byte[] buffer;
        InputStream is;
        String string = null;

        try {
            is = new FileInputStream(file);
            size = is.available();
            buffer = new byte[size];
            is.read(buffer);
            is.close();
            string = new String(buffer, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return string;
    }
}
