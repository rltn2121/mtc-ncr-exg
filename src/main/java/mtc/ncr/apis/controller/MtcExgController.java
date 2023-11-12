package mtc.ncr.apis.controller;

import lombok.RequiredArgsConstructor;
import mtc.ncr.apis.MtcExgApi;
import mtc.ncr.apis.dto.MtcNcrExgRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exg")
@RequiredArgsConstructor
public class MtcExgController implements MtcExgApi {

    private final static Logger log = LoggerFactory.getLogger(MtcExgController.class);

    @Override
    public ResponseEntity<?> exchange(MtcNcrExgRequest exgRequest) {


        return ResponseEntity.ok(exgRequest);
    }
}
