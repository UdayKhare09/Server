package com.uday.Encryption;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class EncryptedDataInputStream {

    private DataInputStream dis;

    public EncryptedDataInputStream(InputStream in) {
        dis = new DataInputStream(in);
    }

    public String readUTF() {
        try {
            return new String(AESEncryption.decrypt(dis.readUTF().getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] readNBytes(int n) {
        try {
            byte[] encryptedData = new byte[n];
            dis.readFully(encryptedData, 0, n);
            return AESEncryption.decrypt(encryptedData);
        } catch (IOException e) {
            throw new RuntimeException("Error reading bytes", e);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting data", e);
        }
    }
}
