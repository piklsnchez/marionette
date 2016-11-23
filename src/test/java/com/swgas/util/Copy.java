package com.swgas.util;

import java.io.File;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class Copy {
    private static final String CLASS = Copy.class.getName();
    private static final Logger LOG   = Logger.getLogger(CLASS);
    
    private static class TreeCopier implements FileVisitor<Path> {
        private final Path source;
        private final Path target;

        private TreeCopier(Path source, Path target) {
            this.source = source;
            this.target = target;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
            CopyOption[] options = new CopyOption[]{StandardCopyOption.COPY_ATTRIBUTES};
            Path newdir = target.resolve(source.relativize(dir));
            try {
                Files.copy(dir, newdir, options);
            } catch (FileAlreadyExistsException e) {
            } catch (IOException e) {
                LOG.throwing(CLASS, "preVisitDirectory", e);
                return FileVisitResult.SKIP_SUBTREE;
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException{
            CopyOption[] options = new CopyOption[]{StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING};
            Path dest = target.resolve(source.relativize(file));
            Files.deleteIfExists(dest);
            Files.copy(file, dest, options);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException{
            // fix up modification time of directory when done
            if (e == null) {
                Path newdir = target.resolve(source.relativize(dir));
                FileTime time = Files.getLastModifiedTime(dir);
                Files.setLastModifiedTime(newdir, time);
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException e) throws IOException{
            if (e instanceof FileSystemLoopException) {
                LOG.info(String.format("cycle detected: %s", file));
            } else {
                throw e;
            }
            return FileVisitResult.CONTINUE;
        }
    }

    public static boolean copyDirectory(String src, String target) {
        Path source = Paths.get(Objects.requireNonNull(src));
        Path destination = Paths.get(Objects.requireNonNull(target));
        if (!source.toFile().isDirectory() || !destination.toFile().getParentFile().isDirectory()) {
            throw new IllegalArgumentException(String.format("%s and %s must be directories", src, target));
        }
        // check if target is a directory
        EnumSet<FileVisitOption> opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
        TreeCopier tc = new TreeCopier(source, destination);
        try {
            Files.walkFileTree(source, opts, Integer.MAX_VALUE, tc);
        } catch (IOException e) {
            LOG.throwing(CLASS, target, e);
            return false;
        }
        return true;
    }
    
    public static void main(String... arg){
        String src = System.getProperty("user.home").concat("/.mozilla/firefox/marionette").replace('/', File.separatorChar);
        String tmp = System.getProperty("java.io.tmpdir").concat("/ffProf");
        boolean success = copyDirectory(src, tmp);
        LOG.info(String.format("success: (%s) and %s was created: %s", success, tmp, Paths.get(tmp).toFile().exists()));
        success = copyDirectory(tmp, src);
        LOG.info(String.format("success: (%s) and %s was restored: %s", success, src, Paths.get(src).toFile().exists()));
    }
}
