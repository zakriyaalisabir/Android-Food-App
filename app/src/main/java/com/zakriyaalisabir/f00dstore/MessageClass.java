package com.zakriyaalisabir.f00dstore;

/**
 * Created by Zakriya Ali Sabir on 3/26/2018.
 */

public class MessageClass {
    private long time;
    private String  msg;
    private String to;
    private String from;

    MessageClass(){

    }

    public MessageClass( String msg,long time, String to, String from) {
        this.time = time;
        this.msg = msg;
        this.to = to;
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }



    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
