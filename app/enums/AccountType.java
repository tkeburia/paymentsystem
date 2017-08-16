package enums;

import lombok.Getter;

public enum AccountType
{
    PREMIUM("premium"),
    STANDARD("standard");

    @Getter
    private final String value;

    AccountType(String value) {
        this.value = value;
    }
}
