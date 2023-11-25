package core.apis.controller;

import core.apis.MtcExgApi;
import core.dto.MtcExgRequest;
import core.dto.MtcExgResponse;
import core.queue.ExgKafkaProducer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/* ----------------------------------------------------- */
/*  api 호출을 통해 화면단에서 충전 요청이 들어오는 경우          */
/*   1) 충전 일련번호 채번                                  */
/*   2) request 구조체에 충전 일련번호 세팅 후                */
/*      kafka에 topic : "mtc.ncr.core.exgRequest" 로 send */
/*   3) api 응답에 충전 일련번호 넣어서 response 해주기        */
/* ----------------------------------------------------- */

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class MtcExgController implements MtcExgApi {

    private final static Logger log = LoggerFactory.getLogger(MtcExgController.class);
    private final ExgKafkaProducer exgKafkaProducer;

    @Override
    public ResponseEntity<?> exchange(MtcExgRequest exgRequest) {

        MtcExgResponse exgResponse = new MtcExgResponse();
        String gid = callGidApi();
        log.info("@@@@@ gid: " + gid);

        try {
            // 충전 일련번호 채번 (아마 timestamp 를 붙이지 않을까 싶다)
            // String exgAcser = exgRequest.getPayAcser();
            String exgAcser = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

            log.info("충전 일련번호 : {}", exgAcser);

            // kafka send
            exgRequest.setAcser(exgAcser);
            exgKafkaProducer.produceMessage(exgRequest);

            exgResponse.setExgAcser(exgAcser);
        }
        catch (Exception e)
        {
            exgResponse.setResult(-1);
            exgResponse.setErrStr(e.toString());
        }

        return ResponseEntity.ok(exgResponse);
    }

    private String callGidApi() {
        return WebClient.create("http://mtc-com-log-svc.coc-mtc.svc.cluster.local:8080")
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/log/mkgid")
                        .queryParam("svcid", "exg")
                        .build()
                )
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
