
package com.example.demo.EventJSON;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "value1",
    "value2",
    "value3"
})
public class Values {

    @JsonProperty("value1")
    private String value1;
    @JsonProperty("value2")
    private String value2;
    @JsonProperty("value3")
    private String value3;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("value1")
    public String getValue1() {
        return value1;
    }

    @JsonProperty("value1")
    public void setValue1(String value1) {
        this.value1 = value1;
    }

    @JsonProperty("value2")
    public String getValue2() {
        return value2;
    }

    @JsonProperty("value2")
    public void setValue2(String value2) {
        this.value2 = value2;
    }

    @JsonProperty("value3")
    public String getValue3() {
        return value3;
    }

    @JsonProperty("value3")
    public void setValue3(String value3) {
        this.value3 = value3;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
