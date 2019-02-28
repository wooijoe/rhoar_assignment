package com.freelancer.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.freelancer.model.Freelancer;
import com.freelancer.service.FreelancerService;

@Path("/freelancers")
@Component
public class FreelancersEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(FreelancersEndpoint.class);

    @Autowired
    private FreelancerService freelancerService;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Freelancer getFreelancerById(@PathParam("id") String freelancerId) {
        return freelancerService.getFreelancerById(freelancerId);
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Freelancer> getAllFreelancers() {
        return freelancerService.getAllFreelancers();
    }

}
