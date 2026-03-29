package com.github.vityan55.musicapp.web.controller.minio;


import com.github.vityan55.musicapp.service.data.AudioMetadataService;
import com.github.vityan55.musicapp.web.controller.AbstractWebTest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AbstractMinioWebTest extends AbstractWebTest {
    @MockitoBean
    private AudioMetadataService audioMetadataService;

    @BeforeEach
    void setupMock() {
        when(audioMetadataService.getDuration(any()))
                .thenReturn(180000);
    }
}
