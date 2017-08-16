package models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
public class AccountLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private boolean limitReached;

    @NonNull
    @JsonSerialize(using = AmountSerializer.class)
    private BigDecimal transferLimit;

    @NonNull
    @JsonSerialize(using = AmountSerializer.class)
    private BigDecimal transferredToday;

    public void increaseTransferredToday(BigDecimal amount) {
        this.transferredToday = this.transferredToday.add(amount);
        if (transferredToday.compareTo(transferLimit) >= 0) limitReached = true;
    }
}
