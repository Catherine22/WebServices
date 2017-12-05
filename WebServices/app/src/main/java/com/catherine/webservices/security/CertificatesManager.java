package com.catherine.webservices.security;

import android.util.Base64;

import com.catherine.webservices.security.extensions.CertificateExtensionsHelper;
import com.catherine.webservices.security.extensions.OIDMap;
import com.catherine.webservices.toolkits.CLog;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Key;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Catherine on 2017/10/2.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class CertificatesManager {
    private final static String TAG = "CertificatesManager";

    /**
     * X.509是数字证书的规范，X.509只能携带公钥信息。<br>
     * 另外还有PKCS#7和12包含更多的一些信息。PKCS#12由于可包含私钥信息，而且文件本身还可通过密码保护，所以更适合信息交换。<br>
     * 常见的证书文件格式：".pem", ".cer", ".crt", ".der", ".p7b", ".p7c", ".p12"<br>
     * 总结：<br>
     * 1. 证书包含很多信息，但一般就是各种Key的内容。<br>
     * 2. 证书由CA签发。为了校验某个证书是否可信，往往需要把一整条证书链都校验一把，直到根证书。<br>
     * 3. 系统一般会集成很多根证书，这样免得使用者自己去下载根证书了。<br>
     * 4. 证书自己的格式通用为X.509，但是证书文件的格式却有很多。不同的系统可能支持不同的证书文件。
     *
     * @throws CertificateException
     * @throws IOException
     */
    public static void printCertificatesInfo(X509Certificate cf)
            throws CertificateException, IOException {

        CLog.d(TAG, "证书序列号:" + cf.getSerialNumber());
        CLog.d(TAG, "版本:" + cf.getVersion());
        CLog.d(TAG, "证书类型:" + cf.getType());
        CLog.d(TAG, String.format("有效期限:%s 到 %s", cf.getNotBefore(), cf.getNotAfter()));

        Map<String, String> subjectDN = refactorDN(cf.getSubjectDN().getName());

        StringBuilder su = new StringBuilder();
        boolean[] subjectUniqueID = cf.getSubjectUniqueID();
        if (subjectUniqueID != null) {
            for (boolean b : subjectUniqueID) {
                int myInt = (b) ? 1 : 0;
                su.append(myInt);
            }
        } else
            su.append("null");
        CLog.d(TAG, "主体" + subjectDN);
        CLog.d(TAG, String.format("主体:[唯一标识符:%s, 通用名称:%s, 机构单元名称:%s, 机构名:%s, 地理位置:%s, 州/省名:%s, 国名:%s]", su,
                subjectDN.get("CN"), subjectDN.get("OU"), subjectDN.get("O"),
                subjectDN.get("L"), subjectDN.get("ST"), subjectDN.get("C")));

        Map<String, String> issuerDN = refactorDN(cf.getIssuerDN().getName());

        StringBuilder i = new StringBuilder();
        if (subjectUniqueID != null) {
            boolean[] issuerUniqueID = cf.getIssuerUniqueID();
            for (boolean b : issuerUniqueID) {
                int myInt = (b) ? 1 : 0;
                i.append(myInt);
            }
        } else
            i.append("null");
        CLog.d(TAG, "签发者" + issuerDN);
        CLog.d(TAG, String.format("签发者:[唯一标识符:%s, 通用名称:%s, 机构单元名称:%s, 机构名:%s, 地理位置:%s, 州/省名:%s, 国名:%s]", i,
                issuerDN.get("CN"), issuerDN.get("OU"), issuerDN.get("O"),
                issuerDN.get("L"), issuerDN.get("ST"), issuerDN.get("C")));

        CLog.d(TAG, "签名算法:" + cf.getSigAlgName());
        CLog.d(TAG, String.format("签名算法OID:%s (%s)", cf.getSigAlgOID(), OIDMap.getName(cf.getSigAlgOID())));
        String sigAlgParams = (cf.getSigAlgParams() == null) ? "null"
                : Base64.encodeToString(cf.getSigAlgParams(), Base64.DEFAULT);
        CLog.d(TAG, "签名参数:" + sigAlgParams);
        CLog.d(TAG, "签名:" + Base64.encodeToString(cf.getSignature(), Base64.DEFAULT));

        CLog.d(TAG, "证书的限制路径长度:" + cf.getBasicConstraints());

        Key publicKey = cf.getPublicKey();
        CLog.d(TAG, "公鑰算法:" + publicKey.getAlgorithm());
        CLog.d(TAG, "公鑰格式:" + publicKey.getFormat());
        if (publicKey.getAlgorithm().equalsIgnoreCase("RSA")) {
            RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
            CLog.d(TAG, "Modulus:" + rsaPublicKey.getModulus());
            CLog.d(TAG, "Exponent:" + rsaPublicKey.getPublicExponent());

        }
        CLog.d(TAG, "公鑰:" + Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT));

        CLog.d(TAG, "扩展(Certificate Extensions):[");
        CertificateExtensionsHelper coarseGrainedExtensions = new CertificateExtensionsHelper(cf);
        CLog.d(TAG, coarseGrainedExtensions.toString());
        CLog.d(TAG, "]");
        // CLog.d(TAG,"==>X509Certificate: " + cf.toString());
    }

    /**
     * PEM格式字串转X509证书
     * <p>
     * -----BEGIN CERTIFICATE-----
     * xxx
     * -----END CERTIFICATE-----
     *
     * @param pem
     * @return
     * @throws CertificateException
     * @throws FileNotFoundException
     */
    public static X509Certificate pemToX509Certificate(String pem)
            throws CertificateException, FileNotFoundException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        String newString = pem.replaceAll("-----BEGIN CERTIFICATE-----", "").replaceAll("-----END CERTIFICATE-----", "");
        return (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(Base64.decode(newString, Base64.DEFAULT)));
    }

    /**
     * 读取X509证书文件
     *
     * @return
     * @throws CertificateException
     * @throws FileNotFoundException
     */
    public static X509Certificate loadX509Certificate(String path) throws CertificateException, FileNotFoundException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        FileInputStream fis = new FileInputStream(path); // 证书文件
        return (X509Certificate) cf.generateCertificate(fis);
    }

    private static Map<String, String> refactorDN(String name) {
        Map<String, String> map = new HashMap<>();
        String[] pairs = name.split(",");
        for (int i = 0; i < pairs.length; i++) {
            String pair = pairs[i];
            String[] keyValue = pair.split("=");
            if (keyValue.length > 1) {
                map.put(keyValue[0], keyValue[1]);
            } else {
                map.put(keyValue[0], "");
            }
        }
        return map;
    }
}