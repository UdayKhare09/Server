package com.uday.Encryption;

import java.io.IOException;
import java.io.OutputStream;
import java.io.DataOutputStream;

public class EncryptedDataOutputStream {

    private final DataOutputStream dos;

    public EncryptedDataOutputStream(OutputStream out) {
        dos = new DataOutputStream(out);
    }

    public void writeUTF(String str) {
        try {
            dos.writeUTF(new String(AESEncryption.encrypt(str.getBytes())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void write(byte[] data) {
        try {
            dos.write(AESEncryption.encrypt(data));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void flush() {
        try {
            dos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            dos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(byte[] packet, int start, int length) {
        try {
            byte[] data = new byte[length];
            System.arraycopy(packet, start, data, 0, length);
            dos.write(AESEncryption.encrypt(data));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
