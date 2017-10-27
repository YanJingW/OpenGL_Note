/*
 * <copyright file="RawResourceReader.java" company="Qihoo 360 Corporation">
 * Copyright (c) Qihoo 360 Corporation. All rights reserved.
 * </copyright>
 */

package com.yjing.openglnote;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * RawResourceReader class.
 */
public class RawResourceReader {
    public static String readTextFileFromRawResource(final Context context,
                                                     final int resourceId) {
        final StringBuilder body = new StringBuilder();
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            inputStream = context.getResources().openRawResource(
                    resourceId);
            inputStreamReader = new InputStreamReader(
                    inputStream);
            bufferedReader = new BufferedReader(
                    inputStreamReader);

            String nextLine;
            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }
        } catch (IOException e) {
            return null;
        } finally {
            Utils.closeInStream(inputStream);
            Utils.closeReader(inputStreamReader);
            Utils.closeReader(bufferedReader);
        }
        return body.toString();
    }
}