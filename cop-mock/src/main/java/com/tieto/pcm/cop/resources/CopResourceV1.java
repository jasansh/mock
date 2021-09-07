package com.tieto.pcm.cop.resources;

import com.tieto.pcm.cop.model.CopStatusRequest;
import com.tieto.pcm.cop.model.CopStatusResponse;
import com.tieto.pcm.core.srv.web.resources.AbstractRs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping(path = "/api/payee")
public class CopResourceV1 extends AbstractRs {

    protected static final Logger logger = LoggerFactory.getLogger(CopResourceV1.class);
    private static final String RESPONSE_TIMEOUT_1_API_YES_IN_2_API = "55";
    private static final String RESPONSE_PARTIAL_IN_VERIFY_API_1_API_YES_IN_2_API = "77";
    private static final String RESPONSE_ERROR_IN_VERIFY_API_1_API_YES_IN_2_API = "88";
    private static final String RESPONSE_NO_IN_VERIFY_API_1_API_YES_IN_2_API = "99";

    private static final String RESPONSE_YES_IN_1_API_FAILURE_IN_2_API = "66";
    private static final String RESPONSE_NO_IN_1_API_FAILURE_IN_2_API = "44";
    private static final String RESPONSE_PARTIAL_IN_1_API_FAILURE_IN_2_API = "33";

    private static final String RESPONSE_NO_IN_1_API_TIMEOUT_IN_2_API = "22";
    private static final String RESPONSE_PARTIAL_IN_1_API_TIMEOUT_IN_2_API = "00";

    private static final String COP_ID_FOR_FAILURE_RESPONSE = "11111111-2222-3333-4444-55555555555";
    private static final String COP_ID_FOR_TIMEOUT_RESPONSE = "55555555-4444-3333-2222-11111111111";

    private static final String ERROR_RESPONSE = "E";
    private static final String NO_RESPONSE = "N";
    private static final String YES_RESPONSE = "M";
    private static final String PARTIAL_RESPONSE = "P";

