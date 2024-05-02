package com.keshavarzi.airplanestats.security.jwt;

import io.jsonwebtoken.security.Keys;
import java.nio.charset.Charset;
import javax.crypto.SecretKey;

/** JWT Security Constants. */
public final class JwtSecurityConstants {
  public static final String TOKEN_PREFIX_BEARER = "Bearer ";
  public static final String NO_TOKEN = "";
  static final String AUTHORIZATION_HEADER = "Authorization";
  static final long JWT_EXPIRATION = 7000L;
  private static final String JWT_SECRET =
      "4=kGn++^Qf8uQy1ArP8nTv>Yt[cb1&wYn+2!hqxC&meh27)pWmMq7NNZ!nNMh?]P";
  static final SecretKey SECRET_KEY =
      Keys.hmacShaKeyFor(JWT_SECRET.getBytes(Charset.defaultCharset()));
}
