package io.selfproject.authorizationserver.security;


import com.nimbusds.jose.jwk.RSAKey;
import io.selfproject.authorizationserver.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.UUID;

@Slf4j
@Component
public class KeyUtils {
    private static final String RSA = "RSA";

    @Value( "${spring.profiles.active}")
    private  String activeProfile;

    @Value( "${keys.private}")
    private  String privateKey;

    @Value( "${keys.public}")
    private  String publicKey;

    public RSAKey getRSAKeyPair() {
        return generateRSAKeyPair(privateKey,publicKey);
    }

    // for generating RSA Public and Private Key Pairs
    public RSAKey generateRSAKeyPair(String privateKeyName, String publicKeyName) {
        KeyPair keyPair;
        var keysDirectory = Paths.get( "src","main","resources","keys");
        verifyKeysDirectory(keysDirectory);
        if(Files.exists(keysDirectory.resolve(privateKeyName)) && Files.exists(keysDirectory.resolve(publicKeyName))){
            log.info("RSA Keys Already Exist. Loading keys From File Paths: {}, {}",publicKeyName,privateKeyName );

            try {
                var publicKeyFile = keysDirectory.resolve(publicKeyName).toFile();
                var privateKeyFile = keysDirectory.resolve(privateKeyName).toFile();
                var keyFactory = KeyFactory.getInstance(RSA);

                // for generating RSA Public Key
                byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
                EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
                RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);

                // for generating RSA private Key
                byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());
                PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
                RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);

                var keyId = "fe2950e0-5795-460c-a68c-64a50fa7e728";//UUID.randomUUID().toString();
                log.info("RSA Keys Loaded Successfully. KeyId: {}",keyId);
                return new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(keyId).build();
            }catch (Exception exception){
                log.error(exception.getMessage());
                throw new ApiException(exception.getMessage());
            }
        } else {
            if(activeProfile.equalsIgnoreCase("prod")){
                throw new ApiException("Public and Private Keys don't exist in prod environment");
            }
        }
       try {
           log.info("Generating New Public And private Key: {}, {}", publicKeyName, privateKeyName);
           var keyPairgenerator = KeyPairGenerator.getInstance(RSA);
           keyPairgenerator.initialize(2048);
           keyPair = keyPairgenerator.generateKeyPair();
           RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
           RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
           try (var fos = new FileOutputStream(keysDirectory.resolve(privateKeyName).toFile())){
               PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyPair.getPrivate().getEncoded());
               fos.write(keySpec.getEncoded());
           }
           try (var fos = new FileOutputStream(keysDirectory.resolve(publicKeyName).toFile())){
               X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyPair.getPublic().getEncoded());
               fos.write(keySpec.getEncoded());
                }
           var keyId = "fe2950e0-5795-460c-a68c-64a50fa7e728";
           log.info("RSA Keys Loaded Successfully. KeyId: {}",keyId);
           return new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(UUID.randomUUID().toString()).build();

       } catch (Exception exception){
           throw new ApiException(exception.getMessage());
       }
    }

    private void verifyKeysDirectory(Path keysDirectory) {
        if(!Files.exists(keysDirectory)){
            try {
                Files.createDirectories(keysDirectory);
            } catch (Exception exception) {
                log.error(exception.getMessage());
                throw new ApiException(exception.getMessage());
            }
            log.info("Keys directory created {}", keysDirectory);
        }
    }
}
