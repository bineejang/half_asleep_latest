package com.example.half_asleep;

public class GridItem {
    private String imageUri; // 이미지 경로 또는 이미지 자체 (필요에 따라서)
    // 다른 필요한 속성들

    public GridItem(String imageUri) {
        this.imageUri = imageUri;
        // 다른 속성 초기화
    }

    public String getImageUri() {
        final String imageUri = this.imageUri;
        return imageUri;
    }

    // 다른 getter 및 setter 메서드
}
