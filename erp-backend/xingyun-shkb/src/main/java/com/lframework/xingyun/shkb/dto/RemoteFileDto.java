package com.lframework.xingyun.shkb.dto;

import lombok.Data;

@Data
public class RemoteFileDto {
    private String name;
    private String path;
    private Boolean ann;
    private Long size;
    private Long mtime;
}
