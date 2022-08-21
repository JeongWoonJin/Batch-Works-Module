package com.assignments.finalworks.domain;

import java.util.List;
import org.json.simple.JSONObject;

public interface Formatter {

    List<String> format(JSONObject response, List<String> route);
}
