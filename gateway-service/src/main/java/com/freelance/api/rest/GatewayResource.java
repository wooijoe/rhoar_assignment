package com.freelance.api.rest;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.MediaType;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.ContextName;
import org.apache.camel.component.http4.HttpMethods;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;

import com.freelance.api.model.Freelancer;
import com.freelance.api.model.Project;

@ApplicationScoped
@ContextName("rest-context")
public class GatewayResource extends RouteBuilder {
	@Override
	public void configure() throws Exception {
		//Use properties component to specify where to retrieve the propertyPlaceholders below
		//{{project.service.url}} and {{freelancer.service.url}}
		PropertiesComponent pc = new PropertiesComponent();
		pc.setLocation("application.properties");
		getContext().addComponent("properties", pc);
		
		// The endpoints are enabled for CORS. The endpoints are likely to be
		// called from JavaScript-based
		// front-end applications, so CORS needs to be enabled.
		restConfiguration().component("undertow").bindingMode(RestBindingMode.json)
				.dataFormatProperty("prettyPrint", "true").enableCORS(true);

		
		rest("/gateway").description("Gateway Api rest service").produces("application/json")

				// projects/projectId/
				.get("/projects/{projectId}").description("Retrieves information for the given projectId").param()
				.name("projectId").type(RestParamType.path).description("The projectId of project").dataType("string")
				.endParam().outType(Project.class).route().id("findProjectByIdRoute").removeHeaders("CamelHttp*")
				.setBody().simple("null").setHeader(Exchange.CONTENT_TYPE, simple(MediaType.APPLICATION_JSON))
				.setHeader(Exchange.HTTP_METHOD, HttpMethods.GET)
				.setHeader(Exchange.HTTP_PATH, simple("projects/${header.projectId}"))
				.setHeader(Exchange.HTTP_URI, simple("{{project.service.url}}")).to("http4://DUMMY")
				.setHeader("CamelJacksonUnmarshalType", simple(Project.class.getName())).unmarshal()
				.json(JsonLibrary.Jackson, Project.class).endRest()

				// projects
				.get("/projects").description("Retrieves a list of all projects").outType(Project[].class).route()
				.id("findAllProjectsRoute").removeHeaders("CamelHttp*").setBody().simple("null")
				.setHeader(Exchange.CONTENT_TYPE, simple(MediaType.APPLICATION_JSON))
				.setHeader(Exchange.HTTP_METHOD, HttpMethods.GET).setHeader(Exchange.HTTP_PATH, simple("projects/"))
				.setHeader(Exchange.HTTP_URI, simple("{{project.service.url}}")).to("http4://DUMMY")
				.setHeader("CamelJacksonUnmarshalType", simple(Project[].class.getName())).unmarshal()
				.json(JsonLibrary.Jackson, Project[].class).endRest()

				// projects/status/{status}
				.get("/projects/status/{status}")
				.description(
						"Retrieves a list of projects with the given status (open, in_progress, completed, cancelled)")
				.param().name("status").type(RestParamType.path).description("The status of project").dataType("string")
				.endParam().outType(Project[].class).route().id("getProjectsByStatusRoute").removeHeaders("CamelHttp*")
				.setBody().simple("null").setHeader(Exchange.CONTENT_TYPE, simple(MediaType.APPLICATION_JSON))
				.setHeader(Exchange.HTTP_METHOD, HttpMethods.GET)
				.setHeader(Exchange.HTTP_PATH, simple("projects/status/${header.status}"))
				.setHeader(Exchange.HTTP_URI, simple("{{project.service.url}}")).to("http4://DUMMY")
				.setHeader("CamelJacksonUnmarshalType", simple(Project[].class.getName())).unmarshal()
				.json(JsonLibrary.Jackson, Project[].class).endRest()

				// freelancers/id/
				.get("/freelancers/{id}").description("Retrieves information for the given freelancer").param()
				.name("id").type(RestParamType.path).description("The freelancerId of freelancer").dataType("string")
				.endParam().outType(Freelancer.class).route().id("findFreelancerByIdRoute").removeHeaders("CamelHttp*")
				.setBody().simple("null").setHeader(Exchange.CONTENT_TYPE, simple(MediaType.APPLICATION_JSON))
				.setHeader(Exchange.HTTP_METHOD, HttpMethods.GET)
				.setHeader(Exchange.HTTP_PATH, simple("freelancers/${header.id}"))
				.setHeader(Exchange.HTTP_URI, simple("{{freelancer.service.url}}")).to("http4://DUMMY")
				.setHeader("CamelJacksonUnmarshalType", simple(Freelancer.class.getName())).unmarshal()
				.json(JsonLibrary.Jackson, Freelancer.class).endRest()

				// freelancers
				.get("/freelancers").description("Retrieves a list of all freelancers").outType(Freelancer[].class)
				.route().id("findAllFreelancersRoute").removeHeaders("CamelHttp*").setBody().simple("null")
				.setHeader(Exchange.CONTENT_TYPE, simple(MediaType.APPLICATION_JSON))
				.setHeader(Exchange.HTTP_METHOD, HttpMethods.GET).setHeader(Exchange.HTTP_PATH, simple("freelancers/"))
				.setHeader(Exchange.HTTP_URI, simple("{{freelancer.service.url}}")).to("http4://DUMMY")
				.setHeader("CamelJacksonUnmarshalType", simple(Freelancer[].class.getName())).unmarshal()
				.json(JsonLibrary.Jackson, Freelancer[].class).endRest()

		;
	}
}
