package com.tieto.portalapp.controller;

import com.tieto.portalapp.saml.SAMLGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;

@Controller
public class SAMLRequestProcessor {

    public static final String SAML_RESPONSE = "/saml-response";
    private static final Logger LOGGER = LoggerFactory.getLogger(SAMLRequestProcessor.class);

    @RequestMapping(value = "/saml-request", method = RequestMethod.POST)
    public String processSAMLRequest(@RequestParam("user-id") String userId, @RequestParam("app-url") String appURL,
                                     @RequestParam("saml-id") String samlId,
                                     @RequestParam("saml-issuer-id") String samlIssuerId,
                                     @RequestParam("keep-alive-url") String keepAliveURL,
                                     @RequestParam("userType") String userType,
                                     Model model, HttpServletRequest servletRequest) throws MalformedURLException {
        LOGGER.info(String.format("Processing SAML request for userId = %s and application URL = %s", userId, appURL));
        SAMLGenerator samlGenerator = new SAMLGenerator();
        String encodedSAMLResponse = samlGenerator.generateSAMLResponse(userId, samlIssuerId, servletRequest, userType, keepAliveURL);
        LOGGER.info(String.format("Encoded SAML token = %s", encodedSAMLResponse));

        model.addAttribute("samlEncodedToken", encodedSAMLResponse);
        model.addAttribute("appURL", appURL);
        model.addAttribute("samlId", samlId);
        //model.addAttribute("keepAliveURL", keepAliveURL);
        model.addAttribute("userType", userType);
        /*model.addAttribute("xSourceApp", xSourceApp);
        model.addAttribute("xTokenType", xTokenType);*/

        //Display in the response
        model.addAttribute("userId", userId);
        model.addAttribute("userGUID", samlGenerator.getUserGUID());
        model.addAttribute("keepAliveURL", keepAliveURL);
        model.addAttribute("samlIssuerId", samlIssuerId);

        LOGGER.info("Request completed successfully");
        return SAML_RESPONSE;
    }
}
