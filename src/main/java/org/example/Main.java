package org.example;

import java.net.URI;
import java.nio.file.Paths;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class Main {
  public static void main(String[] args) {

    var endpoint = "https://sample.example.org";
    var accessKey = "key";
    var secretKey = "key";
    var bucketName = "bucketname";
    var fileName = "file.txt";
    var filePath = "C:\\Sample\\Path\\" + fileName;

    try (var client =
        S3AsyncClient.builder()
            // If using untrusted SSL certificate
            //            .httpClient(
            //                NettyNioAsyncHttpClient.builder()
            //                    .tlsTrustManagersProvider(
            //                        () ->
            //                            new TrustManager[] {
            //                              new X509TrustManager() {
            //                                @Override
            //                                public void checkClientTrusted(
            //                                    X509Certificate[] chain, String authType) {
            //                                  // Do nothing
            //                                }
            //
            //                                @Override
            //                                public void checkServerTrusted(
            //                                    X509Certificate[] chain, String authType) {
            //                                  // Do nothing
            //                                }
            //
            //                                @Override
            //                                public X509Certificate[] getAcceptedIssuers() {
            //                                  return new X509Certificate[0];
            //                                }
            //                              }
            //                            })
            //                    .build())
            .endpointOverride(URI.create(endpoint))
            .region(Region.US_EAST_1)
            .forcePathStyle(true)
            // problematic part
            .multipartEnabled(true) // without this option the upload works fine
            //
            .credentialsProvider(
                StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
            .build()) {

      var putRequest = PutObjectRequest.builder().bucket(bucketName).key(fileName).build();
      var resp = client.putObject(putRequest, AsyncRequestBody.fromFile(Paths.get(filePath)));
      resp.join();
    }
  }
}
