package com.freelance.project.verticle.service;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.freelance.project.model.Project;
import com.freelance.project.verticle.service.ProjectService;
import com.freelance.project.verticle.service.ProjectServiceImpl;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class ProjectServiceTest extends MongoTestBase {

    private Vertx vertx;

    @Before
    public void setup(TestContext context) throws Exception {
        vertx = Vertx.vertx();
        vertx.exceptionHandler(context.exceptionHandler());
        JsonObject config = getConfig();
        mongoClient = MongoClient.createNonShared(vertx, config);
        
        //Create and returns a new async object, the returned async controls the completion of the test. 
        //Calling the io.vertx.rxjava.ext.unit.Async#complete completes the async operation. 
        //The test case will complete when all the async objects have their complete() method called at least once. 
        //This method shall be used for creating asynchronous exit points for the executed test.
        Async async = context.async();
        dropCollection(mongoClient, "products", async, context);
        //await: Cause the current thread to wait until this completion completes with a configurable timeout.
        async.await(10000);
        async.countDown();


    }

    @After
    public void tearDown() throws Exception {
        mongoClient.close();
        vertx.close();
    }


    @Test
    public void testGetProjects(TestContext context) throws Exception {
    	Async saveAsync = context.async(4);
    	ProjectService service = new ProjectServiceImpl(vertx, getConfig(), mongoClient);
    	service.getProjects(ar -> {
            if (ar.failed()) {
                context.fail(ar.cause().getMessage());
            } else {
            	Set<String> projectIds = ar.result().stream().map(p -> p.getProjectId()).collect(Collectors.toSet());
            	System.out.println("before projectIds:"+projectIds);
            	
            	for(Project proj: ar.result()){
            		System.out.println("before getProjectDescription:"+proj.getProjectDescription());
            	}
            }
        });
    	
    	String projectId1 = "project01";
        String projectId2 = "project02";
        String projectId3 = "project03";
        String projectId4 = "project04";
		JsonObject json1 = new JsonObject().put("projectId", projectId1).put("ownerFirstName", "ownerFirstName1GG")
				.put("ownerLastName", "ownerLastName1").put("ownerEmailAddress", "owner1@gmail.com")
				.put("projectTitle", "projectTitle1")
				.put("projectDescription", "projectDescription1").put("projectStatus", "in progress");
		JsonObject json2 = new JsonObject().put("projectId", projectId2).put("ownerFirstName", "ownerFirstName2")
				.put("ownerLastName", "ownerLastName2").put("ownerEmailAddress", "owner2@gmail.com")
				.put("projectTitle", "projectTitle2")
				.put("projectDescription", "projectDescription2").put("projectStatus", "open");
		
        mongoClient.save("projects", json1, ar -> {
            if (ar.failed()) {
                context.fail();
            }
            saveAsync.countDown();
        });


        mongoClient.save("projects", json2, ar -> {
            if (ar.failed()) {
                context.fail();
            }
            saveAsync.countDown();
        });
        JsonObject json3 = new JsonObject().put("projectId", projectId3).put("ownerFirstName", "ownerFirstName3")
				.put("ownerLastName", "ownerLastName3").put("ownerEmailAddress", "owner3@gmail.com")
				.put("projectTitle", "projectTitle3")
				.put("projectDescription", "projectDescription3").put("projectStatus", "in progress");
		JsonObject json4 = new JsonObject().put("projectId", projectId4).put("ownerFirstName", "ownerFirstName4")
				.put("ownerLastName", "ownerLastName4").put("ownerEmailAddress", "owner4@gmail.com")
				.put("projectTitle", "projectTitle4")
				.put("projectDescription", "projectDescription4").put("projectStatus", "open");

        mongoClient.save("projects", json3, ar -> {
            if (ar.failed()) {
                context.fail();
            }
            saveAsync.countDown();
        });

        mongoClient.save("projects", json4, ar -> {
            if (ar.failed()) {
                context.fail();
            }
            saveAsync.countDown();
        });
        saveAsync.await();
        Async async = context.async();

        service.getProjects(ar -> {
            if (ar.failed()) {
                context.fail(ar.cause().getMessage());
            } else {
            	Set<String> projectIds = ar.result().stream().map(p -> p.getProjectId()).collect(Collectors.toSet());
            	System.out.println("projectIds:"+projectIds);
            	
            	for(Project proj: ar.result()){
            		System.out.println("getProjectDescription:"+proj.getProjectDescription());
            	}
                assertThat(ar.result(), notNullValue());
                assertThat(projectIds.size(), equalTo(4));
                assertThat(projectIds, allOf(hasItem(projectId1),hasItem(projectId2),hasItem(projectId3),hasItem(projectId4)));
                async.complete();
            }
        });
    }
    
    @Test
    public void testGetProjectsByStatus(TestContext context) throws Exception {
    	Async saveAsync = context.async(2);
        String projectId2 = "project02";
        String projectId1 = "project01";
		JsonObject json1 = new JsonObject().put("projectId", projectId1).put("ownerFirstName", "ownerFirstName1GG")
				.put("ownerLastName", "ownerLastName1").put("ownerEmailAddress", "owner1@gmail.com")
				.put("projectTitle", "projectTitle1")
				.put("projectDescription", "projectDescription1").put("projectStatus", "in progress");
		JsonObject json2 = new JsonObject().put("projectId", projectId2).put("ownerFirstName", "ownerFirstName2")
				.put("ownerLastName", "ownerLastName2").put("ownerEmailAddress", "owner2@gmail.com")
				.put("projectTitle", "projectTitle2")
				.put("projectDescription", "projectDescription2").put("projectStatus", "open");
		
        mongoClient.save("projects", json1, ar -> {
            if (ar.failed()) {
                context.fail();
            }
            saveAsync.countDown();
        });


        mongoClient.save("projects", json2, ar -> {
            if (ar.failed()) {
                context.fail();
            }
            saveAsync.countDown();
        });
        saveAsync.await();
        ProjectService service = new ProjectServiceImpl(vertx, getConfig(), mongoClient);

        Async async = context.async();

        service.getProjectsByStatus("open", ar -> {
            if (ar.failed()) {
                context.fail(ar.cause().getMessage());
            } else {
                assertThat(ar.result(), notNullValue());
                assertThat(ar.result().size(), equalTo(1));
                Set<String> projectIds = ar.result().stream().map(p -> p.getProjectId()).collect(Collectors.toSet());
                assertThat(projectIds.size(), equalTo(1));
                assertThat(projectIds, allOf(hasItem(projectId2)));
                async.complete();
            }
        });
    }

    @Test
    public void testGetProjectById(TestContext context) throws Exception {
        String projectId3 = "project03";
        Async saveAsync = context.async(1);
        JsonObject json3 = new JsonObject().put("projectId", projectId3).put("ownerFirstName", "ownerFirstName3")
				.put("ownerLastName", "ownerLastName3").put("ownerEmailAddress", "owner3@gmail.com")
				.put("projectTitle", "projectTitle3")
				.put("projectDescription", "projectDescription3").put("projectStatus", "in progress");
        
        mongoClient.save("projects", json3, ar -> {
            if (ar.failed()) {
                context.fail();
            }
            saveAsync.countDown();
        });
        saveAsync.await();
        
        ProjectService service = new ProjectServiceImpl(vertx, getConfig(), mongoClient);

        Async async = context.async();
        
        service.getProjectById(projectId3, ar -> {
            if (ar.failed()) {
                context.fail(ar.cause().getMessage());
            } else {
                assertThat(ar.result(), notNullValue());
                assertThat(ar.result().getProjectId(), equalTo("project03"));
                assertThat(ar.result().getProjectTitle(), equalTo("projectTitle3"));
                async.complete();
            }
        });
    }

    @Test
    public void testGetNonExistingProject(TestContext context) throws Exception {

        ProjectService service = new ProjectServiceImpl(vertx, getConfig(), mongoClient);

        Async async = context.async();

        service.getProjectById("project99", ar -> {
            if (ar.failed()) {
                context.fail(ar.cause().getMessage());
            } else {
                assertThat(ar.result(), nullValue());
                async.complete();
            }
        });
    }

    @Test
    public void testPing(TestContext context) throws Exception {
        ProjectService service = new ProjectServiceImpl(vertx, getConfig(), mongoClient);

        Async async = context.async();
        service.ping(ar -> {
            assertThat(ar.succeeded(), equalTo(true));
            async.complete();
        });
    }

}
