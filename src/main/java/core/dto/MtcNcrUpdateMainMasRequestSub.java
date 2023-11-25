package core.dto;

<<<<<<< HEAD
import lombok.*;
=======
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
>>>>>>> a9f26c50b24ea910c702eebedccaf48d22dc0c3c

@Getter
@Setter
@ToString
<<<<<<< HEAD
@AllArgsConstructor
@NoArgsConstructor
=======
@JsonIgnoreProperties(ignoreUnknown = true)
>>>>>>> a9f26c50b24ea910c702eebedccaf48d22dc0c3c
public class MtcNcrUpdateMainMasRequestSub {
    private int sign; //부호 ( 입금이면 1 , 출금이면 -1)
    private double trxAmt; // 거래금액
    private String cur_c; // 통화코드
    private String trxdt;
}
