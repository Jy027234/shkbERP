package com.lframework.xingyun.shkb.dto;

import lombok.Data;

@Data
public class RemoteFolderDto {
    private String name;
    private String path;
    private Long ctime;
    private Long mtime;
    private Integer count;
}
