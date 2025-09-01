package me.maxhub.logger.mask;

public class MaskSupport {

    private MaskSupport() {
    }

    public static String mask(String data) {
        int length = data.length();
        int start = (length * 30) / 100;
        int end = (length * 70) / 100;
        int maskLen = end - start;

        if (maskLen <= 0) {
            return data;
        }

        var mask = "*".repeat(maskLen);
        return data.substring(0, start) + mask + data.substring(start + maskLen);
    }
}
