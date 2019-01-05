package com.example.demo;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.config.EncoderConfig.encoderConfig;
import static io.restassured.config.LogConfig.logConfig;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.common.Notifier;
import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;

import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;

@DisplayName("WireMock Test Demo")
public class WireMockDemoTests {

  private WireMockServer server;
  private RestAssuredConfig config;

  @BeforeEach
  void setup() {
    // REST Assured configuration
    config = config().encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false))
        .logConfig(logConfig().enablePrettyPrinting(true));
    // server = new WireMockServer();
    // server = new WireMockServer(new WireMockConfiguration());
    // WireMockConfiguration config = wireMockConfig().port(8089).notifier(new
    // ConsoleNotifier(true));

    // Configuring for static calls
    // configureFor("localhost", 8089);

    // ConsoleNotifier
    Notifier notifier = new ConsoleNotifier(true);
    // Slf4jNotifier
    // Notifier notifier = new Slf4jNotifier(true);

    Options options = options().port(8080).notifier(notifier);
    server = new WireMockServer(options);
    server.start();
  }

  @AfterEach
  void tearDown() {
    server.stop();
  }

  @Test
  @DisplayName("wiremock demo #1")
  void mockDemo1() {

    // stub (WireMock)
    server.stubFor(get(urlEqualTo("/mock/demo/1")).withName("mock_demo_1")
        .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/plain").withBody("Hello world!")));

    // shortcuts
    // server.stubFor(get("/mock/demo/1").willReturn(aResponse().withStatus(200)));

    // exercise and assert (REST Assured)
    given().config(config).header("Content-Type", "text/plain").when().get("http://localhost:8080/mock/demo/1").then()
        .assertThat().contentType(ContentType.TEXT).statusCode(200).header("Matched-Stub-Name", "mock_demo_1")
        .body(Matchers.containsString("Hello world!"));

    // verify (WireMock)
    server.verify(getRequestedFor(urlEqualTo("/mock/demo/1")).withHeader("Content-Type", equalTo("text/plain")));
  }

  @Test
  @DisplayName("wiremock demo #2")
  void mockDemo2() {

    // exercise and assert (REST Assured)
    given().config(config).header("Content-Type", "application/json").when().get("http://localhost:8080/mock/demo/2")
        .then().assertThat().contentType(ContentType.JSON).statusCode(200).header("Matched-Stub-Name", "mock_demo_2")
        .body("name", Matchers.equalTo("John"));

    // verify (WireMock)
    server.verify(getRequestedFor(urlEqualTo("/mock/demo/2")).withHeader("Content-Type", equalTo("application/json")));
  }

  @Test
  @DisplayName("wiremock demo #3")
  void mockDemo3() {

    given().config(config).header("Content-Type", "application/json").when().get("http://localhost:8080/mock/demo/3")
        .then().assertThat().contentType(ContentType.JSON).statusCode(200).header("Matched-Stub-Name", "mock_demo_3")
        .body("lotto.lottoId", Matchers.equalTo(5)).body("lotto.winners.winnerId", Matchers.hasItems(23, 54));
  }

  @Test
  @DisplayName("wiremock demo #4")
  void outputStub() {
    StubMapping mapping = server
        .stubFor(get("/some/thing").withName("mock_demo_4").willReturn(aResponse().withStatus(200)));

    System.out.println(mapping.toString());
  }

}
