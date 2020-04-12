using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public static class SessionData {

    private static string kafkaTopic;
    private static Dictionary<string, string> kafkaProps;

    public static string KafkaTopic {
        get {
            return kafkaTopic;
        }
        set {
            kafkaTopic = value;
        }
    }

    public static Dictionary<string, string> KafkaProps {
        get {
            return kafkaProps;
        }
        set {
            kafkaProps = value;
        }
    }

}
