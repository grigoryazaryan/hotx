package app.hotx.model;

import java.io.Serializable;
import java.util.List;

public class VideosListResponse implements Serializable {
    List<PHSmallVideo> docs;
    int total;
    int limit;
    int page;
    int pages;

    public List<PHSmallVideo> getDocs() {
        return docs;
    }

    public void setDocs(List<PHSmallVideo> docs) {
        this.docs = docs;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }
}
