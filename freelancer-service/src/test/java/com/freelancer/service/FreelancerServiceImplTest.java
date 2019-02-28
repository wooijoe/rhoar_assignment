package com.freelancer.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import com.freelancer.model.Freelancer;

@RunWith(SpringRunner.class)
@DataJpaTest
public class FreelancerServiceImplTest {

    @Mock
    private FreelancerService freelancerService;

    @Before
    public void setup() {

        Freelancer freelancer = new Freelancer();
        freelancer.setId("jConnor");
        freelancer.setSkills("Scala");
        freelancer.setFirstname("John");
        freelancer.setLastname("Connor");

        List<Freelancer> freelancerList = new ArrayList();
        freelancerList.add(freelancer);
        System.out.println("freelancerService:"+freelancerService);
        Mockito.when(freelancerService.getFreelancerById("jConnor")).thenReturn(freelancer);
        Mockito.when(freelancerService.getAllFreelancers()).thenReturn(freelancerList);
    }


    @Test
    public void getAllFreelancers() throws Exception {


        List<Freelancer> freelancerList = freelancerService.getAllFreelancers();

        Freelancer johnConnor = freelancerList.get(0);
        assertThat(johnConnor, notNullValue());
        assertThat(johnConnor.getId(), notNullValue());
        assertThat(johnConnor.getFirstname(), equalTo("John"));
        assertThat(johnConnor.getLastname(), equalTo("Connor"));
        assertThat(johnConnor.getSkills(), equalTo("Scala"));
    }
    
    @Test
    public void getFreelancerById() throws Exception {


        Freelancer freelancer = freelancerService.getFreelancerById("jConnor");

        assertThat(freelancer, notNullValue());
        assertThat(freelancer.getId(), notNullValue());
        assertThat(freelancer.getFirstname(), equalTo("John"));
        assertThat(freelancer.getLastname(), equalTo("Connor"));
        assertThat(freelancer.getSkills(), equalTo("Scala"));
    }



}
