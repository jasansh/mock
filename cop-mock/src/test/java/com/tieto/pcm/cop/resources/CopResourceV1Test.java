package com.tieto.pcm.cop.resources;

import com.tieto.pcm.cop.model.CopStatusRequest;
import com.tieto.pcm.cop.model.CopStatusResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class CopResourceV1Test {

    @InjectMocks
    private CopResourceV1 copResource;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void payee_success_responseStatus(){
        ResponseEntity<CopStatusResponse> response = copResource.payee(getPayeeRequest());
        CopStatusResponse copStatusResponse = response.getBody();
        Assert.assertEquals("M", copStatusResponse.getRequestStatus());
        Assert.assertEquals("candice10", copStatusResponse.getAcceptedBy());
        Assert.assertEquals(false, copStatusResponse.getRecheckRequired());
    }

    @Test
    public void payee_partial_responseStatus(){
        CopStatusRequest request = new CopStatusRequest(new CopStatusRequest.Builder(
                "1234",
                "12345677",
                "ACCOUNT1",
                "private"));

        ResponseEntity<CopStatusResponse> response = copResource.payee(request);
        CopStatusResponse copStatusResponse = response.getBody();
        Assert.assertEquals("P", copStatusResponse.getRequestStatus());
    }

    @Test
    public void payee_error_responseStatus(){
        CopStatusRequest request = new CopStatusRequest(new CopStatusRequest.Builder(
                "1234",
                "12345688",
                "ACCOUNT1",
                "private"));

        ResponseEntity<CopStatusResponse> response = copResource.payee(request);
        CopStatusResponse copStatusResponse = response.getBody();
        Assert.assertEquals("E", copStatusResponse.getRequestStatus());
    }

    private CopStatusRequest getPayeeRequest(){
        return new CopStatusRequest(new CopStatusRequest.Builder(
                "1234",
                "12345678",
                "ACCOUNT1",
                "private"));
    }

    @Test
    public void accept_success_responseStatus() throws Exception {
        ResponseEntity<CopStatusResponse> response = copResource.acceptCop("9C26666A-B480-495A-BF2C-C9E0A9E0F39A");
        CopStatusResponse copStatusResponse = response.getBody();
        Assert.assertEquals("M", copStatusResponse.getRequestStatus());
        Assert.assertEquals("candice10", copStatusResponse.getAcceptedBy());
        Assert.assertEquals(false, copStatusResponse.getRecheckRequired());
    }

    @Test
    public void accept_error_responseStatus() throws Exception {
        ResponseEntity<CopStatusResponse> response = copResource.acceptCop("11111111-2222-3333-4444-55555555555");
        CopStatusResponse copStatusResponse = response.getBody();
        Assert.assertEquals("E", copStatusResponse.getRequestStatus());
    }

    @Test
    public void accept_Exception() throws Exception {
        exceptionRule.expect(Exception.class);
        copResource.acceptCop("invalid_cop");
    }
}