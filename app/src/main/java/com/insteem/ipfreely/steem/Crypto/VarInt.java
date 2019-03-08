package com.insteem.ipfreely.steem.Crypto;

/**
 * Created by boot on 2/23/2018.
 */

public class VarInt {
    /** The value wrapped by this instance. */
    public final long value;
    private final int originallyEncodedSize;

    /**
     * Constructs a new VarInt with the given unsigned long value.
     *
     * @param value
     *            the unsigned long value (beware widening conversion of
     *            negatives!)
     */
    public VarInt(long value) {
        this.value = value;
        originallyEncodedSize = getSizeInBytes();
    }

    /**
     * Constructs a new VarInt with the value parsed from the specified offset
     * of the given buffer.
     *
     * @param buf
     *            the buffer containing the value
     * @param offset
     *            the offset of the value
     */
    public VarInt(byte[] buf, int offset) {
        int first = 0xFF & buf[offset];
        if (first < 253) {
            value = first;
            originallyEncodedSize = 1; // 1 data byte (8 bits)
        } else if (first == 253) {
            value = (0xFF & buf[offset + 1]) | ((0xFF & buf[offset + 2]) << 8);
            originallyEncodedSize = 3; // 1 marker + 2 data bytes (16 bits)
        } else if (first == 254) {
            value = CryptoUtils.readUint32(buf, offset + 1);
            originallyEncodedSize = 5; // 1 marker + 4 data bytes (32 bits)
        } else {
            value = CryptoUtils.readInt64(buf, offset + 1);
            originallyEncodedSize = 9; // 1 marker + 8 data bytes (64 bits)
        }
    }

    /**
     * @return the original number of bytes used to encode the value if it was
     *         deserialized from a byte array, or the minimum encoded size if it
     *         was not.
     */
    public int getOriginalSizeInBytes() {
        return originallyEncodedSize;
    }

    /**
     * @return the minimum encoded size of the value.
     */
    public final int getSizeInBytes() {
        return sizeOf(value);
    }

    /**
     * Returns the minimum encoded size of the given unsigned long value.
     *
     * @param value
     *            the unsigned long value (beware widening conversion of
     *            negatives!)
     * @return the minimum encoded size of the given unsigned long value
     */
    public static int sizeOf(long value) {
        // if negative, it's actually a very large unsigned long value
        if (value < 0)
            return 9; // 1 marker + 8 data bytes
        if (value < 253)
            return 1; // 1 data byte
        if (value <= 0xFFFFL)
            return 3; // 1 marker + 2 data bytes
        if (value <= 0xFFFFFFFFL)
            return 5; // 1 marker + 4 data bytes
        return 9; // 1 marker + 8 data bytes
    }

    /**
     * Encodes the value into its minimal representation.
     *
     * @return the minimal encoded bytes of the value
     */
    public byte[] encode() {
        byte[] bytes;
        switch (sizeOf(value)) {
            case 1:
                return new byte[] { (byte) value };
            case 3:
                return new byte[] { (byte) 253, (byte) (value), (byte) (value >> 8) };
            case 5:
                bytes = new byte[5];
                bytes[0] = (byte) 254;
                CryptoUtils.uint32ToByteArrayLE(value, bytes, 1);
                return bytes;
            default:
                bytes = new byte[9];
                bytes[0] = (byte) 255;
                CryptoUtils.uint64ToByteArrayLE(value, bytes, 1);
                return bytes;
        }
    }
}