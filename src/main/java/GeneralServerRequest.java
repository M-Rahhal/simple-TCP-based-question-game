import com.fasterxml.jackson.annotation.JsonProperty;

public record GeneralServerRequest(
        @JsonProperty("token") String token,
        @JsonProperty("type") String type
) {
}
