package core.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MtcNcrPayRequest {
    //고객번호 , 통화코드 , 거래처 , 금액 , 일시
    private String acno;
    private String curC;
    private String trxPlace;
    private Double trxAmt;
    private String trxDt;
    private String payAcser;
}
