package com.omisoft.server.common.auth;


import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.omisoft.server.common.exceptions.SecurityException;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.util.Date;
import java.util.UUID;


/**
 * Created by nslavov on 5/10/16.
 */
public final class AuthUtils {

  //  public static final String AUTH_HEADER_KEY = "Authorization";
  private static final JWSHeader JWT_HEADER = new JWSHeader(JWSAlgorithm.HS256);
  private static final String TOKEN_SECRET = "vikaiZap0Moshtn8kolkoPuti112ksajdjskdmasdmaskmdasdmeqwqqe@#$#@$$@@$@#$@$@@#$#@$@38u483OFKKSDFKDSOFKSDOdkeewerjelw";

  // public static String getSubject(String authHeader) throws ParseException, JOSEException {
  // return decodeToken(authHeader).getSubject();
  // }

  public static JWTClaimsSet decodeToken(String authHeader)
      throws ParseException, JOSEException, SecurityException {
    SignedJWT signedJWT = SignedJWT.parse(authHeader);
    if (signedJWT.verify(new MACVerifier(TOKEN_SECRET))) {
      return signedJWT.getJWTClaimsSet();
    } else {
      throw new SecurityException("Signature verification failed");
    }
  }

  public static String createToken(String host, String sub) {
    JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
    JWTClaimsSet claim = builder.issuer(host)
        .subject(sub)
        .expirationTime((DateTime.now().plusDays(1).toDate()))
        .notBeforeTime(new Date())
        .issueTime(new Date()).jwtID(UUID.randomUUID().toString())
        .build();


    JWSSigner signer = null;
    try {
      signer = new MACSigner(TOKEN_SECRET);

      SignedJWT jwt = new SignedJWT(JWT_HEADER, claim);
      jwt.sign(signer);
      return jwt.serialize();
    } catch (JOSEException e) {
      e.printStackTrace();
    }
    return null;

  }

}
