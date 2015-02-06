package util;

/**
 * Created by philipp on 06.02.15.
 */
public class ImpressItem {


    private int icon;
    private String author;
    private String copyright;
    private String link;


    public ImpressItem(int icon, String author, String copyright, String link) {
        this.icon = icon;
        this.author = author;
        this.copyright = copyright;
        this.link = link;
    }


    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
