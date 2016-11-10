package cn.can.downloadlib.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by HEKANG on 2016/11/10.
 * 描述：下载实体类
 */
@Entity
public class DownloadDBEntity {

    @NotNull
    @Id
    private String downloadId;
    private Long totalSize;
    private Long downloadedSize;
    private String url;
    private String saveDirPath;
    private String fileName;
    private Integer downloadStatus;
    public Integer getDownloadStatus() {
        return this.downloadStatus;
    }
    public void setDownloadStatus(Integer downloadStatus) {
        this.downloadStatus = downloadStatus;
    }
    public String getFileName() {
        return this.fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getSaveDirPath() {
        return this.saveDirPath;
    }
    public void setSaveDirPath(String saveDirPath) {
        this.saveDirPath = saveDirPath;
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public Long getDownloadedSize() {
        return this.downloadedSize;
    }
    public void setDownloadedSize(Long downloadedSize) {
        this.downloadedSize = downloadedSize;
    }
    public Long getTotalSize() {
        return this.totalSize;
    }
    public void setTotalSize(Long totalSize) {
        this.totalSize = totalSize;
    }
    public String getDownloadId() {
        return this.downloadId;
    }
    public void setDownloadId(String downloadId) {
        this.downloadId = downloadId;
    }
    @Generated(hash = 1732717318)
    public DownloadDBEntity(@NotNull String downloadId, Long totalSize,
            Long downloadedSize, String url, String saveDirPath, String fileName,
            Integer downloadStatus) {
        this.downloadId = downloadId;
        this.totalSize = totalSize;
        this.downloadedSize = downloadedSize;
        this.url = url;
        this.saveDirPath = saveDirPath;
        this.fileName = fileName;
        this.downloadStatus = downloadStatus;
    }
    @Generated(hash = 1143139915)
    public DownloadDBEntity() {
    }

}
