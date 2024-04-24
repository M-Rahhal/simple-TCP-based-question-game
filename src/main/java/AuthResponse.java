import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponse(
        @JsonProperty("state") String state,
        @JsonProperty("message") String message,
        @JsonProperty("token")String token
    ){}
