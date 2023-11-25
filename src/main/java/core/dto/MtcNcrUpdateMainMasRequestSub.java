package core.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MtcNcrUpdateMainMasRequestSub {
    private int sign; //부호 ( 입금이면 1 , 출금이면 -1)
    private double trxAmt; // 거래금액
    private String cur_c; // 통화코드
    private String trxdt;
}
