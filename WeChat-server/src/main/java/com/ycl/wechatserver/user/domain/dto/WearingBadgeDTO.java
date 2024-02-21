package com.ycl.wechatserver.user.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WearingBadgeDTO {

    @ApiModelProperty("物品id")
    @NotNull
    private Long badgeId;
}
