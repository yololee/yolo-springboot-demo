package com.yolo.demo.domain;

import lombok.Cleanup;
import lombok.SneakyThrows;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Demo8 {

    @SneakyThrows(Exception.class)
    public static void main(String[] args) {
        @Cleanup InputStream in = Files.newInputStream(Paths.get(args[0]));
        @Cleanup OutputStream out = Files.newOutputStream(Paths.get(args[1]));
        byte[] b = new byte[1024];
        while (true) {
            int r = in.read(b);
            if (r == -1) {
                break;
            }
            out.write(b, 0, r);
        }
    }
}
