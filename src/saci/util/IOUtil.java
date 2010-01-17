/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2009 SACI Informática Ltda.
 */

package saci.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

public class IOUtil {

    private static final int BUFFER_SIZE = 8192;

    public static void main(String[] args) {
        if (args.length != 2) {
            printUsage();
            return;
        }
        String acao = args[0];
        String arquivo = args[1];
        File file = new File(arquivo);
        if (!file.exists() || file.isDirectory()) {
            System.out.println("Invalid file");
            return;
        }
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            if ("createHexString".equalsIgnoreCase(acao)) {
                byte[] b = readBytes(is);
                System.out.print(createHexString(b));
            } else if ("getBytesFromHexString".equalsIgnoreCase(acao)) {
                String s = readString(is);
                System.out.write(getBytesFromHexString(s));
            } else if ("createMD5Checksum".equalsIgnoreCase(acao)) {
                System.out.print(createHexString(createMD5Checksum(is)));
            } else if ("createSHA1Checksum".equalsIgnoreCase(acao)) {
                System.out.print(createHexString(createSHA1Checksum(is)));
            } else if ("createCRC32Checksum".equalsIgnoreCase(acao)) {
                System.out.print(createHexString(String.valueOf(createCRC32Checksum(is)).getBytes()));
            } else {
                printUsage();
            }
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            try {
                is.close();
            } catch (Exception ignored) {
            }
        }
    }

    private static byte[] readBytes(InputStream is) throws IOException {
        byte[] b = new byte[is.available()];
        is.read(b);
        return b;
    }

    private static String readString(InputStream is) throws IOException {
        byte[] b = readBytes(is);
        return new String(b);
    }

    private static void printUsage() {
        System.out.println(
                "Usage:" +
                "\n  IOUtil createHexString inputFile" +
                "\n    generates an hex (text) file from input file" +
                "\n" +
                "\n  IOUtil getBytesFromHexString inputFile" +
                "\n    generates a byte (binary) file from input file (hex text file)" +
                "\n" +
                "\n  IOUtil createMD5Checksum inputFile" +
                "\n    MD5 checksum for input file" +
                "\n" +
                "\n  IOUtil createSHA1Checksum inputFile" +
                "\n    SHA-1 checksum for input file" +
                "\n" +
                "\n  IOUtil createCRC32Checksum inputFile" +
                "\n    CRC32 checksum for input file" +
                "\n" +
                "\n  ** Use \"IOUtil option inputFile > outFile\" to generate" +
                "\n     a new file with the process results");
    }

    public static String createHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            int v = b & 0xFF;
            sb.append(Integer.toHexString(v >>> 4)).append(Integer.toHexString(v & 0xF));
        }
        return sb.toString();
    }

    public static byte[] getBytesFromHexString(String string) {
        byte[] bytes = new byte[string.length() / 2];
        for (int i = 0, j = 0; i < string.length();) {
            int i1 = Character.digit(string.charAt(i++), 16);
            int i2 = Character.digit(string.charAt(i++), 16);
            bytes[j++] = (byte) ((i1 << 4) + (i2 & 0xF));
        }
        return bytes;
    }

    public static byte[] createChecksum(InputStream in, String algorithm) throws NoSuchAlgorithmException, IOException {
        if (algorithm == null) {
            throw new NullPointerException();
        }
        if (algorithm.equals("CRC32")) {
            return String.valueOf(createCRC32Checksum(in)).getBytes();
        }
        byte[] buffer = new byte[BUFFER_SIZE];
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        int length;
        while ((length = in.read(buffer)) > -1) {
            digest.update(buffer, 0, length);
        }
        in.close();
        return digest.digest();
    }

    public static byte[] createMD5Checksum(InputStream in) throws IOException {
        try {
            return createChecksum(in, "MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] createSHA1Checksum(InputStream in) throws IOException {
        try {
            return createChecksum(in, "SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static long createCRC32Checksum(InputStream in) throws IOException {
        byte[] buf = new byte[BUFFER_SIZE];
        int length = -1;
        CRC32 crc = new CRC32();
        while ((length = in.read(buf)) > 0) {
            crc.update(buf, 0, length);
        }
        long checksum = crc.getValue();
        return checksum;
    }

    public static long createCRC32Checksum(File file) throws IOException {
        InputStream in = new FileInputStream(file);
        try {
            return createCRC32Checksum(in);
        } finally {
            in.close();
        }
    }

    public static long createCRC32Checksum(URL url) throws IOException {
        InputStream in = url.openStream();
        try {
            return createCRC32Checksum(in);
        } finally {
            in.close();
        }
    }
}
