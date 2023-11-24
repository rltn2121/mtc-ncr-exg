package core.queue;

import core.Repository.SdaMainMasRepository;
import core.domain.SdaMainMas;
import core.domain.SdaMainMasId;
import core.dto.MtcExgRequest;
import core.dto.MtcExgResponse;
import core.dto.MtcNcrPayRequest;
import core.dto.MtcResultRequest;
import core.service.MtcExgService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class ExgKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(ExgKafkaProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SdaMainMasRepository sdaMainMasRepository;
    private final MtcExgService exgService;

    /* 충전 요청 큐 구독 ing */
    @KafkaListener(topics = "mtc.ncr.exgRequest", groupId = "practice22201653")
    public void consumeMessage(@Payload MtcExgRequest exgReqInfo ,
                               @Header(name = KafkaHeaders.RECEIVED_KEY , required = false) String key ,
                               @Header(KafkaHeaders.RECEIVED_TOPIC ) String topic ,
                               @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp ,
                               @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info ("kafka 'mtc.ncr.exgRequest' 잡음! --> {}" , exgReqInfo.toString());

        //요청받은 통화코드로 조회한 금액 정보
        SdaMainMas nowAcInfo = sdaMainMasRepository.
                                        findById(new SdaMainMasId(exgReqInfo.getAcno() , exgReqInfo.getCurC())).orElseThrow();

        log.info("현재 {} 금액 = {}" ,exgReqInfo.getCurC(), nowAcInfo);

        Double nowJan = nowAcInfo.getAc_jan(); // 현재 환전요청 들어온 통화의 금액

        // 결과 큐에 현재 정보 세팅하면서 만들기
        MtcResultRequest resultRequest = new MtcResultRequest(nowJan,exgReqInfo.getAcno(),exgReqInfo.getCurC(),exgReqInfo.getTrxAmt(),LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")),"",-1,"");

        try {
            if("Y".equals(exgReqInfo.getPayYn())) {
                // 결제 일련번호
                resultRequest.setAprvSno(exgReqInfo.getPayInfo().getPayAcser());
            } else {
                // 충전 일련번호
                resultRequest.setAprvSno(exgReqInfo.getAcser());
            }

            try {
                log.info("충전 시도 시작!");

                // 충전 프로세스
                MtcExgResponse exgResponse = exgService.exchangeService(exgReqInfo);

                if(exgResponse.getResult() == -1) {
                    // 충전 실패
                    resultRequest.setUpmuG(4);
                    resultRequest.setErrMsg(exgResponse.getErrStr());

                    // result 큐로 send
                    kafkaTemplate.send("mtc.ncr.result", "FAIL", resultRequest);
                } else {
                    // 충전 성공
                    resultRequest.setUpmuG(2);

                    // 결제에서 들어온 경우에는 결제큐와 결과큐에 모두 적재
                    if("Y".equals(exgReqInfo.getPayYn())) {
                        MtcNcrPayRequest payRequest = new MtcNcrPayRequest(exgReqInfo.getPayInfo().getAcno(),exgReqInfo.getPayInfo().getCurC(), exgReqInfo.getPayInfo().getTrxPlace(),exgReqInfo.getPayInfo().getTrxAmt(),exgReqInfo.getPayInfo().getTrxDt(),exgReqInfo.getPayInfo().getPayAcser());
                        kafkaTemplate.send("mtc.ncr.payRequest", "NEW", payRequest); // 결제 kew는 "NEW"
                    }
                    kafkaTemplate.send("mtc.ncr.result", "SUCCESS", resultRequest);
                }

            }
            catch (Exception e) {
                log.info("서비스 자체 error");

                // 충전 실패
                resultRequest.setUpmuG(4);
                resultRequest.setErrMsg("충전 중 에러가 발생했습니다. 다시 시도하세요.");

                // result 큐로 send
                kafkaTemplate.send("mtc.ncr.result", "FAIL", resultRequest);
            }
        }
        catch(Exception e) {
            log.info("서비스 자체 error");

            // 충전 실패
            resultRequest.setUpmuG(4);
            resultRequest.setErrMsg("충전 중 에러가 발생했습니다. 다시 시도하세요.");

            // result 큐로 send
            kafkaTemplate.send("mtc.ncr.result", "FAIL", resultRequest);
        }
    }
}
