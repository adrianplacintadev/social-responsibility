package com.socialportal.portal.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.socialportal.portal.model.geo.UserLocation;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Column(name = "first_name")
    @Size(max = 40)
    private String firstName;

    @NotBlank
    @Column(name = "last_name")
    @Size(max = 40)
    private String lastName;

    @NotBlank
    @NaturalId
    @Column(name = "username", unique = true)
    @Size(max = 15)
    private String username;

    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(max = 100)
    @Column(name = "password")
    private String password;

    @NotBlank
    @Size(max = 40)
    @Column(name = "email", unique = true)
    @Email
    private String email;

    @NotBlank
    private String gender;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "geo_location")
    private UserLocation userLocation;

    @JsonIgnore
    @AssertFalse
    private boolean accepted;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private Roles role;

    @JsonIgnore
    @OneToOne(mappedBy = "userEntity", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private UserImage userImage;

}
