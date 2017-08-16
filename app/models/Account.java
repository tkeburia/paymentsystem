package models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import enums.Currency;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import utils.AmountSerializer;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;

@Entity
@NoArgsConstructor
@Data
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(fetch = EAGER, cascade = ALL)
    private List<Balance> balances;

    @NonNull
    @JsonSerialize(using = AmountSerializer.class)
    private BigDecimal allowedOverdraft;

    @NonNull
    @JsonSerialize(using = AmountSerializer.class)
    private BigDecimal usedOverdraft;

    private boolean locked;

    @OneToOne(cascade = ALL)
    @NonNull
    private AccountLimit accountLimit;

    public BigDecimal getAmountFor(Currency cur) {
        return getBalanceFor(cur).getAmount();
    }

    public void setAmountFor(Currency cur, BigDecimal amount) {
        getBalanceFor(cur).setAmount(amount);
    }

    public void increaseAmountFor(Currency cur, BigDecimal amount) {
        getBalanceFor(cur).setAmount(getAmountFor(cur).add(amount));
    }

    public void decreaseAmountFor(Currency cur, BigDecimal amount) {
        getBalanceFor(cur).setAmount(getAmountFor(cur).subtract(amount));
    }

    private Balance getBalanceFor(Currency cur) {
        return balances
            .stream()
            .filter(b -> b.getCurrency().equals(cur))
            .findFirst()
            .orElseThrow(
                    () -> new IllegalArgumentException(String.format("%s balance for Account %s not found!", cur.toString(), this.id))
            );
    }

    @JsonSerialize(using = AmountSerializer.class)
    public BigDecimal getRemainingOverdraft() {
        return allowedOverdraft.subtract(usedOverdraft);
    }

    public void incrementUsedOverdraft(BigDecimal amount) {
        this.usedOverdraft = this.usedOverdraft.add(amount);
    }

}
