package com.gho.OAuth2ResourceServerClient.obj;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
//https://www.logicbig.com/tutorials/misc/jackson/json-identity-reference.html
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
//@JsonIdentityReference(alwaysAsId = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Picture {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique=false, nullable = true)
    private String fileName;

    @Lob()
    //@Column(name = "photo", columnDefinition="BLOB")
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "photo")
    @JsonIgnore
    private byte[] photo;

}
