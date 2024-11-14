package com.codecrafter.commenting.domain.enumeration;

public enum NotificationType {
    LIKES("좋아요"),
    THANKED("고마워요"),
    COMMENT("답변"),
    QUESTION("질문");

    private final String label;

    NotificationType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
