package com.nelepovds.ndutils.rest;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by dmitrynelepov on 29.06.14.
 */
@Table(name = "nd_file")
public class NDFile extends BaseClass {

    @SerializedName(value = "filePath")
    @Column(name = "filePath")
    public String filePath;

    @SerializedName(value = "createTime")
    @Column(name = "createTime")
    public Date createTime;

    @SerializedName(value = "authorId")
    @Column(name = "authorId")
    public Long authorId;

    @SerializedName(value = "fileUrl")
    @Column(name = "fileUrl")
    public String fileUrl;
}
