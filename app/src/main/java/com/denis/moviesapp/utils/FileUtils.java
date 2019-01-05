package com.denis.moviesapp.utils;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import static android.content.Context.MODE_PRIVATE;

public class FileUtils {
    private static final int READ_BLOCK_SIZE = 1024;

    public void write(Context context, String filename, String data) throws IOException {
        FileOutputStream fos = context.openFileOutput(filename, MODE_PRIVATE);
        OutputStreamWriter outputWriter = new OutputStreamWriter(fos);
        outputWriter.write(data);
        outputWriter.close();
    }

    public String read(Context context, String filename) throws Exception {
        if (!fileExists(context, filename)) {
            throw (new Exception("File '" + filename + "' was not found!"));
        }

        FileInputStream fis = context.openFileInput(filename);
        InputStreamReader InputRead = new InputStreamReader(fis);

        char[] inputBuffer = new char[READ_BLOCK_SIZE];
        StringBuilder s = new StringBuilder();
        int charRead;

        while ((charRead = InputRead.read(inputBuffer)) > 0) {
            // char to string conversion
            String readString = String.copyValueOf(inputBuffer, 0, charRead);
            s.append(readString);
        }
        InputRead.close();

        return s.toString();
    }

    private Boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        return file != null && file.exists();
    }
}