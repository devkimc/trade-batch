package com.kr.economy.tradebatch.config;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SocketResultDto {

    private Header header;
    private Body body;

    @Getter
    @ToString
    public static class Header {
        private String tr_id;
        private String datetime;
        private String encrypt;
    }

    @Getter
    @ToString
    public static class Body {
        private String rt_cd;
        private String msg_cd;
        private String msg1;
        private OutPut output;
    }

    @Getter
    @ToString
    public static class OutPut {
        private String iv;
        private String key;
    }
}
