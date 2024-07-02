package io.tutoriel.spring.garageApp.models;

import Role.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

//Lombok annotations trigger the generation of code during the compilation process, making the generated methods and constructors available when you run your application.
//The generated code is not visible in the source code files; it's only present in the compiled bytecode.
@Data
@Builder
@NoArgsConstructor // generates a no-argument constructor for your class
@AllArgsConstructor // generates a constructor that accepts all the fields of your class as parameters
@Entity
@Table(name = "_user")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private  String firstname;
    private String lastname;
    private  String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Token> tokens;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return  role.getAuthorities();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

}
