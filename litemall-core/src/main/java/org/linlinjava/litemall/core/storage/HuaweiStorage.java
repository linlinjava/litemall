package org.linlinjava.litemall.core.storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

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
public class HuaweiStorage implements Storage {
	
    private String endpoint;
    private String ak;
    private String sk;
    private ObsClient obsClient;
    private String bucketName;
    
    public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getAk() {
		return ak;
	}

	public void setAk(String ak) {
		this.ak = ak;
	}

	public String getSk() {
		return sk;
	}

	public void setSk(String sk) {
		this.sk = sk;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	/**
     * 跨域访问规则
     */
    private void doObjectOptions(String keyName)
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
        System.out.println(obsClient.optionsObject(bucketName, keyName, optionInfo));
    }
    
    private void doObjectAclOperations(String keyName)
        throws ObsException
    {
        System.out.println("Setting object ACL to public-read \n");
        
        obsClient.setObjectAcl(bucketName, keyName, AccessControlList.REST_CANNED_PUBLIC_READ);
        
        System.out.println("Getting object ACL " + obsClient.getObjectAcl(bucketName, keyName) + "\n");
        
        System.out.println("Setting object ACL to private \n");
        
        obsClient.setObjectAcl(bucketName, keyName, AccessControlList.REST_CANNED_PRIVATE);
        
        System.out.println("Getting object ACL " + obsClient.getObjectAcl(bucketName, keyName) + "\n");
    }

    private ObsClient getOBSClient() {
    	try {
			ObsConfiguration config = new ObsConfiguration();
			config.setSocketTimeout(30000);
			config.setConnectionTimeout(10000);
			config.setEndPoint(endpoint);
			obsClient = new ObsClient(ak, sk, config);
		} catch (Exception e) {
			e.printStackTrace();
			obsClient = null;
		}
        return obsClient;
    }
    
    private String getBaseUrl() {
        return "https://" + endpoint + "/" + bucketName + "/";
    }
    
	@Override
	public void store(InputStream inputStream, long contentLength, String contentType, String keyName) {
        try {
        	obsClient = getOBSClient();
            // 简单文件上传, 最大支持 5 GB, 适用于小文件上传, 建议 20M以下的文件使用该接口
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(contentLength);
            objectMetadata.setContentType(contentType);
            // 对象键（Key）是对象在存储桶中的唯一标识。
            obsClient.putObject(bucketName, keyName, inputStream, objectMetadata);
        } catch (ObsException e) {
            System.out.println("Response Code: " + e.getResponseCode());
            System.out.println("Error Message: " + e.getErrorMessage());
            System.out.println("Error Code:       " + e.getErrorCode());
            System.out.println("Request ID:      " + e.getErrorRequestId());
            System.out.println("Host ID:           " + e.getErrorHostId());
        } finally {
            if (obsClient != null) {
                try {obsClient.close();}catch (IOException e){}
            }
        }
	}

	@Override
	public Stream<Path> loadAll() {
		return null;
	}

	@Override
	public Path load(String keyName) {
		return null;
	}

	@Override
	public Resource loadAsResource(String keyName) {
		try {
            URL url = new URL(getBaseUrl() + keyName);
            Resource resource = new UrlResource(url);
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                return null;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
	}

	@Override
	public void delete(String keyName) {
		try {
            getOBSClient().deleteObject(bucketName, keyName);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	@Override
	public String generateUrl(String keyName) {
		return getBaseUrl() + keyName;
	}
}
