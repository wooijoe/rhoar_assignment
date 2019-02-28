package com.freelance.project.verticle;

import com.freelance.project.api.ApiVerticle;
import com.freelance.project.verticle.service.ProjectService;
import com.freelance.project.verticle.service.ProjectVerticle;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/*
 * The starting point which reads the project configurations and
 * create the verticles.
 * 
 * This class is specified in the pom.xml in the properties <vertx.verticle> 
 * and is used during start up by virtue of the Vert-X Maven Plugin
 * 
 * <<Vert-X Maven Plugin>>
 * The Maven packaging process adds certain MANIFEST.MF entries that control how the application is launched. 
 * The plugin takes care of adding the required entries to the MANIFEST.MF with values configured using the Common Configuration
	The following are the Manifest entries that will be added based on the configuration elements:
	PropertyName	ManifestAttribute 		Remarks
	vertx.verticle	Main-Verticle			The main verticle, i.e. the entry point of your application. Used when the Main-Class is io.vertx.core.Launcher.
	vertx.launcher	Main-Class				The main class used to start the application, defaults to io.vertx.core.Launcher
 */
public class MainVerticle extends AbstractVerticle {

	@Override
	public void start(Future<Void> startFuture) throws Exception {

		ConfigStoreOptions jsonConfigStore = new ConfigStoreOptions().setType("json");
		ConfigStoreOptions appStore = new ConfigStoreOptions().setType("configmap").setFormat("yaml")
				.setConfig(new JsonObject().put("name", System.getenv("APP_CONFIGMAP_NAME")).put("key",
						System.getenv("APP_CONFIGMAP_KEY")));

		ConfigRetrieverOptions options = new ConfigRetrieverOptions();
		if (System.getenv("KUBERNETES_NAMESPACE") != null) {
			// we're running in Kubernetes
			options.addStore(appStore);
		} else {
			// default to json based config
			jsonConfigStore.setConfig(config());
			options.addStore(jsonConfigStore);
		}

		ConfigRetriever.create(vertx, options).getConfig(ar -> {
			if (ar.succeeded()) {
				deployVerticles(ar.result(), startFuture);
			} else {
				System.out.println("Failed to retrieve the configuration.");
				startFuture.fail(ar.cause());
			}
		});
	}

	private void deployVerticles(JsonObject config, Future<Void> startFuture) {

		Future<String> apiVerticleFuture = Future.future();
		Future<String> projectVerticleFuture = Future.future();

		ProjectService projectService = ProjectService.createProxy(vertx);
		DeploymentOptions options = new DeploymentOptions();
		options.setConfig(config);
		
		//Create the ProjectVerticle (which has the service implementation) first then expose the Api through the ApiVerticle
		vertx.deployVerticle(new ProjectVerticle(), options, projectVerticleFuture.completer());
		vertx.deployVerticle(new ApiVerticle(projectService), options, apiVerticleFuture.completer());

		CompositeFuture.all(apiVerticleFuture, projectVerticleFuture).setHandler(ar -> {
			if (ar.succeeded()) {
				System.out.println("Verticles deployed successfully.");
				startFuture.complete();
			} else {
				System.out.println("WARNINIG: Verticles NOT deployed successfully.");
				startFuture.fail(ar.cause());
			}
		});

	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		super.stop(stopFuture);
	}

}
