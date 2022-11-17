package com.gho.OAuth2ResourceServerClient.obj;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
//@Getter
//@Setter
//@ToString
@NoArgsConstructor
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Company {

    @ToString.Exclude
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = true)
    private String name;

    @ToString.Exclude
    @OneToMany(mappedBy = "company")
    @JsonIgnoreProperties({"company"})
    @EqualsAndHashCode.Exclude
    private Set<Employee> employees;

    @ToString.Exclude
    @OneToMany//(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "company_id")
    @JsonIgnoreProperties({"document"})
    @EqualsAndHashCode.Exclude
    private Set<Document> documents;


}
