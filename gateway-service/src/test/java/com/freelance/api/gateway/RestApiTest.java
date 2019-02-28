package com.freelance.api.gateway;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelance.api.model.Freelancer;
import com.freelance.api.model.Project;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;

@RunWith(Arquillian.class)
public class RestApiTest {

	private static String port = "18080";

	@Rule
	public WireMockRule serviceMock = new WireMockRule(18081);
//WireMockConfiguration.wireMockConfig().dynamicPort()
	@Deployment
	public static Archive<?> createDeployment() {
		return ShrinkWrap.create(WebArchive.class).addPackages(true, RestApplication.class.getPackage())
				.addAsResource("project-local.yml", "project-defaults.yml")
				.addAsResource("application-local.properties", "application.properties");
	}

	@Before
	public void beforeTest() throws Exception {
		RestAssured.baseURI = String.format("http://localhost:%s", port);
		mockResponses();
	}

	private void mockResponses() throws Exception{
		Freelancer freelancer1 = new Freelancer();
		freelancer1.setFirstname("Anakin");
		freelancer1.setLastname("Skywalker");
		freelancer1.setSkills("Light saber, backstab");
		freelancer1.setId("DarthVader");

		Freelancer freelancer2 = new Freelancer();
		freelancer2.setFirstname("Luke");
		freelancer2.setLastname("Skywalker");
		freelancer2.setSkills("Light saber, The Force");
		freelancer2.setId("TheOne");
		List<Freelancer> freelancerList = new ArrayList<Freelancer>();
		freelancerList.add(freelancer1);
		freelancerList.add(freelancer2);

		ObjectMapper mapper = new ObjectMapper();
		String freelancerResponseStr = mapper.writeValueAsString(freelancerList);
		String darthVaderResponseStr = mapper.writeValueAsString(freelancer1);
		serviceMock.stubFor(get(urlEqualTo("/freelancers")).willReturn(aResponse().withStatus(200)
				.withHeader("Content-Type", "application/json").withBody(freelancerResponseStr)));
		serviceMock.stubFor(get(urlEqualTo("/freelancers/DarthVader")).willReturn(aResponse().withStatus(200)
				.withHeader("Content-Type", "application/json").withBody(darthVaderResponseStr)));
		
		Project proj1 = new Project();
		proj1.setProjectId("proj001");
		proj1.setOwnerFirstName("Lim");
		proj1.setProjectStatus("open");
		proj1.setProjectTitle("LimKokWing university");
		proj1.setProjectDescription("Building a university");

		Project proj2 = new Project();
		proj2.setProjectId("proj002");
		proj2.setOwnerFirstName("Kuok");
		proj2.setProjectStatus("open");
		proj2.setProjectTitle("Robert Kuok factory");
		proj2.setProjectDescription("Building a sugar factory");

		List<Project> projList = new ArrayList<Project>();
		projList.add(proj1);
		projList.add(proj2);

		String projectListResponseStr = mapper.writeValueAsString(projList);
		String proj1ResponseStr = mapper.writeValueAsString(proj1);
		serviceMock.stubFor(get(urlMatching("/projects")).willReturn(aResponse().withStatus(200)
				.withHeader("Content-Type", "application/json").withBody(projectListResponseStr)));
		serviceMock.stubFor(get(urlMatching("/projects/proj001")).willReturn(aResponse().withStatus(200)
				.withHeader("Content-Type", "application/json").withBody(proj1ResponseStr)));
		serviceMock.stubFor(get(urlMatching("/projects/status/open")).willReturn(aResponse().withStatus(200)
				.withHeader("Content-Type", "application/json").withBody(projectListResponseStr)));
		
	}
	@Test
	@RunAsClient
	public void testGetFreelancers() throws Exception {
		
		JsonPath jsonPath = given().get("/gateway/freelancers").then().assertThat().statusCode(200)
				.contentType(ContentType.JSON).extract().body().jsonPath();

		List<HashMap<String, String>> ret = jsonPath.get("");

		assertEquals(2, ret.size());
		assertEquals("DarthVader", ret.get(0).get("id"));
		assertEquals("TheOne", ret.get(1).get("id"));
	}

	@Test
	@RunAsClient
	public void testGetFreelancerById() throws Exception {

		given().get("/gateway/freelancers/{id}", "DarthVader").then().assertThat().statusCode(200)
				.contentType(ContentType.JSON).body("firstname", equalTo("Anakin"))
				.body("lastname", equalTo("Skywalker"));
	}

	@Test
	@RunAsClient
	public void testGetProjects() throws Exception {
		
		JsonPath jsonPath = given().get("/gateway/projects").then().assertThat().statusCode(200)
				.contentType(ContentType.JSON).extract().body().jsonPath();

		List<HashMap<String, String>> ret = jsonPath.get("");

		assertEquals(2, ret.size());
		assertEquals("proj001", ret.get(0).get("projectId"));
		assertEquals("Lim", ret.get(0).get("ownerFirstName"));


		assertEquals("proj002", ret.get(1).get("projectId"));
		assertEquals("Kuok", ret.get(1).get("ownerFirstName"));

	}

	@Test
	@RunAsClient
	public void testGetProjectById() throws Exception {

		given().get("/gateway/projects/{projectId}", "proj001").then().assertThat().statusCode(200)
		.contentType(ContentType.JSON).body("projectId", equalTo("proj001"))
		.body("ownerFirstName", equalTo("Lim")).body("projectStatus", equalTo("open"));
	}

	@Test
	@RunAsClient
	public void testGetProjectsByStatus() throws Exception {

		
		JsonPath jsonPath = given().get("/gateway/projects/status/open").then().assertThat().statusCode(200)
				.contentType(ContentType.JSON).extract().body().jsonPath();

		List<HashMap<String, String>> ret = jsonPath.get("");

		assertEquals(2, ret.size());
		assertEquals("proj001", ret.get(0).get("projectId"));
		assertEquals("Lim", ret.get(0).get("ownerFirstName"));


		assertEquals("proj002", ret.get(1).get("projectId"));
		assertEquals("Kuok", ret.get(1).get("ownerFirstName"));

	}

}
