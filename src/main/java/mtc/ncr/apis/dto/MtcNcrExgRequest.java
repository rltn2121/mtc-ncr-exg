package mtc.ncr.apis.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MtcNcrExgRequest {

    /* 업무구분             */
    /* 1 : 고객이 충전 요청  */
    /* 2 : 결제 중 충전 요청 */
    private Long upmuG;

    /* 계좌번호 (고객번호) */
    private String acno;

    /* 통화코드 */
    private String curC;

    /* 충전 금액 */
    private Long trxAmt;
}
