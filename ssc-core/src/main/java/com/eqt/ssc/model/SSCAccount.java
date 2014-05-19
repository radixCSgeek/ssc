package com.eqt.ssc.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Wraps up all the info we could want on an account for monitoring purposes.
 * The only one we must have is accountId, all the others are optional.
 *
 */
public class SSCAccount {

	protected String accountId;
	protected String accessKey;
	protected String secretKey;
	protected String s3BucketName;
	protected String s3Path;
	protected String ctBucketName;
	protected String ctPath;
	
	//for a given key, say s3, or ct, or ssc, can store the last update.
	protected Map<String, Long> updateTimestampMap = new HashMap<String, Long>();
	
	/**
	 * here for serialization
	 */
	public SSCAccount() {}
	
	public SSCAccount(String accountId) {
		this.accountId = accountId;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
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

	public String getS3BucketName() {
		return s3BucketName;
	}

	public void setS3BucketName(String s3BucketName) {
		this.s3BucketName = s3BucketName;
	}

	public String getS3Path() {
		return s3Path;
	}

	public void setS3Path(String s3Path) {
		this.s3Path = s3Path;
	}

	public String getCtBucketName() {
		return ctBucketName;
	}

	public void setCtBucketName(String ctBucketName) {
		this.ctBucketName = ctBucketName;
	}

	public String getCtPath() {
		return ctPath;
	}

	public void setCtPath(String ctPath) {
		this.ctPath = ctPath;
	}

	public Map<String, Long> getUpdateTimestampMap() {
		return updateTimestampMap;
	}

	public void setUpdateTimestampMap(Map<String, Long> updateTimestampMap) {
		this.updateTimestampMap = updateTimestampMap;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountId == null) ? 0 : accountId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SSCAccount other = (SSCAccount) obj;
		if (accountId == null) {
			if (other.accountId != null)
				return false;
		} else if (!accountId.equals(other.accountId))
			return false;
		return true;
	}
	
	
	
	
}
