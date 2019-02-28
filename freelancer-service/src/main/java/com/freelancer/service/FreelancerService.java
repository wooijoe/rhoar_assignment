package com.freelancer.service;

import java.util.List;

import com.freelancer.model.Freelancer;

public interface FreelancerService {

    public List<Freelancer> getAllFreelancers();

    public Freelancer getFreelancerById(String freelancerId);

}
