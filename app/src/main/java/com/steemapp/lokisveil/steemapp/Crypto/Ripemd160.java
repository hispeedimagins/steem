package com.steemapp.lokisveil.steemapp.Crypto;

import org.joou.UInteger;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by boot on 2/18/2018.
 */

public class Ripemd160 implements Serializable {
    /** Generated serial uid. */
    //private static final long serialVersionUID = 7984783145088522082L;
    private byte[] hashValue;

    public Ripemd160(String hashValue) {
        this.setHashValue(hashValue);
    }
    /**
     * Convert the first four bytes of the hash into a number.
     *
     * @return The number.
     */
    /*this.setRefBlockNum(UShort.valueOf(blockId.getNumberFromHash() & 0xffff));
    this.setRefBlockPrefix(blockId.getHashValue());*/
    public int getNumberFromHash() {
        byte[] fourBytesByte = new byte[4];
        System.arraycopy(hashValue, 0, fourBytesByte, 0, 4);
        return ByteBuffer.wrap(fourBytesByte).order(ByteOrder.BIG_ENDIAN).getInt();
    }

    /**
     * Get the wrapped hash value in its long representation.
     *
     * @return The wrapped hash value in its long representation.
     */
    public UInteger getHashValue() {
        return UInteger.valueOf(CryptoUtils.readUint32(hashValue, 4));
    }



    /**
     * Set the hash value by providing its decoded byte representation.
     *
     * @param hashValue
     *            The hash to wrap.
     */
    public void setHashValue(byte[] hashValue) {
        this.hashValue = hashValue;
    }

    /**
     * Set the hash value by providing its encoded String representation.
     *
     * @param hashValue
     *            The hash to wrap.
     */
    public void setHashValue(String hashValue) {
        this.hashValue = CryptoUtils.HEX.decode(hashValue);
    }


    public byte[] toByteArray()  {
        return this.hashValue;
    }

    @Override
    public String toString() {
        return CryptoUtils.HEX.encode(this.hashValue);
    }
}
