package models;

import enums.AccountType;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
public class Account
{
    public Long id;

    private Balance balance;
    private Overdraft overDraft;
    private Limit limit;
    private AccountType accountType;
}
