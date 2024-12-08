package com.escamilla.sensor.config;

import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.FileReader;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;



public class MqttSSLUtil {
    SocketFactory getSslSocketFactory(String CA_CERT_PATH, String CLIENT_CERT_PATH, String CLIENT_KEY_PATH) {
        try {
            // Cargar el certificado CA
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            FileInputStream caInput = new FileInputStream(CA_CERT_PATH);
            X509Certificate caCert = (X509Certificate) cf.generateCertificate(caInput);

            // Crear un TrustStore y agregar el certificado CA
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null); // Inicializar un KeyStore vacío
            trustStore.setCertificateEntry("ca", caCert);

            // Cargar el certificado del cliente

            FileInputStream clientCertInput = new FileInputStream(CLIENT_CERT_PATH);
            X509Certificate clientCert = (X509Certificate) cf.generateCertificate(clientCertInput);

            // Cargar la clave privada del cliente

            PrivateKey privateKey;
            try (PEMParser pemParser = new PEMParser(new FileReader(CLIENT_KEY_PATH))) {
                Object object = pemParser.readObject();
                if (object instanceof PEMKeyPair) {
                    PrivateKeyInfo privateKeyInfo = ((PEMKeyPair) object).getPrivateKeyInfo();
                    privateKey = new JcaPEMKeyConverter().getPrivateKey(privateKeyInfo);
                } else if (object instanceof PrivateKeyInfo) {
                    privateKey = new JcaPEMKeyConverter().getPrivateKey((PrivateKeyInfo) object);
                } else {
                    throw new IllegalArgumentException("Formato de clave privada no soportado.");
                }
            }
            // Crear un KeyStore para la clave privada y el certificado del cliente
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setKeyEntry("client", privateKey, null, new java.security.cert.Certificate[]{clientCert});

            // Configurar KeyManager para el cliente
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, null);

            // Configurar TrustManager para el servidor (CA)
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

            // Crear el SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            return sslContext.getSocketFactory();

        } catch (Exception e) {
            throw new RuntimeException("Error configurando el contexto SSL", e);
        }
    }
}
