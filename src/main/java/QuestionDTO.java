import com.fasterxml.jackson.annotation.JsonProperty;

public record QuestionDTO(
        @JsonProperty("question") String question,
        @JsonProperty("choices") String[] choices,
        @JsonProperty("Points") int points,
        @JsonProperty("answer") int answer
    )
{}
