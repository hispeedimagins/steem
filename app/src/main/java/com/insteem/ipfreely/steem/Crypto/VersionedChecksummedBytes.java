package com.insteem.ipfreely.steem.Crypto;

import com.google.common.base.Objects;
import com.google.common.primitives.Ints;
import com.google.common.primitives.UnsignedBytes;
import com.insteem.ipfreely.steem.Crypto.Base59.Base58;
import com.insteem.ipfreely.steem.Crypto.Base59.Base58ChecksumProvider;

import java.io.Serializable;
import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by boot on 2/27/2018.
 */

public class VersionedChecksummedBytes  implements Serializable, Cloneable, Comparable<VersionedChecksummedBytes>  {
    /** Generated serial version id. */
    //private static final long serialVersionUID = 7964431971365434633L;
    protected final int version;
    protected byte[] bytes;

    protected VersionedChecksummedBytes(String encoded, Base58ChecksumProvider checksumProvider) {
        byte[] versionAndDataBytes = Base58.decodeChecked(encoded, checksumProvider);
        byte versionByte = versionAndDataBytes[0];
        version = versionByte & 0xFF;
        bytes = new byte[versionAndDataBytes.length - 1];
        System.arraycopy(versionAndDataBytes, 1, bytes, 0, versionAndDataBytes.length - 1);
    }

    protected VersionedChecksummedBytes(int version, byte[] bytes) {
        checkArgument(version >= 0 && version < 256);
        this.version = version;
        this.bytes = bytes;
    }

    /**
     * @return the base-58 encoded String representation of this object,
     *         including version and checksum bytes
     */
    public final String toBase58() {
        // A stringified buffer is:
        // 1 byte version + data bytes + 4 bytes check code (a truncated hash)
        byte[] addressBytes = new byte[1 + bytes.length + 4];
        addressBytes[0] = (byte) version;
        System.arraycopy(bytes, 0, addressBytes, 1, bytes.length);
        byte[] checksum = Sha256Hash.hashTwice(addressBytes, 0, bytes.length + 1);
        System.arraycopy(checksum, 0, addressBytes, bytes.length + 1, 4);
        return Base58.encode(addressBytes);
    }

    @Override
    public String toString() {
        return toBase58();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(version, Arrays.hashCode(bytes));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        VersionedChecksummedBytes other = (VersionedChecksummedBytes) o;
        return this.version == other.version && Arrays.equals(this.bytes, other.bytes);
    }

    /**
     * {@inheritDoc}
     *
     * This implementation narrows the return type to
     * <code>VersionedChecksummedBytes</code> and allows subclasses to throw
     * <code>CloneNotSupportedException</code> even though it is never thrown by
     * this implementation.
     */
    @Override
    public VersionedChecksummedBytes clone() throws CloneNotSupportedException {
        return (VersionedChecksummedBytes) super.clone();
    }

    /**
     * {@inheritDoc}
     *
     * This implementation uses an optimized Google Guava method to compare
     * <code>bytes</code>.
     */
    @Override
    public int compareTo(VersionedChecksummedBytes o) {
        int result = Ints.compare(this.version, o.version);
        return result != 0 ? result : UnsignedBytes.lexicographicalComparator().compare(this.bytes, o.bytes);
    }

    /**
     * Returns the "version" or "header" byte: the first byte of the data. This
     * is used to disambiguate what the contents apply to, for example, which
     * network the key or address is valid on.
     *
     * @return A positive number between 0 and 255.
     */
    public int getVersion() {
        return version;
    }
}
