package com.gho.OAuth2ResourceServerClient.obj;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique=false, nullable = true)
    private String fileName;

    @Lob()
    //@Column(name = "photo", columnDefinition="BLOB")
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "document")
    @JsonIgnore
    private byte[] document;

    @ManyToOne
    @ToString.Exclude
    @JsonIgnoreProperties({"picture", "documents", "company"})
    @EqualsAndHashCode.Exclude
    private Employee employee;

    @ManyToOne
    @ToString.Exclude
    @JsonIgnoreProperties({"documents", "employees"})
    @EqualsAndHashCode.Exclude
    private Company company;

}
