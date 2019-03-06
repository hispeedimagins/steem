//package com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.API;
//
//import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.RequestMethods;
//import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Enums.SteemApiType;
//import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.SignedTransaction;
//
///**
// * Created by boot on 2/28/2018.
// */
//
//public class NetworkBroadcastApi {
//    /** Add a private constructor to hide the implicit public one. */
//    private NetworkBroadcastApi() {
//    }
//
//    /**
//     * Broadcast a transaction on the Steem blockchain. This method will
//     * validate the transaction and return immediately. Please notice that this
//     * does not mean that the operation has been accepted and has been
//     * processed. If you want to make sure that this is the case use the
//     * {@link #broadcastTransactionSynchronous(CommunicationHandler, SignedTransaction)}
//     * method.
//     *
//     * @param communicationHandler
//     *            A
//     *            {@link eu.bittrade.libs.steemj.communication.CommunicationHandler
//     *            CommunicationHandler} instance that should be used to send the
//     *            request.
//     * @param transaction
//     *            The {@link SignedTransaction} object to broadcast.
//     * @throws SteemCommunicationException
//     *             <ul>
//     *             <li>If the server was not able to answer the request in the
//     *             given time (see
//     *             {@link eu.bittrade.libs.steemj.configuration.SteemJConfig#setResponseTimeout(int)
//     *             setResponseTimeout}).</li>
//     *             <li>If there is a connection problem.</li>
//     *             </ul>
//     * @throws SteemResponseException
//     *             <ul>
//     *             <li>If the SteemJ is unable to transform the JSON response
//     *             into a Java object.</li>
//     *             <li>If the Server returned an error object.</li>
//     *             </ul>
//     * @throws SteemInvalidTransactionException
//     *             In case the provided transaction is not valid.
//     */
//    public static void broadcastTransaction(CommunicationHandler communicationHandler, SignedTransaction transaction)
//            throws SteemCommunicationException, SteemResponseException, SteemInvalidTransactionException {
//        JsonRPCRequest requestObject = new JsonRPCRequest();
//        requestObject.setApiMethod(RequestMethods.BROADCAST_TRANSACTION);
//        requestObject.setSteemApi(SteemApiType.NETWORK_BROADCAST_API);
//
//        if (transaction.getSignatures() == null || transaction.getSignatures().isEmpty()) {
//            transaction.sign();
//        }
//
//        Object[] parameters = { transaction };
//        requestObject.setAdditionalParameters(parameters);/**/
//
//        communicationHandler.performRequest(requestObject, Object.class);
//    }
//
//    /**
//     * Broadcast a transaction on the Steem blockchain. This method will
//     * validate the transaction and return after it has been accepted and
//     * applied.
//     *
//     * @param communicationHandler
//     *            A
//     *            {@link eu.bittrade.libs.steemj.communication.CommunicationHandler
//     *            CommunicationHandler} instance that should be used to send the
//     *            request.
//     * @param transaction
//     *            The {@link SignedTransaction} object to broadcast.
//     * @return A {@link BroadcastTransactionSynchronousReturn} object providing
//     *         information about the block in which the transaction has been
//     *         applied.
//     * @throws SteemCommunicationException
//     *             <ul>
//     *             <li>If the server was not able to answer the request in the
//     *             given time (see
//     *             {@link eu.bittrade.libs.steemj.configuration.SteemJConfig#setResponseTimeout(int)
//     *             setResponseTimeout}).</li>
//     *             <li>If there is a connection problem.</li>
//     *             </ul>
//     * @throws SteemResponseException
//     *             <ul>
//     *             <li>If the SteemJ is unable to transform the JSON response
//     *             into a Java object.</li>
//     *             <li>If the Server returned an error object.</li>
//     *             </ul>
//     * @throws SteemInvalidTransactionException
//     *             In case the provided transaction is not valid.
//     */
//    public static BroadcastTransactionSynchronousReturn broadcastTransactionSynchronous(
//            CommunicationHandler communicationHandler, SignedTransaction transaction)
//            throws SteemCommunicationException, SteemResponseException, SteemInvalidTransactionException {
//        JsonRPCRequest requestObject = new JsonRPCRequest();
//        requestObject.setApiMethod(RequestMethods.BROADCAST_TRANSACTION_SYNCHRONOUS);
//        requestObject.setSteemApi(SteemApiType.NETWORK_BROADCAST_API);
//
//        if (transaction.getSignatures() == null || transaction.getSignatures().isEmpty()) {
//            transaction.sign();
//        }
//
//        Object[] parameters = { transaction };
//        requestObject.setAdditionalParameters(parameters);
//
//        return communicationHandler.performRequest(requestObject, BroadcastTransactionSynchronousReturn.class).get(0);
//    }
//
//    /**
//     * Broadcast a whole block.
//     *
//     * @param communicationHandler
//     *            A
//     *            {@link eu.bittrade.libs.steemj.communication.CommunicationHandler
//     *            CommunicationHandler} instance that should be used to send the
//     *            request.
//     * @param signedBlock
//     *            The {@link SignedBlock} object to broadcast.
//     * @throws SteemCommunicationException
//     *             <ul>
//     *             <li>If the server was not able to answer the request in the
//     *             given time (see
//     *             {@link eu.bittrade.libs.steemj.configuration.SteemJConfig#setResponseTimeout(int)
//     *             setResponseTimeout}).</li>
//     *             <li>If there is a connection problem.</li>
//     *             </ul>
//     * @throws SteemResponseException
//     *             <ul>
//     *             <li>If the SteemJ is unable to transform the JSON response
//     *             into a Java object.</li>
//     *             <li>If the Server returned an error object.</li>
//     *             </ul>
//     */
//    public static void broadcastBlock(CommunicationHandler communicationHandler, SignedBlock signedBlock)
//            throws SteemCommunicationException, SteemResponseException {
//        JsonRPCRequest requestObject = new JsonRPCRequest();
//        requestObject.setApiMethod(RequestMethods.BROADCAST_BLOCK);
//        requestObject.setSteemApi(SteemApiType.NETWORK_BROADCAST_API);
//
//        Object[] parameters = { signedBlock };
//        requestObject.setAdditionalParameters(parameters);
//
//        communicationHandler.performRequest(requestObject, Object.class);
//    }
//}
