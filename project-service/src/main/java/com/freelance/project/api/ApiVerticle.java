package com.freelance.project.api;

import java.util.List;

import com.freelance.project.model.Project;
import com.freelance.project.verticle.service.ProjectService;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

/*
 * This class exposes the services through a httpServer
 */
public class ApiVerticle extends AbstractVerticle {

	private ProjectService projectService;

	public ApiVerticle(ProjectService projectService) {
		this.projectService = projectService;
	}

	@Override
	public void start(Future<Void> startFuture) throws Exception {

		//A Router, when receives a request, looks for the matching route, and passes the request further. 
		//The routes having a handler method associated with it to do something with the request.
		Router router = Router.router(vertx);
		router.get("/projects").handler(this::getProjects);
		router.get("/projects/:projectId").handler(this::getProjectById);
		router.get("/projects/status/:projectStatus").handler(this::getProjectsByStatus);
		router.route("/projects").handler(BodyHandler.create());

		// Health Checks
		router.get("/health/readiness").handler(rc -> rc.response().end("OK"));
		HealthCheckHandler healthCheckHandler = HealthCheckHandler.create(vertx).register("health", f -> health(f));
		router.get("/health/liveness").handler(healthCheckHandler);
		
		//Add the router, created in the previous section to the HTTP server:
		//Notice that we have added requestHandler(router::accept) to the server. 
		//This instructs the server, to invoke the accept() of the router object when any request is received.
		vertx.createHttpServer().requestHandler(router::accept).listen(config().getInteger("project.http.port", 8080),
				result -> {
					if (result.succeeded()) {
						startFuture.complete();
					} else {
						startFuture.fail(result.cause());
					}
				});
	}

	//remaining methods are private since they do not need to be exposed 
	private void getProjects(RoutingContext rc) {
		projectService.getProjects(ar -> {
			if (ar.succeeded()) {
				List<Project> projects = ar.result();
				JsonArray json = new JsonArray();
				projects.stream().map(p -> p.toJson()).forEach(p -> json.add(p));
				rc.response().putHeader("Content-type", "application/json").end(json.encodePrettily());
			} else {
				rc.fail(ar.cause());
			}
		});
	}

	private void getProjectById(RoutingContext rc) {
		String projectId = rc.request().getParam("projectId");
		projectService.getProjectById(projectId, ar -> {
			if (ar.succeeded()) {
				Project project = ar.result();
				JsonObject json;
				if (project != null) {
					json = project.toJson();
					rc.response().putHeader("Content-type", "application/json").end(json.encodePrettily());
				} else {
					rc.fail(404);
				}
			} else {
				rc.fail(ar.cause());
			}
		});
	}

	private void getProjectsByStatus(RoutingContext rc) {
		String status = rc.request().getParam("projectStatus");
		projectService.getProjectsByStatus(status, ar -> {
			if (ar.succeeded()) {
				List<Project> projects = ar.result();
				JsonArray json = new JsonArray();
				projects.stream().map(p -> p.toJson()).forEach(p -> json.add(p));
				rc.response().putHeader("Content-type", "application/json").end(json.encodePrettily());
			} else {
				rc.fail(ar.cause());
			}
		});
	}

	private void health(Future<Status> future) {
		projectService.ping(ar -> {
			if (ar.succeeded()) {
				// HealthCheckHandler has a timeout of 1000s. If timeout is
				// exceeded, the future will be failed
				if (!future.isComplete()) {
					future.complete(Status.OK());
				}
			} else {
				if (!future.isComplete()) {
					future.complete(Status.KO());
				}
			}
		});
	}

}
