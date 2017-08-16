package rest.internal.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@JsonInclude(NON_NULL)
public class MoneyTransferResponse {
    @NonNull
    private boolean success;
    private List<String> errorMessages;
}