    @PostMapping(path = "/{copId}/accept", consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
            produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CopStatusResponse> acceptCop(@PathVariable("copId") String copId) throws Exception {
        logger.debug("Received copId : {}", copId);
        CopStatusResponse copStatusResponse = null;

        if (!isCorrectCopId(copId)) {
            throw new Exception("Incorrect copId");
        }

        copStatusResponse = generateResponse(null, copId);

        logger.debug("CoP Response {} ", copStatusResponse);
        return new ResponseEntity<>(copStatusResponse, HttpStatus.OK);
    }

    @PostMapping(path = "", consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
            produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CopStatusResponse> payee(@Valid @RequestBody CopStatusRequest request) {
        logger.debug("Received request {} ", request);
        CopStatusResponse copStatusResponse = null;
        copStatusResponse = generateResponse(request, null);
        logger.debug("Payee Response {} ", copStatusResponse);
        return new ResponseEntity<>(copStatusResponse, HttpStatus.OK);
    }

    private CopStatusResponse generateResponse(CopStatusRequest request, String copId) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Instant instant = timestamp.toInstant();
        CopStatusResponse copStatusResponse = new CopStatusResponse();
        copStatusResponse.setRequestStatus(YES_RESPONSE);

        if (copId != null) {
            if (copId.equalsIgnoreCase(COP_ID_FOR_FAILURE_RESPONSE)) {
                copStatusResponse.setRequestStatus(ERROR_RESPONSE);
            } else if (copId.equalsIgnoreCase(COP_ID_FOR_TIMEOUT_RESPONSE)) {
                try {
                    Thread.sleep(4600);
                    copStatusResponse.setRequestStatus(ERROR_RESPONSE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            copStatusResponse.setCopId(copId);
        } else {
            copStatusResponse.setCopId(getCopId());
        }

        if (request != null && request.getAccountNumber() != null) {
            copStatusResponse.setRequestStatus(generateResponseStatus(request.getAccountNumber()));
            if ((request.getAccountNumber().indexOf(RESPONSE_NO_IN_1_API_FAILURE_IN_2_API) > -1) ||
                    (request.getAccountNumber().indexOf(RESPONSE_YES_IN_1_API_FAILURE_IN_2_API) > -1) ||
                    (request.getAccountNumber().indexOf(RESPONSE_PARTIAL_IN_1_API_FAILURE_IN_2_API) > -1)) {
                copStatusResponse.setCopId(COP_ID_FOR_FAILURE_RESPONSE);
            } else if ((request.getAccountNumber().indexOf(RESPONSE_NO_IN_1_API_TIMEOUT_IN_2_API) > -1) ||
                    (request.getAccountNumber().indexOf(RESPONSE_PARTIAL_IN_1_API_TIMEOUT_IN_2_API) > -1)) {
                copStatusResponse.setCopId(COP_ID_FOR_TIMEOUT_RESPONSE);
            }
        }

        copStatusResponse.setAcceptedTimestamp(instant.toString());
        copStatusResponse.setAcceptedBy("candice10");
        copStatusResponse.setRecheckRequired(false);
        return copStatusResponse;
    }

    private String generateResponseStatus(String accountNumber) {
        String requestStatus = YES_RESPONSE;
        if ((accountNumber.indexOf(RESPONSE_NO_IN_1_API_FAILURE_IN_2_API) > -1) ||
                (accountNumber.indexOf(RESPONSE_NO_IN_1_API_TIMEOUT_IN_2_API) > -1) ||
                (accountNumber.indexOf(RESPONSE_NO_IN_VERIFY_API_1_API_YES_IN_2_API) > -1)) {
            requestStatus = NO_RESPONSE;
        } else if ((accountNumber.indexOf(RESPONSE_PARTIAL_IN_1_API_TIMEOUT_IN_2_API) > -1) ||
                (accountNumber.indexOf(RESPONSE_PARTIAL_IN_1_API_FAILURE_IN_2_API) > -1) ||
                (accountNumber.indexOf(RESPONSE_PARTIAL_IN_VERIFY_API_1_API_YES_IN_2_API) > -1)) {
            requestStatus = PARTIAL_RESPONSE;
        } else if (accountNumber.indexOf(RESPONSE_ERROR_IN_VERIFY_API_1_API_YES_IN_2_API) > -1) {
            requestStatus = ERROR_RESPONSE;
        } else if (accountNumber.indexOf(RESPONSE_TIMEOUT_1_API_YES_IN_2_API) > -1) {
            try {
                Thread.sleep(4600);
                requestStatus = ERROR_RESPONSE;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return requestStatus;
    }

    private String getCopId() {
        List<String> copIds = copIds();
        Random r = new Random();
        int randomCopId = r.nextInt(copIds.size());
        String randomElement = copIds.get(randomCopId);
        return randomElement;
    }

    private List<String> copIds() {
        List<String> copIds = new ArrayList<>();
        copIds.add("9C26666A-B480-495A-BF2C-C9E0A9E0F39A");
        copIds.add("8C27777A-B500-5D4C-5621-B6E0AEF0F39A");
        copIds.add("154B5654-BF2C-125F-22AA-9AE0A9E0F39A");
        copIds.add("8AF8725C-4A56-445A-95AB-12E09C266669");
        copIds.add("9AC56B78-8CA7-495A-A56B-F7E0A9E0F39A");
        copIds.add("67BB7AA9-897A-E0F3-4A1B-425AAF8725CA");
        copIds.add("3AB45788-22AA-495A-557C-495AA9E0F39A");
        copIds.add("87C45A87-31B7-755A-FF23-C9E0A912F39A");
        copIds.add("5874125B-B4A2-49BA-D458-89E039E0F39A");
        return copIds;
    }

    private Boolean isCorrectCopId(String copId) {
        logger.debug("Received copId: {} ", copId);
        return ((copId.equalsIgnoreCase(COP_ID_FOR_FAILURE_RESPONSE)) || (copId.equalsIgnoreCase(COP_ID_FOR_TIMEOUT_RESPONSE))
                || (copIds().indexOf(copId.toUpperCase()) > -1));
    }
}
