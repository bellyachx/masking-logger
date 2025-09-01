package me.maxhub.logger.properties;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class HeaderFilterProps {
    private Boolean enabled;
    private Set<String> include = new HashSet<>();
    private Set<String> exclude = new HashSet<>();
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Set<String> includeDefault = Set.of(); // todo

    public HeaderFilterProps() {
        this.enabled = false;
        this.include = Collections.emptySet();
    }

    public void init() {
        this.include = Stream
            .concat(includeDefault.stream(), include.stream())
            .filter(header -> !exclude.contains(header))
            .collect(Collectors.toSet());

    }
}
