package net.sf.hivgensim.services;

public abstract class AbstractService {
	
	private String url = "http://localhost:8080/wts/services/";
	private String uid = "gbehey0";
	private String passwd = "bla123";
	private String encodedPrivateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMz1OALfr/CLLt20vxBJ/xvbwu9CUXY0CnV8YIigfIHUr7w5fPgBA4YavcVavqqqHeQgXRXV5luOLczBRnYNv6y3HBddZ5UvjLa/zr8/37OUMURhkqyB66lU8FOrV5ONslf/+1zs1Dpi83y0Yxhx0PRYub75JW7WyoVCpGz0qDELAgMBAAECgYBSk/ZmSgPUMe/HCfz1Lisn6UpIJfs2Wc9g+KTYR3kCwlOvzaXJMndd/8Y4DtDFaFc0w8ldc9olR0qytaiTBgUUc94UA+MtOM4aOjd0u9MrD59mGCG3MO1+ojjn9PMiPmXlj4QIdbu0CkWnwStrUkFr80sgUvHXSW09sM/YRj6x6QJBAOz8fO//IGO8xEnfhRIryvjHj/dnM7rYX2QMoYYvrd0Nvdxyr3t6qTEEkgNeimBmfZuG2ULn787V8fUoZUX2bS0CQQDdZuSHEbZ7GN+jq2QRh5fgsxcHSn460aM8Y9C5mN9r+w3Tq1j4qcvtrDu+ltwFInc8fEiSjQNx0jR712fi5yoXAkANp36LVWfIV1f36akBIwTO0LC60HdqjIzydsfXs2eRFPmbegAiXS7iZCEFkKzoYP9btqlN8Y8fm7QVK/6pyUkBAkEA0ImW3QY5DC88jqvjoINH8dSd7zciOIK3Ly2RLw+n+cxJlMMDFYzRUTd2Oqlb6dYx2x3xOWBrCy2EU9Vru5Qi1wJBAKPjEQW8ZSrVeys3p2x5kmcmGebz+M1u1dkEgNegBiglI3DnW3oxLD2JhdOHzFyZ1hEJDFfCEuOPhGaIkmaXJqA=";
	
	protected String getUrl() {
		return url;
	}
	protected void setUrl(String url) {
		this.url = url;
	}
	protected String getUid() {
		return uid;
	}
	protected void setUid(String uid) {
		this.uid = uid;
	}
	protected String getPasswd() {
		return passwd;
	}
	protected void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	protected String getEncodedPrivateKey() {
		return encodedPrivateKey;
	}
	protected void setEncodedPrivateKey(String encodedPrivateKey) {
		this.encodedPrivateKey = encodedPrivateKey;
	}
	
}
