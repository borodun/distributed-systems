package hash.cracker.manager.types;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Task {
    private String hash;
    private int maxLength;
}
