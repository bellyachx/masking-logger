package ch.qos.logback.core.rolling;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.rolling.helper.Compressor;
import ch.qos.logback.core.rolling.helper.FileNamePattern;
import ch.qos.logback.core.rolling.helper.RenameUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static ch.qos.logback.core.CoreConstants.CODES_URL;

public class IndexBasedRollingPolicy extends RollingPolicyBase {

    private static final String FNP_NOT_SET = "The FileNamePattern option must be set before using TimeBasedRollingPolicy. ";
    private static final String PRUDENT_MODE_UNSUPPORTED = "See also " + CODES_URL + "#tbr_fnp_prudent_unsupported";
    private static int MAX_WINDOW_SIZE = 20;

    @Getter
    @Setter
    private int minIndex;
    @Getter
    @Setter
    private int maxIndex;
    private int currentIndex;

    private RenameUtil util = new RenameUtil();

    @Override
    public void start() {
        util.setContext(this.context);

        if (Objects.nonNull(fileNamePatternStr)) {
            fileNamePattern = new FileNamePattern(fileNamePatternStr, this.context);
            determineCompressionMode();
        } else {
            addWarn(FNP_NOT_SET);
            addWarn(CoreConstants.SEE_FNP_NOT_SET);
            throw new IllegalStateException(FNP_NOT_SET + CoreConstants.SEE_FNP_NOT_SET);
        }

        currentIndex = computeLastIndex();


        if (isParentPrudent()) {
            addError("Prudent mode is not supported with FixedWindowRollingPolicy.");
            addError(PRUDENT_MODE_UNSUPPORTED);
            throw new IllegalStateException("Prudent mode is not supported.");
        }

        if (maxIndex < minIndex) {
            addWarn("MaxIndex (" + maxIndex + ") cannot be smaller than MinIndex (" + minIndex + ").");
            addWarn("Setting maxIndex to equal minIndex.");
            maxIndex = minIndex;
        }

        final int maxWindowSize = MAX_WINDOW_SIZE;
        if ((maxIndex - minIndex) > maxWindowSize) {
            addWarn("Large window sizes are not allowed.");
            maxIndex = minIndex + maxWindowSize;
            addWarn("MaxIndex reduced to " + maxIndex);
        }

        Compressor compressor = new Compressor(compressionMode);
        compressor.setContext(this.context);
        super.start();
    }

    @Override
    public void rollover() throws RolloverFailure {
        if (maxIndex < 0) {
            return;
        }

        if (maxIndex == currentIndex) {
            currentIndex = minIndex;
        } else {
            currentIndex += 1;
        }

        var file = new File(getActiveFileName());
        if (file.exists()) {
            deleteOldFile(file.toPath());
        }
    }

    @Override
    public String getActiveFileName() {
        return fileNamePattern.convertInt(currentIndex);
    }

    private int computeLastIndex() {
        long lastModified = -1;
        int idx = minIndex;
        for (int i = maxIndex; i >= minIndex; i--) {
            File file = new File(fileNamePattern.convertInt(i));
            if (file.exists()) {
                long tmpLM = file.lastModified();
                if (lastModified < tmpLM) {
                    idx = i;
                    lastModified = tmpLM;
                }
            }
        }
        return idx;
    }

    private void deleteOldFile(Path path) {
        try {
            Files.delete(path);
        } catch (IOException e) {
            addError("Could not delete file [" + path + "]", e);
        }
    }
}
