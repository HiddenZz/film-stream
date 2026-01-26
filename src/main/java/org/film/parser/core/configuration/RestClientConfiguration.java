package org.film.parser.core.configuration;

import org.film.parser.core.configuration.properties.RestClientProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import javax.net.ssl.*;
import java.net.CookieManager;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.Duration;

@EnableConfigurationProperties({RestClientProperties.class})
@Configuration
public class RestClientConfiguration {

    @Bean
    RestClient restClient(RestClientProperties props) {
        HttpClient jdkHttpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        return RestClient.builder()
                .requestFactory(new JdkClientHttpRequestFactory(jdkHttpClient))
                .baseUrl(props.getParseHost())
                .build();
    }

    @Bean
    RestClient lumexRestClient() {
        HttpClient jdkHttpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(15))
                .cookieHandler(new CookieManager())
                .build();

        return RestClient.builder()
                .requestFactory(new JdkClientHttpRequestFactory(jdkHttpClient))
                .defaultHeaders((headers) -> {
                    headers.add("Origin", "https://p.lumex.space");
                    headers.add("Referer", "https://p.lumex.space/");
                    headers.add("Sec-Fetch-Dest", "empty");
                    headers.add("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36");
                }).build();
    }


    @Bean
    RestClient veveoRestClient() {
        return RestClient.builder().build();
    }


    @Bean
    RestClient proxyRestClient() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());

        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

        SSLParameters sslParameters = new SSLParameters();
        sslParameters.setEndpointIdentificationAlgorithm(null);

        HttpClient jdkHttpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(5))
                .cookieHandler(new CookieManager())
                .sslContext(sc)
                .sslParameters(sslParameters)
                .proxy(ProxySelector.of(new InetSocketAddress("127.0.0.1", 8080)))
                .build();

        return RestClient.builder()
                .requestFactory(new JdkClientHttpRequestFactory(jdkHttpClient))
                .build();
    }

}
