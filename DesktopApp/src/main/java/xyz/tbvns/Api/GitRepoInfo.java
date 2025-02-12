package xyz.tbvns.Api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GitRepoInfo {
    private int stars;
    private String desc;
}
