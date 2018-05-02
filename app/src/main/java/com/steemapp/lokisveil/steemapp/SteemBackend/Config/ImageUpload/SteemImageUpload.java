package com.steemapp.lokisveil.steemapp.SteemBackend.Config.ImageUpload;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.android.volley.toolbox.HttpResponse;
import com.google.common.net.HttpHeaders;
import com.google.gson.Gson;
import com.steemapp.lokisveil.steemapp.Crypto.Base59.Sha256ChecksumProvider;
import com.steemapp.lokisveil.steemapp.Crypto.CryptoUtils;
import com.steemapp.lokisveil.steemapp.Crypto.DumpedPrivateKey;
import com.steemapp.lokisveil.steemapp.Crypto.ECKey;
import com.steemapp.lokisveil.steemapp.Crypto.Sha256Hash;
import com.steemapp.lokisveil.steemapp.SteemBackend.Config.Models.AccountName;
import com.steemapp.lokisveil.steemapp.jsonclasses.OperationJson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

public class SteemImageUpload {
    //private static final Logger LOGGER = LoggerFactory.getLogger(SteemJImageUpload.class);


    //private static SteemJImageUploadConfig steemJImageUploadConfig;
    private static int maxBufferSize = 1 * 1024 * 1024;
    /** The endpoint to send the upload request to. */
    private static String steemitImagesEndpoint = "https://steemitimages.com";
    /** The image signing challenge added to the hash. */
    private static String imageSigningChallenge = "ImageSigningChallenge";
    /** The timeout in milliseconds to establish a connection. */
    private static int connectTimeout = 0;
    /**
     * The timeout in milliseconds to read data from an established connection.
     */
    private static int readTimeout = 0;

    /**
     * This class should not be instantiated as all methods are private.
     */
    private SteemImageUpload() {
    }

    /**
     * Upload the given <code>fileToUpload</code> to the
     * {link SteemJImageUploadConfig#getSteemitImagesEndpoint()
     * SteemitImagesEndpoint}.
     *
     * In addition to the file, the method also requires a valid Steem
     * <code>accountName</code> and the <code>privatePostingKey</code> of this
     * account, as uploading is only available for Steem users.
     *
     * <p>
     * Example: <code>
     * uploadImage(new AccountName("dez1337"), 5KMamixsFoUkdlz7sNG4RsyaKQyJMBBqrdT6y54qr4cdVhU9rz7, new File("C:\Path\To\Image.png"))
     * </code>
     * </p>
     *
     * @param accountName
     *            The Steem account used to sign the upload.
     * @param privatePostingKey
     *            The private posting key of the <code>accountName</code>.
     * @param fileToUpload
     *            The image to upload.
     * @return A URL object that contains the download URL of the image.
     * @throws IOException
     *             In case the given <code>fileToUpload</code> could not be
     *             transformed into its byte representation.
     * throws HttpResponseException
     *             In case the
     *             {link SteemJImageUploadConfig#getSteemitImagesEndpoint()
     *             SteemitImagesEndpoint} returned an error.*/

    public static String uploadImage(AccountName accountName, String privatePostingKey, File fileToUpload,String path)
            throws IOException {
        // Transform the WIF private Key into a real ECKey object.
        ECKey privatePostingKeyAsKey = DumpedPrivateKey
                .fromBase58(null, privatePostingKey, new Sha256ChecksumProvider()).getKey();

        byte[] inputData = new byte[(int) fileToUpload.length()];
        // Generate the byte representation for the given file.
        /*int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        *//*DataOutputStream outputStream = null;
        InputStream inputStream = null;*//*
        File file = new File(path);
        //Bitmap image = BitmapFactory.decodeFile(path);

        FileInputStream fileInputStream = new FileInputStream(file);
        bytesAvailable = fileInputStream.available();
        bufferSize = Math.min(bytesAvailable, maxBufferSize);
        buffer = new byte[bufferSize];

        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        while (bytesRead > 0) {
            //outputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }*/
        try (FileInputStream fileInputStreams = new FileInputStream(fileToUpload.getAbsoluteFile());
             DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(fileInputStreams))) {

            dataInputStream.readFully(inputData);
        } catch (IOException e) {
            throw new IOException("Could not transform the given file into its byte representation.", e);
        }

        Sha256Hash imageHash = null;

        // Create a hash for the byte representation and the image signing
        // challenge.
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            outputStream.write(imageSigningChallenge.getBytes(StandardCharsets.UTF_8));
            //outputStream.write(accountName.getName().getBytes(StandardCharsets.UTF_8));
            outputStream.write(inputData);

            //outputStream.write(buffer);
            byte[] opba = outputStream.toByteArray();
            imageHash = Sha256Hash.of(opba);
        } catch (IOException e) {
            throw new IOException("Could not transform the given file into its byte representation.", e);
        }

        String signature = privatePostingKeyAsKey.signMessage(imageHash);
        /*String url = steemitImagesEndpoint + "/"
                + accountName.getName() + "/" + CryptoUtils.HEX.encode(signature.getBytes());*/
        String url = steemitImagesEndpoint + "/"
                + accountName.getName() + "/" + CryptoUtils.HEX.encode(Base64.decode(signature,android.util.Base64.DEFAULT));
        String i = URLConnection.guessContentTypeFromName(fileToUpload.getName());

        String result = multipartRequest(fileToUpload,url, null, path, "image", i);
