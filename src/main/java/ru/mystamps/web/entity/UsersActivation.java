package ru.mystamps.web.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users_activation")
public class UsersActivation {
	
	public static final int ACTIVATION_KEY_LENGTH = 10;
	
	@Getter
	@Setter
	@Id
	@Column(name = "act_key", length = ACTIVATION_KEY_LENGTH)
	private String activationKey;
	
	@Getter
	@Setter
	@Column(nullable = false)
	private String email;
	
	@Getter
	@Setter
	@Column(name = "created_at", nullable = false)
	private Date createdAt;
	
}
