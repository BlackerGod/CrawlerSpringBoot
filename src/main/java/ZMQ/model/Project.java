package ZMQ.model;

public class Project {
    private String name = "";

    private String url;

    private String descrption;

    private Integer starcount = 0;

    private Integer forkcount = 0;

    private Integer openissuecount = 0;

    private String date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public String getDescrption() {
        return descrption;
    }

    public void setDescrption(String descrption) {
        this.descrption = descrption == null ? null : descrption.trim();
    }

    public Integer getStarcount() {
        return starcount;
    }

    public void setStarcount(Integer starcount) {
        this.starcount = starcount;
    }

    public Integer getForkcount() {
        return forkcount;
    }

    public void setForkcount(Integer forkcount) {
        this.forkcount = forkcount;
    }

    public Integer getOpenissuecount() {
        return openissuecount;
    }

    public void setOpenissuecount(Integer openissuecount) {
        this.openissuecount = openissuecount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date == null ? null : date.trim();
    }
}