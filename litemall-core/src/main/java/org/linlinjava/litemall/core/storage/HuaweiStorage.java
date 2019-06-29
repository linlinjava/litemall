package org.linlinjava.litemall.core.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.exception.ObsException;
import com.obs.services.model.AccessControlList;
import com.obs.services.model.BucketCors;
import com.obs.services.model.BucketCorsRule;
import com.obs.services.model.ObjectMetadata;
import com.obs.services.model.OptionsInfoRequest;

/**
 * 目前可以测试通上传，并没有集成到项目里面，待处理。。。。
 */
public class HuaweiStorage {

    private static final String endPoint = "https://obs.cn-north-1.myhuaweicloud.com";
    
    private static final String ak = "AK**************";
    
    private static final String sk = "SK**************";
	
    private static ObsClient obsClient;
    
    private static String bucketName = "litemalltest";
    
    private static String objectKey = "test/img1.jpg";
    
    public static void main(String[] args)
        throws IOException
    {
        ObsConfiguration config = new ObsConfiguration();
        config.setSocketTimeout(30000);
        config.setConnectionTimeout(10000);
        config.setEndPoint(endPoint);
        try
        {
            /*
             * Constructs a obs client instance with your account for accessing OBS
             */
            obsClient = new ObsClient(ak, sk, config);
            
            /*
             * Create bucket 
             */
            //obsClient.createBucket(bucketName);
            
            /*
             * Create object
             */
            File file = new File("C:/Users/Administrator/Pictures/1.jpg");
            obsClient.putObject(bucketName, objectKey, new FileInputStream(file), null);
            System.out.println("Create object:" + objectKey + " successfully!\n");
            
            /*
             * Get object metadata
             */
            System.out.println("Getting object metadata");
            ObjectMetadata metadata = obsClient.getObjectMetadata(bucketName, objectKey, null);
            System.out.println("\t" + metadata);
            
            /*
             * Get object
             */
//            System.out.println("Getting object content");
//            ObsObject obsObject = obsClient.getObject(bucketName, objectKey, null);
//            System.out.println("\tobject content:" + ServiceUtils.toString(obsObject.getObjectContent()));
            
            /*
             * Copy object
             */
            /*String sourceBucketName = bucketName;
            String destBucketName = bucketName;
            String sourceObjectKey = objectKey;
            String destObjectKey = objectKey + "-back";
            System.out.println("Copying object\n");
            obsClient.copyObject(sourceBucketName, sourceObjectKey, destBucketName, destObjectKey);*/
            
            /*
             * Options object
             */
            // doObjectOptions();
            
            /*
             * Put/Get object acl operations
             */
            // doObjectAclOperations();
            
            /*
             * Delete object
             */
            /*System.out.println("Deleting objects\n");
            obsClient.deleteObject(bucketName, objectKey, null);
            obsClient.deleteObject(bucketName, destObjectKey, null);*/
        }
        catch (ObsException e)
        {
            System.out.println("Response Code: " + e.getResponseCode());
            System.out.println("Error Message: " + e.getErrorMessage());
            System.out.println("Error Code:       " + e.getErrorCode());
            System.out.println("Request ID:      " + e.getErrorRequestId());
            System.out.println("Host ID:           " + e.getErrorHostId());
        }
        finally
        {
            if (obsClient != null)
            {
                try
                {
                    /*
                     * Close obs client 
                     */
                    obsClient.close();
                }
                catch (IOException e)
                {
                }
            }
        }
        
    }
    
    /**
     * 跨域访问规则
     */
    private static void doObjectOptions()
        throws ObsException
    {
        
        BucketCors bucketCors = new BucketCors();
        BucketCorsRule rule = new BucketCorsRule();
        rule.getAllowedHeader().add("Authorization");
        rule.getAllowedOrigin().add("http://www.a.com");
        rule.getAllowedOrigin().add("http://www.b.com");
        rule.getExposeHeader().add("x-obs-test1");
        rule.getExposeHeader().add("x-obs-test2");
        rule.setMaxAgeSecond(100);
        rule.getAllowedMethod().add("HEAD");
        rule.getAllowedMethod().add("GET");
        rule.getAllowedMethod().add("PUT");
        bucketCors.getRules().add(rule);
        obsClient.setBucketCors(bucketName, bucketCors);
        
        System.out.println("Options object\n");
        OptionsInfoRequest optionInfo = new OptionsInfoRequest();
        optionInfo.setOrigin("http://www.a.com");
        optionInfo.getRequestHeaders().add("Authorization");
        optionInfo.getRequestMethod().add("PUT");
        System.out.println(obsClient.optionsObject(bucketName, objectKey, optionInfo));
    }
    
    private static void doObjectAclOperations()
        throws ObsException
    {
        System.out.println("Setting object ACL to public-read \n");
        
        obsClient.setObjectAcl(bucketName, objectKey, AccessControlList.REST_CANNED_PUBLIC_READ);
        
        System.out.println("Getting object ACL " + obsClient.getObjectAcl(bucketName, objectKey) + "\n");
        
        System.out.println("Setting object ACL to private \n");
        
        obsClient.setObjectAcl(bucketName, objectKey, AccessControlList.REST_CANNED_PRIVATE);
        
        System.out.println("Getting object ACL " + obsClient.getObjectAcl(bucketName, objectKey) + "\n");
    }
}
