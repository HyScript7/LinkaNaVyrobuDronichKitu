package io.gitub.hyscript7.drony;

public class Log {
    private final String name;
    private final Level level;

    public static enum Level {
        DEBUG(0),
        INFO(1),
        ERROR(2);

        private final int value;

        Level(int value) {
            this.value = value;
        }
    }

    public Log(String name) {
        this.name = name;
        this.level = Level.INFO;
    }

    public Log(String name, Level level) {
        this.name = name;
        this.level = level;
    }

    public synchronized void info(String msg) {
        if (level.value <= Level.INFO.value) {
            println(msg);
        }
    }

    public synchronized void debug(String msg) {
        if (level.value <= Level.DEBUG.value) {
            System.out.print("(DEBUG) ");
            println(msg);
        }
    }

    public synchronized void error(String msg) {
        if (level.value <= Level.ERROR.value) {
            System.out.print("(ERROR) ");
            println(msg);
        }
    }

    private void println(String msg) {
        if (name != null) {
            System.out.print("[" + name + "] ");
        }
        System.out.print(msg.stripTrailing() + "\n");
    }
}
