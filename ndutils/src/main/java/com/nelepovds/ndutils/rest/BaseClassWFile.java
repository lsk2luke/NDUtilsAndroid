package com.nelepovds.ndutils.rest;

import com.activeandroid.annotation.Column;
import com.activeandroid.query.Select;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dmitrynelepov on 02.07.14.
 */
public class BaseClassWFile extends BaseClass {


    @SerializedName(value = "fileId")
    @Column(name = "fileId")
    public Long fileId;

    private NDFile file;

    public NDFile getFile() {
        if (file == null && fileId != null && fileId > 0) {
            this.file = new Select().from(NDFile.class).where("id = ?", fileId).executeSingle();
        }
        return file;

    }

}
