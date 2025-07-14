package me.maxhub.logger;

public class Test {

    public static void test() {
        long start = System.currentTimeMillis();
        var walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
        var callerClass = walker.walk(frames ->
            frames.skip(1).findFirst().map(StackWalker.StackFrame::getDeclaringClass).orElse(null)
        );
        long end = System.currentTimeMillis();
        System.out.println(end - start);
        System.out.println("Called by: " + callerClass);
    }
}
