package core.dto;

<<<<<<< HEAD
=======
import lombok.AllArgsConstructor;
>>>>>>> a9f26c50b24ea910c702eebedccaf48d22dc0c3c
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
<<<<<<< HEAD
public class MtcNcrUpdateMainMasRequest {
    private String acno;
    private String gid;
    private List<MtcNcrUpdateMainMasRequestSub> requestSubList;
=======
@AllArgsConstructor
public class MtcNcrUpdateMainMasRequest {
    private String acno;
    private String gid;
    private String aprvSno;
    private List<MtcNcrUpdateMainMasRequestSub> requestSubList;
    private MtcNcrPayRequest payInfo;
    private String svcid; // PAY , EXG 로 셋팅
>>>>>>> a9f26c50b24ea910c702eebedccaf48d22dc0c3c
}
