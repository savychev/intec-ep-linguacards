package be.intecbrussel.linguacards.security;

import be.intecbrussel.linguacards.config.JwtProperties;
import be.intecbrussel.linguacards.entity.User;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Service
public class JwtTokenService {

    private final JwtProperties props;
    private final JwtEncoder jwtEncoder;

    public JwtTokenService(JwtProperties props) {
        this.props = props;
        this.jwtEncoder = buildEncoder(props.getSecret());
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(props.getExpirationMinutes() * 60);

        // issuer должен быть URL
        URI issuerUri = URI.create(props.getIssuer());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuerUri.toString())
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    private JwtEncoder buildEncoder(String secret) {
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        SecretKey key = new SecretKeySpec(secretBytes, "HmacSHA256");
        return new NimbusJwtEncoder(new ImmutableSecret<>(key));
    }
}