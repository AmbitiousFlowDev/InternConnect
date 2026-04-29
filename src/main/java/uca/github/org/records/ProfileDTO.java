package uca.github.org.records;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {
    private String firstName;
    private String lastName;
    private String description;
    private String education;
    private String skills;
    private String experience;
    private String preferences;
}