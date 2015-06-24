package jp.mickey305.authtodosample;

public class NavigationDrawerItem {
    private int resId;
    private String name;
    private int type;

    public NavigationDrawerItem(int type) {
        this.type = type;
    }

    public NavigationDrawerItem(int resId, String name, int type) {
        this.resId = resId;
        this.name = name;
        this.type = type;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
