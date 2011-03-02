package saci.util;

public class ByteArrayInputStream extends java.io.ByteArrayInputStream {
    
    private final byte[] buffer;

    public ByteArrayInputStream(byte[] buf) {
        super(buf);
        this.buffer = buf;
    }

    public byte[] getBuffer() {
        return buffer;
    }

}
