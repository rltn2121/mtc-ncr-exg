package mtc.ncr.apis;

import mtc.ncr.apis.dto.MtcNcrExgRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface MtcExgApi {

    @PostMapping("/exg")
    ResponseEntity<?> exchange(@RequestBody MtcNcrExgRequest exgRequest);
}
