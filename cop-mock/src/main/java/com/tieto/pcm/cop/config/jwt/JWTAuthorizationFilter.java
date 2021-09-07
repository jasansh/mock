package com.tieto.pcm.cop.config.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.json.JSONObject;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final String HEADER = "X-Request-Context";
    private final String SECRET = "correct-horse-battery-staple";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            if (checkJWTToken(request)) {
                String sub = validateToken(request);
                if (sub != null) {
                    setUpSpringAuthentication(sub);
                }else{
                    SecurityContextHolder.clearContext();
                }
            } else {
                SecurityContextHolder.clearContext();
            }
            chain.doFilter(request, response);
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            return;
        }
    }

    private String validateToken(HttpServletRequest request) {
        String jwtToken = request.getHeader(HEADER);
        String[] parts = jwtToken.split("\\.");
        JSONObject payload = new JSONObject(decode(parts[1]));
        String sub = payload.get("sub").toString().toLowerCase();
        if (payload.get("numeric-user-id") == null || payload.get("sub") == null) {
            if (!sub.contains("cesd_erb") && !sub.contains("cesd_enw")) {
                return null;
            }
        }

        String signature = parts[2];
        if(signature.equals(hmacSha256(parts[0] + "." + parts[1], SECRET))){
           return sub;
        }
        return null;
    }

    private static String encode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String decode(String encodedString) {
        return new String(Base64.getUrlDecoder().decode(encodedString));
    }

    private String hmacSha256(String data, String secret) {
        try {
            byte[] hash = secret.getBytes(StandardCharsets.UTF_8);
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec key = new SecretKeySpec(hash, "HmacSHA256");
            sha256Hmac.init(key);
            byte[] signedBytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return encode(signedBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            return null;
        }
    }



    /**
     * Authentication method in Spring flow
     *
     * @param sub
     */
    private void setUpSpringAuthentication(String sub) {
        // Collection<? extends GrantedAuthority> authorities =
        //     Arrays.stream(claims.get("sub").toString().split("@"))
        //              .map(SimpleGrantedAuthority::new)
        //              .collect(Collectors.toList());

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(sub, null,
                null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private boolean checkJWTToken(HttpServletRequest request) {
        String authenticationHeader = request.getHeader(HEADER);
        if (authenticationHeader == null)
            return false;
        return true;
    }

}
