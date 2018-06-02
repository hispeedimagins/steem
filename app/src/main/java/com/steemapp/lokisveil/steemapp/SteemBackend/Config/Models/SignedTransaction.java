package com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models;

import android.util.Log;

import com.steemapp.lokisveil.steemapp.Crypto.Base59.Sha256ChecksumProvider;
import com.steemapp.lokisveil.steemapp.Crypto.CryptoUtils;
import com.steemapp.lokisveil.steemapp.Crypto.DumpedPrivateKey;
import com.steemapp.lokisveil.steemapp.Crypto.ECKey;
import com.steemapp.lokisveil.steemapp.Crypto.Sha256Hash;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.PrivateKeyType;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.ValidationType;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Interfaces.ByteTransformable;

import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Interfaces.SignatureObject;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Operations.Operation;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.SteemJConfig;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Utilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.joou.UInteger;
import org.joou.UShort;

import org.spongycastle.util.encoders.Base64;




/**
 * This class represents a Steem "signed_transaction" object.
 * 
 * @author <a href="http://Steemit.com/@dez1337">dez1337</a>
 */

public class SignedTransaction extends Transaction implements ByteTransformable, Serializable {
    private static final long serialVersionUID = 4821422578657270330L;
    //private static final Logger LOGGER = LoggerFactory.getLogger(SignedTransaction.class);

    protected transient List<String> signatures;

    /**
     * This constructor is only used to create the POJO from a JSON response.
     */
    /*@JsonCreator
    private SignedTransaction(@JsonProperty("ref_block_num") UShort refBlockNum,
            @JsonProperty("ref_block_prefix") UInteger refBlockPrefix,
            @JsonProperty("expiration") TimePointSec expirationDate,
            @JsonProperty("operations") List<Operation> operations,
            @JsonProperty("extensions") List<FutureExtensions> extensions,
            @JsonProperty("signatures") List<String> signatures) {
        super(refBlockNum, refBlockPrefix, expirationDate, operations, extensions);
        this.signatures = signatures;
    }*/

    /**
     * Create a new signed transaction object.
     * 
     * @param refBlockNum
     *            The reference block number (see
     *            {@link #setRefBlockNum(UShort)}).
     * @param refBlockPrefix
     *            The reference block index (see
     *            {@link #setRefBlockPrefix(UInteger)}).
     * @param expirationDate
     *            Define until when the transaction has to be processed (see
     *            {@link #setExpirationDate(TimePointSec)}).
     * @param operations
     *            A list of operations to process within this Transaction (see
     *            {@link #setOperations(List)}).
     * @param extensions
     *            Extensions are currently not supported and will be ignored
     *            (see {@link #setExtensions(List)}).
     */
    public SignedTransaction(UShort refBlockNum, UInteger refBlockPrefix, TimePointSec expirationDate,
            List<Operation> operations, List<FutureExtensions> extensions) {
        super(refBlockNum, refBlockPrefix, expirationDate, operations, extensions);
        this.signatures = new ArrayList<>();
    }

    /**
     * Like
     * {@link #SignedTransaction(UShort, UInteger, TimePointSec, List, List)},
     * but allows you to provide a
     * {link eu.bittrade.libs.steemj.base.models.BlockId} object as the
     * reference block and will also set the <code>expirationDate</code> to the
     * latest possible time.
     * 
     * @param blockId
     *            The block reference (see {@link #setRefBlockNum(UShort)} and
     *            {@link #setRefBlockPrefix(UInteger)}).
     * @param operations
     *            A list of operations to process within this Transaction (see
     *            {@link #setOperations(List)}).
     * @param extensions
     *            Extensions are currently not supported and will be ignored
     *            (see {@link #setExtensions(List)}).
     */
    public SignedTransaction(BlockId blockId, List<Operation> operations, List<FutureExtensions> extensions) {
        super(blockId, operations, extensions);
        this.signatures = new ArrayList<>();
    }

    /**
     * Get the signatures for this transaction.
     * 
     * @return An array of currently appended signatures.
     */
    //@JsonProperty("signatures")
    public List<String> getSignatures() {
        return this.signatures;
    }

    /**
     * Verify that the signature is canonical.
     * 
     * Original implementation can be found <a href=
     * "https://github.com/kenCode-de/graphenej/blob/master/graphenej/src/main/java/de/bitsharesmunich/graphenej/Transaction.java"
     * >here</a>.
     * 
     * @param signature
     *            A single signature in its byte representation.
     * @return True if the signature is canonical or false if not.
     */
    private boolean isCanonical(byte[] signature) {
        return ((signature[0] & 0x80) != 0) || (signature[0] == 0) || ((signature[1] & 0x80) != 0)
                || ((signature[32] & 0x80) != 0) || (signature[32] == 0) || ((signature[33] & 0x80) != 0);
    }

