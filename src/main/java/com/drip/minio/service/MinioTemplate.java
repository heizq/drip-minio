package com.drip.minio.service;

import com.drip.minio.util.FileFormatUtil;
import com.drip.minio.util.PropertyConfigUtil;
import io.minio.MinioClient;
import io.minio.errors.*;
import org.xmlpull.v1.XmlPullParserException;

import javax.activation.MimeType;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by lenovo on 2018/6/4.
 */
public class MinioTemplate {

    private String endpoint;

    private String accessKey;

    private String secretKey;

    private String bucket;

    private MinioTemplate() {
    }

    private MinioTemplate(String endpoint, String accessKey, String secretKey) {
        this.endpoint = endpoint;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    private MinioTemplate(String endpoint, String accessKey, String secretKey, String bucket) {
        this.endpoint = endpoint;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.bucket = bucket;
    }

    private static class SingletonHolder {
        private static MinioTemplate instance = new MinioTemplate(PropertyConfigUtil.getPropertyValue("minio.endpoint"),
                PropertyConfigUtil.getPropertyValue("minio.accessKey"),
                PropertyConfigUtil.getPropertyValue("minio.secretKey"),
                PropertyConfigUtil.getPropertyValue("minio.bucket")) ;
    }


    public static final MinioTemplate getInstance(){
        return SingletonHolder.instance;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    /**
     * Gets a Minio client
     *
     * @return an authenticated Amazon S3 client
     */
    public MinioClient getMinioClient() throws InvalidPortException, InvalidEndpointException {
        return new MinioClient(getInstance().getEndpoint(), getInstance().getAccessKey(), getInstance().getSecretKey());
    }


    public void createBucket(String bucketName) throws XmlPullParserException, NoSuchAlgorithmException, InvalidKeyException, IOException, InvalidPortException, InvalidEndpointException, RegionConflictException, NoResponseException, InternalException, ErrorResponseException, InsufficientDataException, InvalidBucketNameException, BucketPolicyTooLargeException, InvalidObjectPrefixException, InvalidArgumentException {
        MinioClient client = getMinioClient();
        if(!client.bucketExists(bucketName)){
            client.makeBucket(bucketName);
        }
    }


    public  String uploadFile(InputStream inputStream, String bucketName,String fileName) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InvalidArgumentException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException, InvalidPortException, InvalidEndpointException {
        MinioClient client = getMinioClient();
        // Create object 'my-objectname' in 'my-bucketname' with content from the input stream.
        String fileRelativePath = FileFormatUtil.getFileNamePrefixDir() + "/" + fileName;
        client.putObject(bucketName, fileRelativePath, inputStream, "application/octet-stream");
        return fileRelativePath;
    }

    public void deleteFile(String bucketName,String fileName) throws InvalidPortException, InvalidEndpointException, IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InvalidArgumentException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException {
        MinioClient client = getMinioClient();
        String name = FileFormatUtil.getFileRelativePathForDelete(bucketName,fileName);
        client.removeObject(bucketName,name);
    }

    public InputStream getFile(String bucketName,String fileName) throws InvalidPortException, InvalidEndpointException, IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException, InvalidArgumentException, InternalException, NoResponseException, InvalidBucketNameException, XmlPullParserException, ErrorResponseException {
        MinioClient client = getMinioClient();
        String name = FileFormatUtil.getFileRelativePathForDelete(bucketName,fileName);
        return client.getObject(bucketName,name);
    }
}
