package com.ycl.wechatserver.websocket.domain.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WSBaseResp<T> {
    private Integer type;
    private T data;
}