    /**
     *
     * Like {link sign(String) sign(String)}, but uses the
     * {link SteemJConfig#getChainId() default chain id}.
     *
     * throws SteemInvalidTransactionException
     *             If the transaction can not be signed.
     */
    public void sign(ImmutablePair<PrivateKeyType, String> keys) {
        sign(SteemJConfig.getInstance().getChainId(),keys);
    }

    /**
     * Use this method if you want to specify a different chainId than the
     * {@link SteemJConfig#getChainId() default one}. Otherwise use the
     * {link #sign() sign()} method.
     * 
     * @param chainId
     *            The chain id that should be used during signing.
     * throws SteemInvalidTransactionException
     *             If the transaction can not be signed.
     */
    public void sign(String chainId,ImmutablePair<PrivateKeyType, String> keys) {
        /*if (!SteemJConfig.getInstance().getValidationLevel().equals(ValidationType.SKIP_VALIDATION)) {
            this.validate();
        }*///getRequiredSignatureKeys()
        //ImmutablePair<PrivateKeyType, ECKey> adk = convertWifToECKeyPair(keys);
        //ECKey kk = ECKey.fromPrivate(Arrays.copyOf(bytes, 32), isPubKeyCompressed());
        ECKey k = DumpedPrivateKey.fromBase58(null, keys.getRight(), new Sha256ChecksumProvider()).getKey();
        List<ECKey> requiredSignatures = new ArrayList<>();
        requiredSignatures.add(k);
        for (ECKey requiredPrivateKey : requiredSignatures ) {
            boolean isCanonical = false;

            Sha256Hash messageAsHash;
            while (!isCanonical) {
                try {
                    messageAsHash = Sha256Hash.of(this.toByteArray(chainId));
                } catch (RuntimeException e) {
                    throw new RuntimeException(
                            "The required encoding is not supported by your platform.", e);
                }

                String signature = requiredPrivateKey.signMessage(messageAsHash);
                byte[] signatureAsByteArray = Base64.decode(signature);

                if (isCanonical(signatureAsByteArray)) {
                    this.getExpirationDate().setDateTime(this.getExpirationDate().getDateTimeAsTimestamp() + 1000);
                } else {
                    isCanonical = true;
                    this.signatures.add(CryptoUtils.HEX.encode(signatureAsByteArray));
                }
            }
        }
    }



    public void signMy(String chainId,ImmutablePair<PrivateKeyType, String> keys) {
        /*if (!SteemJConfig.getInstance().getValidationLevel().equals(ValidationType.SKIP_VALIDATION)) {
            this.validate();
        }*///getRequiredSignatureKeys()
        //ImmutablePair<PrivateKeyType, ECKey> adk = convertWifToECKeyPair(keys);
        //ECKey kk = ECKey.fromPrivate(Arrays.copyOf(bytes, 32), isPubKeyCompressed());
        ECKey k = DumpedPrivateKey.fromBase58(null, keys.getRight(), new Sha256ChecksumProvider()).getKey();

        boolean isCanonical = false;

        Sha256Hash messageAsHash;
        while (!isCanonical) {
            try {
                messageAsHash = Sha256Hash.of(this.toByteArray(chainId));
            } catch (RuntimeException e) {
                throw new RuntimeException(
                        "The required encoding is not supported by your platform.", e);
            }

            String signature = k.signMessage(messageAsHash);
            byte[] signatureAsByteArray = Base64.decode(signature);

            if (isCanonical(signatureAsByteArray)) {
                this.getExpirationDate().setDateTime(this.getExpirationDate().getDateTimeAsTimestamp() + 1000);
            } else {
                isCanonical = true;
                this.signatures.add(CryptoUtils.HEX.encode(signatureAsByteArray));
            }
        }
    }


    private ImmutablePair<PrivateKeyType, ECKey> convertWifToECKeyPair(ImmutablePair<PrivateKeyType, String> wifPrivateKey) {

        /*PrivateKeyType l = wifPrivateKey.getLeft();
        String r = wifPrivateKey.getRight();
        ImmutablePair p = new ImmutablePair<>(l, DumpedPrivateKey.fromBase58(null, r, new Sha256ChecksumProvider()));
        ECKey k = DumpedPrivateKey.fromBase58(null, wifPrivateKey.getRight(), new Sha256ChecksumProvider()).getKey();*/

        //PrivateKeyType sd = (PrivateKeyType) p.getKey();
        return new ImmutablePair<>(wifPrivateKey.getLeft(),
                DumpedPrivateKey.fromBase58(null, wifPrivateKey.getRight(), new Sha256ChecksumProvider()).getKey());
    }

