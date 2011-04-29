package ru.mystamps.web.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
public class User {
	
	@Getter
	@Setter
	@Id
	@GeneratedValue
	private Integer id;
	
	@Getter
	@Setter
	@Column(length = 15, unique = true, nullable = false)
	private String login;
	
	@Getter
	@Setter
	@Column(length = 100, nullable = false)
	private String name;
	
	@Getter
	@Setter
	@Column(nullable = false)
	private String email;
	
	@Getter
	@Setter
	@Column(name = "registered_at", nullable = false)
	private Date registeredAt;
	
	@Getter
	@Setter
	@Column(name = "activated_at", nullable = false)
	private Date activatedAt;
	
	@Getter
	@Setter
	@Column(columnDefinition = "CHAR(40)", nullable = false)
	private String hash;
	
	@Getter
	@Setter
	@Column(columnDefinition = "CHAR(10)", nullable = false)
	private String salt;
	
}
