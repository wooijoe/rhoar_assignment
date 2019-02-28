package com.freelancer.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "freelancer", uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class Freelancer implements Serializable {

	private static final long serialVersionUID = -1108043957592113528L;

	@Id
	private String id;
	private String firstname;
	private String lastname;
	private String skills;

	public Freelancer() {
	}
	@Column(name = "id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "firstname")
	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstName) {
		this.firstname = firstName;
	}

	@Column(name = "lastname")
	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastName) {
		this.lastname = lastName;
	}

	@Column(name = "skills")
	public String getSkills() {
		return skills;
	}

	public void setSkills(String skills) {
		this.skills = skills;
	}

	@Override
	public String toString() {
		return "ShoppingCart [" + "id=" + id + "firstName=" + firstname + ", lastName=" + lastname + ", list of skills="
				+ skills + "]";
	}

}
