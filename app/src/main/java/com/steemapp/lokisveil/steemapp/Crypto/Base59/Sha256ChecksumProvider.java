package com.steemapp.lokisveil.steemapp.Crypto.Base59;


import com.steemapp.lokisveil.steemapp.Crypto.Sha256Hash;

/**
 * Calculate the actual checksum of a <code>Base58</code> value for blockchains
 * based on <code>SHA256</code> (e.g. Bitcoin).
 * 
 * @author <a href="http://steemit.com/@dez1337">dez1337</a>
 */
public class Sha256ChecksumProvider implements Base58ChecksumProvider {
    @Override
    public byte[] calculateActualChecksum(byte[] data) {
        return Sha256Hash.hashTwice(data);
    }
}
