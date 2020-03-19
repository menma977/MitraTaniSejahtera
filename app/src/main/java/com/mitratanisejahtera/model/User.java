package com.mitratanisejahtera.model;

import android.content.Context;
import org.json.JSONObject;

public class User {
  private String token;
  private String username;
  private String img;
  private Integer status;
  private String type;
  private Cache cache;

  public User(Context context) {
    cache = new Cache(context);
    try {
      JSONObject auth = cache.get();
      setToken((String) auth.get("token"));
      setUsername((String) auth.get("username"));
      setImg((String) auth.get("image"));
      setStatus((Integer) auth.get("status"));
      setType((String) auth.get("type"));
    } catch (Exception e) {
      e.printStackTrace();
      setToken("");
      setUsername("");
      setImg("");
      setStatus(0);
      setType("");
    }
  }

  public void set(String value) {
    cache.set(value);
  }

  public void clear() {
    cache.remove();
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getImg() {
    return img;
  }

  public void setImg(String img) {
    this.img = img;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public String getType() {
    return type;
  }

  public void setType(String status) {
    this.type = type;
  }
}
