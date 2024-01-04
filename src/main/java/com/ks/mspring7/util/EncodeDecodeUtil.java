package com.ks.mspring7.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;

public class EncodeDecodeUtil {

    private Logger logger = LoggerFactory.getLogger(EncodeDecodeUtil.class);
    public EncodeDecodeUtil() {
    }

    public String encodeString(String originalInput) {
        byte[] bytesEncoded = null;
        try {
            bytesEncoded = Base64.getEncoder().encode(originalInput.getBytes());
            logger.info("encoded value is " + new String(bytesEncoded));
        } catch (Exception e) {
            throw e;
        }
        return new String(bytesEncoded);
    }

    public String decodeString(String encodedString) {
        byte[] valueDecoded = null;
        try {
            valueDecoded = Base64.getDecoder().decode(encodedString);
            logger.info("encoded value is " + new String(valueDecoded));
        } catch (Exception e) {
            throw e;
        }
        return new String(valueDecoded);
    }

    public String encodedUrl(String originalURL) {
        byte[] bytesEncoded = null;
        try {
            bytesEncoded = Base64.getUrlEncoder().encode(originalURL.getBytes());
            logger.info("encoded url value is " + new String(bytesEncoded));
        } catch (Exception e) {
            throw e;
        }
        return new String(bytesEncoded);
    }

    public String decodeUrl(String encodedUrl) {
        byte[] valueDecoded = null;
        try {
            valueDecoded = Base64.getUrlDecoder().decode(encodedUrl);
            logger.info("decoded url value is " + new String(valueDecoded));
        } catch (Exception e) {
            throw e;
        }
        return new String(valueDecoded);
    }
}