//next parse result string
        return result;
        //return executeMultipartRequest(accountName, CryptoUtils.HEX.encode(Base64.decode(signature,android.util.Base64.DEFAULT)), fileToUpload);
    }

    /**
     * This method handles the final upload to the
     * {link SteemJImageUploadConfig#getSteemitImagesEndpoint()
     * SteemitImagesEndpoint}.
     *
     * param accountName
     *            The Steem account used to sign the upload.
     * param signature
     *            The signature for this upload.
     * param fileToUpload
     *            The image to upload.
     * @return A URL object that contains the download URL of the image.
     * throws HttpResponseException
     *             In case the
     *             {link SteemJImageUploadConfig#getSteemitImagesEndpoint()
     *             SteemitImagesEndpoint} returned an error.
     *//*
    private static URL executeMultipartRequest(AccountName accountName, String signature, File fileToUpload)
            throws IOException {
        NetHttpTransport.Builder builder = new NetHttpTransport.Builder();

        MultipartContent content = new MultipartContent().setMediaType(new HttpMediaType("multipart/form-data")
                .setParameter("boundary", "----WebKitFormBoundaryaAsqCuJ0UrJUS0dz"));

        FileContent fileContent = new FileContent(URLConnection.guessContentTypeFromName(fileToUpload.getName()),
                fileToUpload);

        MultipartContent.Part part = new MultipartContent.Part(fileContent);

        part.setHeaders(new HttpHeaders().set("Content-Disposition",
                String.format("form-data; name=\"image\"; filename=\"%s\"", fileToUpload.getName())));

        content.addPart(part);

        HttpRequest httpRequest = builder.build().createRequestFactory(new HttpClientRequestInitializer())
                .buildPostRequest(new GenericUrl(SteemJImageUploadConfig.getInstance().getSteemitImagesEndpoint() + "/"
                        + accountName.getName() + "/" + signature), content);

        LOGGER.debug("{} {}", httpRequest.getRequestMethod(), httpRequest.getUrl().toURI());

        HttpResponse httpResponse = httpRequest.execute();

        LOGGER.debug("{} {} {} ({})", httpResponse.getRequest().getRequestMethod(),
                httpResponse.getRequest().getUrl().toURI(), httpResponse.getStatusCode(),
                httpResponse.getStatusMessage());

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode response = objectMapper.readTree(httpResponse.parseAsString());

        return new URL(response.get("url").asText());
    }*/


    public static String multipartRequest(File file,String urlTo, Map<String, String> parmas, String filepath, String filefield, String fileMimeType) {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        //String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String boundary = "----WebKitFormBoundaryaAsqCuJ0UrJUS0dz";
        String lineEnd = "\r\n";

        String result = "";




        String[] q = filepath.split("/");
        int idx = q.length - 1;

        try {
            //File file = new File(filepath);
            String filename = file.getName();
            FileInputStream fileInputStream = new FileInputStream(file.getAbsoluteFile());

            URL url = new URL(urlTo);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            //outputStream.writeBytes("Content-Disposition: form-data; name=\"" + filefield + "\"; filename=\"" + q[idx] + "\"" + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + filefield + "\"; filename=\"" + filename + "\"" + lineEnd);
            outputStream.writeBytes("Content-Type: " + fileMimeType + lineEnd);
            outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);

            outputStream.writeBytes(lineEnd);
            /*int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;*/
            /*int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            bytesAvailable = fileInputStream.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {

                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }*/

            byte[] inputData = new byte[(int) file.length()];
            try (FileInputStream fileInputStreams = new FileInputStream(file.getAbsoluteFile());
                 DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(fileInputStreams))) {

                dataInputStream.readFully(inputData);
                outputStream.write(inputData);
            } catch (IOException e) {
                throw new IOException("Could not transform the given file into its byte representation.", e);
            }

            /*try (ByteArrayOutputStream outputStreamm = new ByteArrayOutputStream()) {

                outputStreamm.write(inputData);

            } catch (IOException e) {
                throw new IOException("Could not transform the given file into its byte representation.", e);
            }*/

            outputStream.writeBytes(lineEnd);

            // Upload POST Data
            if(parmas != null){
                Iterator<String> keys = parmas.keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next();
                    String value = parmas.get(key);

                    outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                    outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                    outputStream.writeBytes(lineEnd);
                    outputStream.writeBytes(value);
                    outputStream.writeBytes(lineEnd);
                }
            }


            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);


            if (200 != connection.getResponseCode()) {
                String not = "";
                not = "Failed to upload code:" + connection.getResponseCode() + " " + connection.getResponseMessage();
                String nots = "Failed to upload code:" + connection.getResponseCode() + " " + convertStreamToString(connection.getErrorStream());
                not = "";
                int i = 0;
                i++;


                //throw new CustomException("Failed to upload code:" + connection.getResponseCode() + " " + connection.getResponseMessage());
            }

            inputStream = connection.getInputStream();

            result = convertStreamToString(inputStream);

            fileInputStream.close();
            inputStream.close();
            outputStream.flush();
            outputStream.close();
            Gson gson = new Gson();
            OperationJson.urlfromsteemitimages urls = gson.fromJson(result,OperationJson.urlfromsteemitimages.class);


            return urls.getUrl();
        } catch (Exception e) {
            Log.e("multipartexception",e.toString());
            //throw new CustomException(e);
        }
        return result;
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}