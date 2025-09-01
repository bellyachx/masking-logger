package me.maxhub.logger.mask.impl.json.v2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.maxhub.logger.mask.Mask;

@Getter
@RequiredArgsConstructor
public class MaskedParameter {
    private final String name;
    @Mask
    private final Object value;
}
