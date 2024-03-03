package com.ycl.wechatserver.user.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BackDTO {


    @NotNull
    @ApiModelProperty("拉黑目标uid")
    private Long uid;

}
