package com.freelancer.rest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.freelancer.service.FreelancerService;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class FreelancerEndpointTest {

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

	@Before
	public void beforeTest() throws Exception {
		RestAssured.baseURI = String.format("http://localhost:%d/freelancers/", wireMockRule.port());
		initWireMockServer();
	}

	@Test
	public void getFreelancerById() throws Exception {
		given().get("/{id}", "jConnor").then().assertThat().statusCode(200).contentType(ContentType.JSON)
				.body("id", equalTo("jConnor")).body("firstName", equalTo("John")).body("lastName", equalTo("Connor"))
				.body("skills", equalTo("JEE, Javascript, Reactive, Scala"));
	}


	//Mock the response using a local file
	private void initWireMockServer() throws Exception {
		InputStream isresp = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("freelancer-response.json");

		stubFor(get(urlEqualTo("/freelancers/jConnor"))
				.willReturn(aResponse().withStatus(200).withHeader("Content-type", "application/json")
						.withBody(IOUtils.toString(isresp, Charset.defaultCharset()))));

		stubFor(get(urlEqualTo("/freelancers/error")).willReturn(aResponse().withStatus(500)));
	}

}
