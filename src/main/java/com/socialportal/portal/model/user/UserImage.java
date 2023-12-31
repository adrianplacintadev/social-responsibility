package com.socialportal.portal.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.socialportal.portal.model.image.ImageData;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Setter;

@Entity
@Setter
public class UserImage extends ImageData {
    @JsonIgnore
    @OneToOne
    @JoinColumn(name="owner_id")
    private UserEntity userEntity;
}
