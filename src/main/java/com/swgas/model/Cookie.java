package com.swgas.model;

import com.swgas.rest.Jsonable;
import java.io.StringReader;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonValue;

public class Cookie implements Jsonable<Cookie>{
    private static String CLASS = Cookie.class.getName();
    private static final Logger LOG = Logger.getLogger(CLASS);
    
    private String        name;
    private String        value;
    private String        path;
    private String        domain;
    private boolean       secure;
    private boolean       httpOnly;
    private LocalDateTime expiry;

    public Cookie(){}
    
    public Cookie(String name, String value, String path, String domain, boolean secure, boolean httpOnly, LocalDateTime expiry){
        this.name     = name;
        this.value    = value;
        this.path     = path;
        this.domain   = domain;
        this.secure   = secure;
        this.httpOnly = httpOnly;
        this.expiry   = expiry;
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * @param domain the domain to set
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * @return the secure
     */
    public boolean isSecure() {
        return secure;
    }

    /**
     * @param secure the secure to set
     */
    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    /**
     * @return the httpOnly
     */
    public boolean isHttpOnly() {
        return httpOnly;
    }

    /**
     * @param httpOnly the httpOnly to set
     */
    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    /**
     * @return the expiry
     */
    public LocalDateTime getExpiry() {
        return expiry;
    }

    /**
     * @param expiry the expiry to set
     */
    public void setExpiry(LocalDateTime expiry) {
        this.expiry = expiry;
    }
    
    @Override
    public String toJson(){
        return toString();
    }
    
    @Override
    public Cookie fromJson(String json){
        JsonObject cookie = Json.createReader(new StringReader(json)).readObject();
        this.name     = cookie.getString("name");
        this.value    = cookie.getString("value");
        this.path     = cookie.getString("path",      "/");
        this.domain   = cookie.getString("domain",    "");
        this.secure   = cookie.getBoolean("secure",   false);
        this.httpOnly = cookie.getBoolean("httpOnly", false);
        JsonValue exp = cookie.get("expiry");
        if(null == exp || cookie.isNull("expiry")){
            this.expiry = LocalDateTime.now().plus(20, ChronoUnit.YEARS);
        } else {
            try{
                this.expiry = LocalDateTime.ofEpochSecond(((JsonNumber)exp).longValue(), 0, ZoneOffset.UTC);
            } catch(DateTimeException e){
                this.expiry = LocalDateTime.MIN;
                LOG.warning(e.toString());
            }
        }
        return this;
    }
    
    @Override
    public String toString(){
        return Json.createObjectBuilder()
        .add("name",     Objects.toString(name,   ""))
        .add("value",    Objects.toString(value,  ""))
        .add("path",     Objects.toString(path,   ""))
        .add("domain",   Objects.toString(domain, ""))
        .add("secure",   secure)
        .add("httpOnly", httpOnly)
        .add("expiry",   Optional.ofNullable(expiry).orElseGet(()-> LocalDateTime.MIN).toEpochSecond(ZoneOffset.UTC))
        .build().toString();
    }
}
