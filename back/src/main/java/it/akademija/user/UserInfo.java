package it.akademija.user;

public class UserInfo {

	private Long userId;
	private String role;
	private String name;
	private String surname;
	private String personalCode;
	private String address;
	private String city;
	private String phone;
	private String email;
	private String username;

	public UserInfo() {

	}

	public UserInfo(Long userId, String role, String username) {
		this.userId = userId;
		this.role = role;
		this.username = username;

	}

	public UserInfo(Long userId, String role, String name, String surname, String username, String email) {
		this.userId = userId;
		this.role = role;
		this.name = name;
		this.surname = surname;
		this.username = username;
		this.email = email;
	}

	public UserInfo(String role, String name, String surname, String personalCode, String address, String city, String phone,
			String email, String username) {
		super();
		this.role = role;
		this.name = name;
		this.surname = surname;
		this.address = address;
		this.city = city;
		this.personalCode = personalCode;
		this.phone = phone;
		this.email = email;
		this.username = username;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getPersonalCode() {
		return personalCode;
	}

	public void setPersonalCode(String personalCode) {
		this.personalCode = personalCode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}