package models;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

@Data
@Entity
public class Balance
{
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;

    private BigDecimal accountBalance, usableAmount;
}
