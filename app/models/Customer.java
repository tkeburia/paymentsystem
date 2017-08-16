package models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

import static javax.persistence.CascadeType.ALL;

@Entity
@NoArgsConstructor
@Data
@RequiredArgsConstructor
public class Customer {

    @Id
    @NonNull
    private Long id;

    @NonNull
    private String firstName;

    @NonNull
    private String lastName;

    @OneToOne(cascade = ALL)
    private Account account;
}
