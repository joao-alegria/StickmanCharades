using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public static class SessionData {

    private static int sessionId;
    private static string kafkaTopic;
    private static Dictionary<string, string> kafkaProps;

    public static int SessionId {
        get {
            return sessionId;
        }
        set {
            sessionId = value;
        }
    }

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
