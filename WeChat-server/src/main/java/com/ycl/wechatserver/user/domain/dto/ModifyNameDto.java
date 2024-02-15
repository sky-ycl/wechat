package com.ycl.wechatserver.user.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModifyNameDto {

    @NonNull
    @ApiModelProperty("用户名")
    @Length(max = 6,message = "用户名可别取太长，不然我记不住噢")
    private String name;
}
