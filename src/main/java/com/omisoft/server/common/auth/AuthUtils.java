package com.omisoft.server.common.auth;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.omisoft.server.common.exceptions.SecurityException;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;
import org.joda.time.DateTime;


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

  /**
   * Decode token
   *
   * @param authHeader authorization header
   * @return claim set
   * @throws ParseException Parsing error
   * @throws JOSEException sec problem
   * @throws SecurityException General security exception
   */
  public static JWTClaimsSet decodeToken(String authHeader)
      throws ParseException, JOSEException, SecurityException {
    SignedJWT signedJWT = SignedJWT.parse(authHeader);
    if (signedJWT.verify(new MACVerifier(TOKEN_SECRET))) {
      return signedJWT.getJWTClaimsSet();
    } else {
      throw new SecurityException("Signature verification failed");
    }
  }

  /**
   * Creates token
   *
   * @param host host name
   * @param sub subject
   * @return token
   */
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

  public static String createRegistryToken(String host, String email, int expirationDay) {
    JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
    JWTClaimsSet claim = builder.issuer(host)
        .subject(email)
        .expirationTime((DateTime.now().plusDays(expirationDay).toDate()))
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

  public static boolean expired(String token) {
    try {
      JWTClaimsSet jwt = decodeToken(token);
      return (new DateTime(jwt.getExpirationTime())).isBefore(DateTime.now());
    } catch (JOSEException | SecurityException | ParseException e) {
      e.printStackTrace();
    }
    return true;
  }

}
