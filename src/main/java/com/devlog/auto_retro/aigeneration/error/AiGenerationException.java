package com.devlog.auto_retro.aigeneration.error;

import com.devlog.auto_retro.common.error.BusinessException;
import com.devlog.auto_retro.common.error.ErrorCode;

public class AiGenerationException extends BusinessException {
    public AiGenerationException(AiGenerationErrorCode errorCode) {
        super(errorCode);
    }
}