    /**
     * @return The list of private keys required to sign this transaction.
     * throws SteemInvalidTransactionException
     *             If the required private key is not present in the
     *             {link eu.bittrade.libs.steemj.configuration.PrivateKeyStorage}.
     */
    //@JsonIgnore
    protected List<ECKey> getRequiredSignatureKeys() {
        List<ECKey> requiredSignatures = new ArrayList<>();
        Map<SignatureObject, PrivateKeyType> requiredAuthorities = getRequiredAuthorities();

        for (Entry<SignatureObject, PrivateKeyType> requiredAuthority : requiredAuthorities.entrySet()) {
            if (requiredAuthority.getKey() instanceof AccountName) {
                requiredSignatures = getRequiredSignatureKeyForAccount(requiredSignatures,
                        (AccountName) requiredAuthority.getKey(), requiredAuthority.getValue());
            }
            /*else if (requiredAuthority.getKey() instanceof Authority) {
                // TODO: Support authorities.
            } */
            else {
                Log.w("getRequiredSignatureKe","Unknown SigningObject type {} "+requiredAuthority.getKey());
            }
        }

        return requiredSignatures;
    }

    /**
     * Fetch the requested private key for the given <code>accountName</code>
     * from the {link eu.bittrade.libs.steemj.configuration.PrivateKeyStorage}
     * and merge it into the <code>requiredSignatures</code> list.
     * 
     * @param requiredSignatures
     *            A list of already fetched keys. This list is used to make sure
     *            that a key is not added twice.
     * @param accountName
     *            The account name to fetch the key for.
     * @param privateKeyType
     *            The key type to fetch.
     * @return The <code>requiredSignatures</code> including the
     *         <code>privateKeyType</code> for <code>accountName</code>.
     * throws SteemInvalidTransactionException
     *             If the required private key is not present in the
     *             {link eu.bittrade.libs.steemj.configuration.PrivateKeyStorage}.
     */
    private List<ECKey> getRequiredSignatureKeyForAccount(List<ECKey> requiredSignatures, AccountName accountName,
            PrivateKeyType privateKeyType)  {
        ECKey privateKey;

        try {
            PrivateKeyStorage k = SteemJConfig.getInstance().getPrivateKeyStorage();

            privateKey = SteemJConfig.getInstance().getPrivateKeyStorage().getKeyForAccount(privateKeyType, accountName);
        } catch (InvalidParameterException ipe) {
            throw new RuntimeException(
                    "Could not find private " + privateKeyType + " key for the user " + accountName.getName() + ".");
        }

        if (!requiredSignatures.contains(privateKey)) {
            requiredSignatures.add(privateKey);
        }

        return requiredSignatures;
    }

    /**
     * This method creates a byte array based on a transaction object under the
     * use of a guide written by <a href="https://Steemit.com/Steem/@xeroc/">
     * Xeroc</a>. This method should only be used internally.
     * 
     * If a chainId is provided it will be added in front of the byte array.
     * 
     * @return The serialized transaction object.
     * throws SteemInvalidTransactionException
     *             If the transaction can not be signed.
     */
    @Override
    public byte[] toByteArray()  {
        return toByteArray(SteemJConfig.getInstance().getChainId());
    }

    /**
     * Like {@link #toByteArray() toByteArray()}, but allows to define a Steem
     * chain id.
     * 
     * @param chainId
     *            The HEX representation of the chain Id you want to use for
     *            this transaction.
     * @return The serialized transaction object.
     * throws SteemInvalidTransactionException
     *             If the transaction can not be signed.
     */
    protected byte[] toByteArray(String chainId) {
        try (ByteArrayOutputStream serializedTransaction = new ByteArrayOutputStream()) {
            if (chainId != null && !chainId.isEmpty()) {
                serializedTransaction.write(CryptoUtils.HEX.decode(chainId));
            }
            serializedTransaction.write(Utilities.transformShortToByteArray(this.getRefBlockNum().shortValue()));
            serializedTransaction.write(Utilities.transformIntToByteArray(this.getRefBlockPrefix().intValue()));
            serializedTransaction.write(this.getExpirationDate().toByteArray());

            serializedTransaction.write(Utilities.transformLongToVarIntByteArray(this.getOperations().size()));
            int opsi = this.getOperations().size();
            for (Operation operation : this.getOperations()) {
                /*
                 * Validate all Operations
                 * 
                 * TODO: Add a validation method to the Transaction Object?
                 */
                operation.validate(SteemJConfig.getInstance().getValidationLevel());
                serializedTransaction.write(operation.toByteArray());
            }

            serializedTransaction.write(Utilities.transformIntToVarIntByteArray(this.getExtensions().size()));
            for (FutureExtensions futureExtensions : this.getExtensions()) {
                serializedTransaction.write(futureExtensions.toByteArray());
            }

            return serializedTransaction.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(
                    "A problem occured while transforming the transaction into a byte array.", e);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
