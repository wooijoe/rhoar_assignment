package com.freelancer.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.freelancer.model.Freelancer;

@Component
public class FreelancerServiceImpl implements FreelancerService {

	@Autowired
    FreelancerRepository repository;

	/*
	 *  By default Spring Data JPA will automatically parses the method name and creates a query from it. 
	 *  The query is implemented using the JPA criteria API. 
	 *  The parser that analyzes the method name supports quite a large set of keywords such as 
	 *  	And, Or, GreaterThan, LessThan, Like, IsNull, Not and so on. 
	 *  You can also add OrderBy clauses if you like. 
	 */
	public Freelancer getFreelancerById(String id) {
		return repository.getFreelancerById(id);
	}

	public List<Freelancer> getAllFreelancers() {
		//implementation automatically provided by JpaRepository
		//Can also implement with Pageable parameter to paginate.
		return repository.findAll();
	}
}