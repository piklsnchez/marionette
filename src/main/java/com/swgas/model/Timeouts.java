package com.swgas.model;

import com.swgas.rest.Jsonable;
import java.io.StringReader;
import java.time.Duration;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class Timeouts implements Jsonable<Timeouts>{
    private Duration script;
    private Duration pageLoad;
    private Duration implicit;
    
    public Timeouts(){}
    
    public Timeouts(Duration script, Duration pageLoad, Duration implicit){
        this.script   = script;
        this.pageLoad = pageLoad;
        this.implicit = implicit;
    }

    /**
     * @return the script
     */
    public Duration getScript() {
        return script;
    }

    /**
     * @param script the script to set
     */
    public void setScript(Duration script) {
        this.script = script;
    }

    /**
     * @return the pageLoad
     */
    public Duration getPageLoad() {
        return pageLoad;
    }

    /**
     * @param pageLoad the pageLoad to set
     */
    public void setPageLoad(Duration pageLoad) {
        this.pageLoad = pageLoad;
    }

    /**
     * @return the implicit
     */
    public Duration getImplicit() {
        return implicit;
    }

    /**
     * @param implicit the implicit to set
     */
    public void setImplicit(Duration implicit) {
        this.implicit = implicit;
    }
    
    @Override
    public String toJson(){
        return toString();
    }
    
    @Override
    public Timeouts fromJson(String json){
        JsonObject _json = Json.createReader(new StringReader(json)).readObject();
        this.script      = Duration.parse(_json.getString("script"));
        this.pageLoad    = Duration.parse(_json.getString("pageLoad"));
        this.implicit    = Duration.parse(_json.getString("implicit"));
        return this;
    }
    
    public String toWebDriverString(){
        JsonObjectBuilder builder = Json.createObjectBuilder();
        if(null == script){
            builder.addNull("script");
        } else {
            builder.add("script", script.toMillis());
        }
        if(null == pageLoad){
            builder.addNull("pageLoad");
        } else {
            builder.add("pageLoad", pageLoad.toMillis());
        }
        if(null == implicit){
            builder.addNull("implicit");
        } else {
            builder.add("implicit", implicit.toMillis());
        }
        
        return builder.build().toString();
    }
    
    @Override
    public String toString(){
        JsonObjectBuilder builder = Json.createObjectBuilder();
        if(null == script){
            builder.addNull("script");
        } else {
            builder.add("script", script.toString());
        }
        if(null == pageLoad){
            builder.addNull("pageLoad");
        } else {
            builder.add("pageLoad", pageLoad.toString());
        }
        if(null == implicit){
            builder.addNull("implicit");
        } else {
            builder.add("implicit", implicit.toString());
        }
        
        return builder.build().toString();
    }
}
