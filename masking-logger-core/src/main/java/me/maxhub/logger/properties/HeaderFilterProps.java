package me.maxhub.logger.properties;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class HeaderFilterProps {
    private Boolean enabled;
    private Set<String> include;
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Set<String> includeDefault = Set.of(); // todo

    public HeaderFilterProps() {
        this.enabled = false;
        this.include = Collections.emptySet();
    }

    public HeaderFilterProps(Boolean enabled, Set<String> include, Set<String> exclude) {
        this.enabled = enabled;
        this.include = Stream
            .concat(includeDefault.stream(), include.stream())
            .filter(header -> !exclude.contains(header))
            .collect(Collectors.toSet());
    }
}
