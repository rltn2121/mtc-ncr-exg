package core.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MtcResultRequest {
    private Double nujkJan;
    private String acno;
    private String curC;
    private Double trxAmt;
    private String trxdt;
    private String aprvSno;
    private int upmuG;
    private String errMsg;
//    private MtcNcrPayRequest payinfo;
}
