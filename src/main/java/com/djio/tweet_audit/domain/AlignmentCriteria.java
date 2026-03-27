package com.djio.tweet_audit.domain;

import lombok.Data;
import java.util.List;

@Data
public class AlignmentCriteria {
    private List<String> forbiddenWords;
    private boolean professionalCheck;
    private String tone;
    private boolean excludePolitics;
}