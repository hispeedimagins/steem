package com.steemapp.lokisveil.steemapp.Crypto.core;

import org.spongycastle.crypto.prng.EntropySource;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by boot on 2/27/2018.
 */

public class AndroidRandomSource implements EntropySource {

    //@Override
    public synchronized void nextBytes(byte[] bytes) {
        // On Android we use /dev/urandom for providing random data
        File file = new File("/dev/urandom");
        if (!file.exists()) {
            throw new RuntimeException("Unable to generate random bytes on this Android device");
        }
        try {
            FileInputStream stream = new FileInputStream(file);
            DataInputStream dis = new DataInputStream(stream);
            dis.readFully(bytes);
            dis.close();
        } catch (IOException e) {
            throw new RuntimeException("Unable to generate random bytes on this Android device", e);
        }
    }

   // @Override
    public ByteBuffer provideEntropy() {
        byte[] buffer = new byte[ 256 / 8];
        nextBytes(buffer);
        ByteBuffer byteBuffer = ByteBuffer.allocate(buffer.length);
        byteBuffer.put(buffer, 0, buffer.length);
        return byteBuffer;
    }

    @Override
    public boolean isPredictionResistant() {
        return false;
    }

    @Override
    public byte[] getEntropy() {
        return new byte[0];
    }

    @Override
    public int entropySize() {
        return 0;
    }
}
