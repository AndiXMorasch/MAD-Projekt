package de.hsos.findyourdoc.logic;

public class DocModelBuilder {
    private String docName;
    private String date;
    private String time;
    private int image;
    private int remindTime;
    private boolean wasNotified;

    public static DocModelBuilder createBuilder() {
        return new DocModelBuilder();
    }

    public DocModelBuilder docName(String docName) {
        this.docName = docName;
        return this;
    }

    public DocModelBuilder date(String date) {
        this.date = date;
        return this;
    }

    public DocModelBuilder image(int image) {
        this.image = image;
        return this;
    }

    public DocModelBuilder time(String time) {
        this.time = time;
        return this;
    }

    public DocModelBuilder remindTime(int remindTime) {
        this.remindTime = remindTime;
        return this;
    }

    public DocModelBuilder wasNotified(boolean notified) {
        this.wasNotified = notified;
        return this;
    }

    public DocModel build() {
        return new DocModel(this.docName, this.image, this.date, this.time, this.remindTime, this.wasNotified);
    }
}
