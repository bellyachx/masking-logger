package me.maxhub.logger.mask.impl.json.v2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.maxhub.logger.Mask;

@Getter
@RequiredArgsConstructor
public class MaskedParameter {
    @Mask
    private final Object value;
}
