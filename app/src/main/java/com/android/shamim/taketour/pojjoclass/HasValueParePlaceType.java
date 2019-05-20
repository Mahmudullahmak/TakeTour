package com.android.shamim.taketour.pojjoclass;

/**
 * Created by SAMIM on 2/8/2018.
 */

public class HasValueParePlaceType {
  private String  typeKey;
  private String keyValue;

    public HasValueParePlaceType(String typeKey, String keyValue) {
        this.typeKey = typeKey;
        this.keyValue = keyValue;
    }

    public String getTypeKey() {
        return typeKey;
    }

    public void setTypeKey(String typeKey) {
        this.typeKey = typeKey;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }
}
