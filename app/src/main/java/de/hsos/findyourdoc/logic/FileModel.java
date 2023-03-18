package de.hsos.findyourdoc.logic;

public class FileModel {
    private final int id;
    private final int fileIcon;
    private final String docName;
    private final String uri;
    private final String fileName;

    public FileModel(int id, int fileIcon, String docName, String uri) {
        this.id = id;
        this.fileIcon = fileIcon;
        this.docName = docName;
        this.uri = uri;
        this.fileName = extractFileName(docName);
    }

    public int getId() {
        return id;
    }

    public int getFileIcon() {
        return this.fileIcon;
    }

    public String getUri() {
        return this.uri;
    }

    public String getDocName() {
        return this.docName;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String extractFileName(String docName) {
        return this.uri.substring(this.uri.lastIndexOf("%2F") + 3);
    }
}
