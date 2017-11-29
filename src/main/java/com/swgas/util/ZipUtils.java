package com.swgas.util;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {
    private static final String CLASS = ZipUtils.class.getName();
    private static final Logger LOG   = Logger.getLogger(CLASS);
    private static final int LEN      = 8192;

    public static void unZip(InputStream in, Path outputDirectory) {
        try (ZipInputStream zipStream = new ZipInputStream(in)) {
            Files.createDirectories(outputDirectory);
            ZipEntry entry;
            while((entry = zipStream.getNextEntry()) != null) {
                boolean dir = entry.isDirectory();
                Path path = outputDirectory.resolve(Arrays.stream(entry.getName().split("/")).skip(1).reduce("", (a, b) -> a.isEmpty() ? b : String.format("%s/%s", a, b)));
                //LOG.finest(String.format("path: %s (%s)", path, dir ? "directory" : "file"));
                if (dir) {
                    Files.createDirectories(path);
                } else {
                    try(BufferedWriter writer = Files.newBufferedWriter(path)){
                        byte[] buff = new byte[LEN];
                        int count;
                        while ((count = zipStream.read(buff)) != -1) {
                            writer.append(new String(buff, 0, count));
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.severe(e.toString());
        }
    }
}