package core.service;

import core.Repository.SdaMainMasRepository;
import core.apis.controller.MtcExgController;
import core.domain.SdaMainMas;
import core.domain.SdaMainMasId;
import core.dto.MtcExgRequest;
import core.dto.MtcExgResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MtcExgService {

    private final static Logger log = LoggerFactory.getLogger(MtcExgController.class);
    private final SdaMainMasRepository sdaMainMasRepository;

    // 환율 정보
    private final static Double USDExg = 1307.5;
    private final static Double JPYExg = 8.7;
    private final static Double CNYExg = 180.8;

    public MtcExgResponse exchangeService(MtcExgRequest exgRequest) {

        // 충전 결과
        MtcExgResponse exgResponse = new MtcExgResponse();

        try
        {
            /* main_mas 충전 요청 들어온 잔액 추가해주기 */
            // main_mas 원장 읽기
            SdaMainMas TrxMainMas = this.sdaMainMasRepository
                                        .findById(new SdaMainMasId(exgRequest.getAcno(), exgRequest.getCurC())).get();

            log.info("충전 전 외화({}) main_mas --> {} ",TrxMainMas.getCur_c(), TrxMainMas.toString());

            TrxMainMas.setAc_jan(TrxMainMas.getAc_jan() + exgRequest.getTrxAmt());

            // main_mas 업데이트
            TrxMainMas = this.sdaMainMasRepository.save(TrxMainMas);

            log.info("충전 후 외화({}) main_mas --> {} ",TrxMainMas.getCur_c(), TrxMainMas.toString());

            /* main_mas KRW 금액 빼주기 */
            SdaMainMas KRWMainMas = this.sdaMainMasRepository
                                        .findById(new SdaMainMasId(exgRequest.getAcno(), "KRW")).get();

            log.info("충전 전 원화(KRW) main_mas --> {} ", KRWMainMas.toString());

            Double trxKRWAmt = 0.0;
            if("USD".equals(TrxMainMas.getCur_c())) {
                trxKRWAmt = exgRequest.getTrxAmt() * USDExg;
            } else if("JPY".equals(TrxMainMas.getCur_c())) {
                trxKRWAmt = exgRequest.getTrxAmt() * JPYExg;
            } else if("CNY".equals(TrxMainMas.getCur_c())) {
                trxKRWAmt = exgRequest.getTrxAmt() * CNYExg;
            }

            log.info("충전에 필요한 원화 금액 --> {}", trxKRWAmt);

            // 만약 환전을 요청한 금액보다 원화가 적게 있다면 에러
            if (trxKRWAmt > KRWMainMas.getAc_jan()) {
                log.info("충전이 불가능해!!! 돈이 없어!!!");

                exgResponse.setResult(-1);
                exgResponse.setErrStr("충전 잔액이 부족합니다.");
            } else {
                KRWMainMas.setAc_jan(KRWMainMas.getAc_jan() - trxKRWAmt);

                // main_mas 업데이트
                KRWMainMas = this.sdaMainMasRepository.save(KRWMainMas);

                log.info("충전 후 원화(KRW) main_mas --> {} ", KRWMainMas.toString());

                exgResponse.setResult(0);
                exgResponse.setErrStr("충전 성공");

                // 충전 완료!!!
                log.info("충전 완료!!!");
            }
        }
        catch( Exception e)
        {
            log.info("충전 실패함 --> [{}]" , e.toString());
        }

        return exgResponse;
    }
}
