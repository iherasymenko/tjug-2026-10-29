package io.github.iherasymenko.tjug_weather_network.tjug_weather_network.testinfra;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.List;
import java.util.stream.Collectors;

final class CertificateGenerator {

    private static final String ALIAS = "mock-server";
    private static final String PASSWORD = "changeit";

    private final Path trustStoreFile;
    private final KeyStore keyStore;
    private final SystemPropertyOverrides jvmTrustStoreSettings;
    private SSLContext previousDefaultContext;

    CertificateGenerator(Path directory, List<String> hosts) throws IOException, GeneralSecurityException, InterruptedException {
        this.trustStoreFile = directory.resolve("truststore.p12");
        this.keyStore = generateKeyStore(directory.resolve("mock-server.p12"), hosts);
        this.jvmTrustStoreSettings = new SystemPropertyOverrides()
                .add("javax.net.ssl.trustStore", trustStoreFile.toString())
                .add("javax.net.ssl.trustStorePassword", PASSWORD)
                .add("javax.net.ssl.trustStoreType", KeyStore.getDefaultType());
    }

    SSLContext serverContext() throws GeneralSecurityException {
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, PASSWORD.toCharArray());
        SSLContext serverContext = SSLContext.getInstance("TLS");
        serverContext.init(keyManagerFactory.getKeyManagers(), null, null);
        return serverContext;
    }

    void installAsJvmTrustStore() throws IOException, GeneralSecurityException {
        Path defaultTrustStore = Path.of(System.getProperty("javax.net.ssl.trustStore",
                Path.of(System.getProperty("java.home"), "lib", "security", "cacerts").toString()));
        String defaultPassword = System.getProperty("javax.net.ssl.trustStorePassword", PASSWORD);

        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (InputStream in = Files.newInputStream(defaultTrustStore)) {
            trustStore.load(in, defaultPassword.toCharArray());
        }
        trustStore.setCertificateEntry(ALIAS, keyStore.getCertificate(ALIAS));
        try (OutputStream out = Files.newOutputStream(trustStoreFile)) {
            trustStore.store(out, PASSWORD.toCharArray());
        }
        jvmTrustStoreSettings.override();

        // The properties only apply if the default SSLContext hasn't been created yet, so replace it as well
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);
        SSLContext clientContext = SSLContext.getInstance("TLS");
        clientContext.init(null, trustManagerFactory.getTrustManagers(), null);
        previousDefaultContext = SSLContext.getDefault();
        SSLContext.setDefault(clientContext);
    }

    void uninstallAsJvmTrustStore() {
        SSLContext.setDefault(previousDefaultContext);
        jvmTrustStoreSettings.rollback();
    }

    private static KeyStore generateKeyStore(Path file, List<String> hosts) throws IOException, GeneralSecurityException, InterruptedException {
        String keytool = Path.of(System.getProperty("java.home"), "bin", "keytool").toString();
        String subjectAlternativeNames = hosts.stream().map(host -> "dns:" + host).collect(Collectors.joining(","));
        try (Process process = new ProcessBuilder(
                keytool, "-genkeypair",
                "-alias", ALIAS,
                "-keyalg", "RSA", "-keysize", "2048",
                "-validity", "1",
                "-dname", "CN=Mock Server",
                "-ext", "SAN=" + subjectAlternativeNames,
                "-keystore", file.toString(), "-storetype", "PKCS12",
                "-storepass", PASSWORD, "-keypass", PASSWORD,
                "-noprompt")
                .redirectErrorStream(true)
                .start()) {
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            if (process.waitFor() != 0) {
                throw new IllegalStateException("keytool failed: " + output);
            }
        }

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (InputStream in = Files.newInputStream(file)) {
            keyStore.load(in, PASSWORD.toCharArray());
        }
        return keyStore;
    }

}
