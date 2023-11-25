package core.queue;

import core.Repository.SdaMainMasRepository;
import core.domain.SdaMainMas;
import core.domain.SdaMainMasId;
import core.dto.*;
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
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ExgKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(ExgKafkaProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SdaMainMasRepository sdaMainMasRepository;
    private final MtcExgService exgService;

    // 환율 정보
    private final static Double USDExg = 1307.5;
    private final static Double JPYExg = 8.7;
    private final static Double CNYExg = 180.8;

    /* 충전 요청 큐 구독 ing */
    @KafkaListener(topics = "mtc.ncr.exgRequest", groupId = "practice22201653")
    public void consumeMessage(@Payload MtcExgRequest exgReqInfo ,
                               @Header(name = KafkaHeaders.RECEIVED_KEY , required = false) String key ,
                               @Header(KafkaHeaders.RECEIVED_TOPIC ) String topic ,
                               @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp ,
                               @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info ("@@영은충전 kafka 'mtc.ncr.exgRequest' 잡음! --> {}" , exgReqInfo.toString());

        // 충전 요청이 들어오면 환율 계산해서 com 큐에 넣기
        MtcNcrUpdateMainMasRequest comRequest = new MtcNcrUpdateMainMasRequest();
        comRequest.setAcno(exgReqInfo.getAcno());

        // 충전요청 들어온 금액은 더하고(sign: 1) 원화는 환율 계산해서 빼주기(sign: -1)
        Double KRWAmt = 0.0;
        if("USD".equals(exgReqInfo.getCurC())) {
            KRWAmt = exgReqInfo.getTrxAmt() * USDExg;
        } else if("JPY".equals(exgReqInfo.getCurC())) {
            KRWAmt = exgReqInfo.getTrxAmt() * JPYExg;
        } else if("CNY".equals(exgReqInfo.getCurC())) {
            KRWAmt = exgReqInfo.getTrxAmt() * CNYExg;
        }

        List<MtcNcrUpdateMainMasRequestSub> reqestList = new ArrayList<>();
        String acser = "";
        if ("Y".equals(exgReqInfo.getPayYn())) {
            acser = exgReqInfo.getPayInfo().getPayAcser();
        } else {
            acser = exgReqInfo.getAcser();
        }
        // 원화 먼저 빼기!
        reqestList.add(new MtcNcrUpdateMainMasRequestSub(-1, KRWAmt, "KRW", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) , acser));
        // 그다음 외화 충전!
        reqestList.add(new MtcNcrUpdateMainMasRequestSub(1, exgReqInfo.getTrxAmt(), exgReqInfo.getCurC(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) , acser));
        comRequest.setRequestSubList(reqestList);

        log.info("@@영은충전 com으로 요청보낼 정보!! --> {}", comRequest.toString());

        // com 큐로 충전 요청 send
        kafkaTemplate.send("mtc.ncr.comRequest", "KEY", comRequest);

        log.info("@영은충전 충전의 할 일은 여기까지~!");
    }
}
