package models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import enums.Currency;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import utils.AmountSerializer;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@Data
@RequiredArgsConstructor
public class Balance {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NonNull
    private Currency currency;

    @NonNull
    @JsonSerialize(using = AmountSerializer.class)
    private BigDecimal amount;
}
