package es_g54.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

/**
 *
 * @author joaoalegria
 */
@Service
public class SkeletonProcessor {
    
    public int rightHandOverHead=0;
    public int leftHandOverHead=0;

    void process(JSONObject json) {
        JSONArray head = ((JSONArray)((JSONObject)json.get("data")).get("Head"));
        JSONArray leftHand = ((JSONArray)((JSONObject)json.get("data")).get("LeftHand"));
        JSONArray rightHand = ((JSONArray)((JSONObject)json.get("data")).get("RightHand"));
        if(((double)rightHand.get(1))>((double)head.get(1))){
            rightHandOverHead++;
        }
        if(((double)leftHand.get(1))>((double)head.get(1))){
            leftHandOverHead++;
        }
    }
    
    
}
