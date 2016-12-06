package com.can.appstore.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * 广告请求参数
 */
public class CommonAdParam {
    private String adPositionId;
    private String versionId;
    private String mac;
    private String model;
    private String channel;
    private int member;
    private int playTypeId;
    private int contentId;
    private String area;
    private int topicId;
    private int programId;

    public CommonAdParam() {
        adPositionId = "adscqd";
        member = -1;
        playTypeId = -1;
        contentId = -1;
        topicId = -1;
        programId = -1;
    }

    public String getAdPositionId() {
        return adPositionId;
    }

    public void setAdPositionId(String adPositionId) {
        this.adPositionId = adPositionId;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public int getMember() {
        return member;
    }

    public void setMember(int member) {
        this.member = member;
    }

    public int getPlayTypeId() {
        return playTypeId;
    }

    public void setPlayTypeId(int playTypeId) {
        this.playTypeId = playTypeId;
    }

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public int getProgramId() {
        return programId;
    }

    public void setProgramId(int programId) {
        this.programId = programId;
    }

    public final Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        if (adPositionId != null) map.put("adpositionid", adPositionId);
        if (versionId != null) map.put("versionid", versionId);
        if (mac != null) map.put("mac", mac);
        if (model != null) map.put("model", model);
        if (channel != null) map.put("channel", channel);
        if (area != null) map.put("area", area);

        if (member >= 0) map.put("member", String.valueOf(member));
        if (playTypeId >= 0) map.put("playtypeid", String.valueOf(playTypeId));
        if (contentId >= 0) map.put("contentid", String.valueOf(playTypeId));
        if (topicId >= 0) map.put("topicid", String.valueOf(topicId));
        if (programId >= 0) map.put("programid", String.valueOf(programId));

        return map;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("CommonAdParam{");
        sb.append("adPositionId='").append(adPositionId).append('\'');
        sb.append(", versionId='").append(versionId).append('\'');
        sb.append(", mac='").append(mac).append('\'');
        sb.append(", model='").append(model).append('\'');
        sb.append(", channel='").append(channel).append('\'');
        sb.append(", member=").append(member);
        sb.append(", playTypeId=").append(playTypeId);
        sb.append(", contentId=").append(contentId);
        sb.append(", area='").append(area).append('\'');
        sb.append(", topicId=").append(topicId);
        sb.append(", programId=").append(programId);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommonAdParam that = (CommonAdParam) o;

        if (member != that.member) return false;
        if (playTypeId != that.playTypeId) return false;
        if (contentId != that.contentId) return false;
        if (topicId != that.topicId) return false;
        if (programId != that.programId) return false;
        if (adPositionId != null ? !adPositionId.equals(that.adPositionId) : that.adPositionId != null) return false;
        if (versionId != null ? !versionId.equals(that.versionId) : that.versionId != null) return false;
        if (mac != null ? !mac.equals(that.mac) : that.mac != null) return false;
        if (model != null ? !model.equals(that.model) : that.model != null) return false;
        if (channel != null ? !channel.equals(that.channel) : that.channel != null) return false;
        return area != null ? area.equals(that.area) : that.area == null;

    }

    @Override
    public int hashCode() {
        int result = adPositionId != null ? adPositionId.hashCode() : 0;
        result = 31 * result + (versionId != null ? versionId.hashCode() : 0);
        result = 31 * result + (mac != null ? mac.hashCode() : 0);
        result = 31 * result + (model != null ? model.hashCode() : 0);
        result = 31 * result + (channel != null ? channel.hashCode() : 0);
        result = 31 * result + member;
        result = 31 * result + playTypeId;
        result = 31 * result + contentId;
        result = 31 * result + (area != null ? area.hashCode() : 0);
        result = 31 * result + topicId;
        result = 31 * result + programId;
        return result;
    }
}
