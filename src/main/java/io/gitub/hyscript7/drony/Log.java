package io.gitub.hyscript7.drony;

public class Log {
    private final String name;

    public Log(String name) {
        this.name = name;
    }

    public synchronized void info(String msg) {
        if (name != null) {
            System.out.print("[" + name + "] ");
        }
        System.out.print(msg.stripTrailing() + "\n");
    }
}
