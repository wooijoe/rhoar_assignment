package com.freelance.project.verticle.service;

import java.util.List;

import com.freelance.project.model.Project;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

/*
 * When creating a service there’s a certain amount of boilerplate code to listen on the event bus for incoming messages, 
 * route them to the appropriate method and return results on the event bus.
 * 
 * With Vert.x service proxies, you can avoid writing all that boilerplate code and concentrate on writing your service.
 * You write your service as a Java interface and annotate it with the @ProxyGen annotation
 * 
 * Service annotated with @ProxyGen annotation trigger the generation of the service helper classes:
    The service proxy: a compile time generated proxy that uses the EventBus to interact with the service via messages
    The service handler: a compile time generated EventBus handler that reacts to events sent by the proxy
 */
@ProxyGen
public interface ProjectService {

    final static String ADDRESS = "project-service";

    static ProjectService create(Vertx vertx, JsonObject config, MongoClient client) {
        return new ProjectServiceImpl(vertx, config, client);
    }

    /*
     * Alternatively, you can use the generated proxy class. 
     * The proxy class name is the service interface class name followed by VertxEBProxy. 
     * For instance, if your service interface is named SomeDatabaseService, 
     * the proxy class is named SomeDatabaseServiceVertxEBProxy.
     * 
     * This requires the package-info.java to have the @ModuleGen annotation
     */
    static ProjectService createProxy(Vertx vertx) {
        return new ProjectServiceVertxEBProxy(vertx, ADDRESS);
    }

    /*
     * Service to return all projects available
     */
    void getProjects(Handler<AsyncResult<List<Project>>> resulthandler);
    /*
     * Service to return project by specific projectId
     */
    void getProjectById(String projectId, Handler<AsyncResult<Project>> resulthandler);
    /*
     * Service to return projects with a specific status
     */
    void getProjectsByStatus(String projectStatus, Handler<AsyncResult<List<Project>>> resulthandler);

    /*
     * Service for health check
     */
    void ping(Handler<AsyncResult<String>> resultHandler);

}
