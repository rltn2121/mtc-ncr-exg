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

    public MtcExgResponse exchangeService(MtcExgRequest exgRequest) {

        // 충전 결과
        MtcExgResponse exgResponse = new MtcExgResponse();

        return exgResponse;
    }
}
