package be.intecbrussel.linguacards.config;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URI;

@ConfigurationProperties(prefix = "app.security.jwt")
@Validated
public class JwtProperties {

    @NotNull
    private URI issuer;

    @NotBlank
    @Size(min = 32, message = "must contain at least 32 characters for HS256")
    private String secret;

    @Min(1)
    private long expirationMinutes;

    public URI getIssuer() {
        return issuer;
    }

    public void setIssuer(URI issuer) {
        this.issuer = issuer;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpirationMinutes() {
        return expirationMinutes;
    }

    public void setExpirationMinutes(long expirationMinutes) {
        this.expirationMinutes = expirationMinutes;
    }

    @AssertTrue(message = "issuer must be an absolute HTTP(S) URI")
    public boolean isIssuerAbsoluteHttpUri() {
        if (issuer == null) {
            return true;
        }

        String scheme = issuer.getScheme();
        return issuer.isAbsolute()
                && ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme));
    }
}
