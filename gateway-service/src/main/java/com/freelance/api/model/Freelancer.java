package com.freelance.api.model;


public class Freelancer {
	private String id;
	private String firstname;
	private String lastname;
	private String skills;

	public Freelancer() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstName) {
		this.firstname = firstName;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastName) {
		this.lastname = lastName;
	}

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
