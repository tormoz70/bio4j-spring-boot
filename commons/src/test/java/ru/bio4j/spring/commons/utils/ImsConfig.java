package ru.bio4j.spring.commons.utils;


import ru.bio4j.spring.model.transport.Prop;

public class ImsConfig extends SQLContextConfig {

    @Prop(name = "ims.screenplays.path")
    private String screenplaysPath = null;

    public String getScreenplaysPath() {
        return screenplaysPath;
    }

    public void setScreenplaysPath(String screenplaysPath) {
        this.screenplaysPath = screenplaysPath;
    }

}
