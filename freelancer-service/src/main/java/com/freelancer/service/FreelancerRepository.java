package com.freelancer.service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freelancer.model.Freelancer;

/*
 * Spring Data JPA provides a repository programming model that starts with an interface per managed domain object.
 * By extending JpaRepository we get a bunch of generic CRUD methods into our type.
 * 
 * We have type safe CRUD methods, query execution and pagination built right in. 
 * The cool thing is that this is not only working for JPA based repositories but also for non-relational databases.
 */
public interface FreelancerRepository extends JpaRepository<Freelancer, String> {

    public Freelancer getFreelancerById(String Id);

}
