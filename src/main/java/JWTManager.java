import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.security.SignatureException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



public class JWTManager {
    private static final String SECRET_KEY = "SECRET_PA$$WORD";
    public static String createJWT(HashMap<String , Object> claims , long expireDuration) {

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;


        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        Date date = new Date();
        JwtBuilder builder = Jwts.builder()
                .setExpiration(new Date(date.getTime()+expireDuration))
                .signWith(signatureAlgorithm, signingKey)
                .addClaims(claims);

        return builder.compact();
    }




    /**
     * This method throws ExpiredJwtException if the token was expired
     */
    public static Claims decodeJWT(String jwt) throws SignatureException {
        Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
                .parseClaimsJws(jwt).getBody();
        return claims;
    }
}