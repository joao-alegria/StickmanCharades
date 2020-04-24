using nuitrack;
using UnityEngine;
using System.Collections.Generic;
using System.IO;
using Confluent.Kafka;

public class SkeletonController : MonoBehaviour {
    [Range(0, 1)]
    public int skeletonCount = 1;         //Max number of skeletons tracked by Nuitrack
    [SerializeField] SimpleSkeletonAvatar skeletonAvatar;

    List<SimpleSkeletonAvatar> avatars = new List<SimpleSkeletonAvatar>();

    //private string path = "Assets/Resources/skeletonExample.txt";
    //private StreamWriter writer;

    private Dictionary<JointType, string> jointMapping = new Dictionary<JointType, string>();

    private IProducer<int, string> kafkaProducer;
    private int msgKey = 1;

    void OnEnable() {
        NuitrackManager.SkeletonTracker.OnSkeletonUpdateEvent += OnSkeletonUpdate;
    }

    void Start() {

        /* DEBUGGING 
        SessionData.KafkaTopic = "actor0002";
        SessionData.KafkaProps = new Dictionary<string, string> {
            { "group.id","test" },
            { "bootstrap.servers", "localhost:9092" },
            { "enable.auto.commit", "false" },
            { "auto.offset.reset", "latest" }
        };
        /* DEBUGGING */

        jointMapping.Add(JointType.Head,"Head"); jointMapping.Add(JointType.Neck,"Neck"); 
        jointMapping.Add(JointType.LeftCollar,"LeftCollar"); jointMapping.Add(JointType.Torso,"Torso"); 
        jointMapping.Add(JointType.Waist,"Waist"); jointMapping.Add(JointType.LeftShoulder,"LeftShoulder"); 
        jointMapping.Add(JointType.RightShoulder,"RightShoulder"); jointMapping.Add(JointType.LeftElbow,"LeftElbow"); 
        jointMapping.Add(JointType.RightElbow,"RightElbow"); jointMapping.Add(JointType.LeftWrist,"LeftWrist"); 
        jointMapping.Add(JointType.RightWrist,"RightWrist"); jointMapping.Add(JointType.LeftHand,"LeftHand"); 
        jointMapping.Add(JointType.RightHand,"RightHand"); jointMapping.Add(JointType.LeftHip,"LeftHip"); 
        jointMapping.Add(JointType.RightHip,"RightHip"); jointMapping.Add(JointType.LeftKnee,"LeftKnee"); 
        jointMapping.Add(JointType.LeftAnkle,"LeftAnkle"); jointMapping.Add(JointType.RightKnee,"RightKnee"); 
        jointMapping.Add(JointType.RightAnkle,"RightAnkle");

        for (int i = 0; i < skeletonCount; i++) {
            GameObject newAvatar = Instantiate(skeletonAvatar.gameObject, transform, true);
            SimpleSkeletonAvatar simpleSkeleton = newAvatar.GetComponent<SimpleSkeletonAvatar>();
            simpleSkeleton.autoProcessing = false;
            avatars.Add(simpleSkeleton);
        }

        NuitrackManager.SkeletonTracker.SetNumActiveUsers(skeletonCount);

        //writer = new StreamWriter(path, true);

        kafkaProducer = new ProducerBuilder<int, string>(SessionData.KafkaProps).Build();

        //GameObject.Find("PreviewCanvas").GetComponent<SkeletonConsumer>().presentActorSkeleton("Hello World!");
    }

    async void OnSkeletonUpdate(SkeletonData skeletonData) {
        for (int i = 0; i < avatars.Count; i++) {
            if(avatars[i] != null) {
                if (i < skeletonData.Skeletons.Length) {
                    avatars[i].gameObject.SetActive(true);
                    avatars[i].ProcessSkeleton(skeletonData.Skeletons[i]);
                    //print(GetJoints(i));
                    //writer.WriteLine(GetJoints(i));
                    try { var deliveryReport = await kafkaProducer.ProduceAsync(
                            SessionData.KafkaTopic, 
                            new Message<int, string> { Key = msgKey, Value = GetJoints(i) }
                        );
                        //print($"delivered to: {deliveryReport.TopicPartitionOffset}");

                    } catch (ProduceException<string, string> e) {
                        print($"failed to deliver message: {e.Message} [{e.Error.Code}]");
                    }
                    msgKey++;
                } else {
                    avatars[i].gameObject.SetActive(false);
                }
            }
        }
    }

    string GetJoints(int index) {

        int i;
        string positions = "{";
        string orientations = "{";
        nuitrack.Orientation o;
        string[] v;
        foreach (KeyValuePair<nuitrack.JointType, UnityEngine.Vector3> kvp in avatars[index].ExportJoints()) {
            v = new string[3];
            for(i=0;i<3;i++) { 
                //if(kvp.Value[i]==0) { v[i] = "0.0"; }  // v[i].ToString("0.0000")
                if(!(kvp.Value[i].ToString()).Contains(".")) { v[i] = kvp.Value[i] + ".0"; } 
                else { v[i] = kvp.Value[i].ToString(); }
            }
            positions += string.Format("\"{0}\": [{1}], ", jointMapping[kvp.Key], v[0] +","+ v[1] +","+ v[2]);

            o = CurrentUserTracker.CurrentSkeleton.GetJoint(kvp.Key).Orient;
            orientations += string.Format("\"{0}\": [{1}], ", jointMapping[kvp.Key], o.Matrix[0] +","+ o.Matrix[1] +","+ o.Matrix[2] +","+ o.Matrix[3] +","+ o.Matrix[4] +","+ o.Matrix[5] +","+ o.Matrix[6] +","+ o.Matrix[7] +","+ o.Matrix[8]);
        }
        positions = positions.Substring(0, positions.Length-2);
        positions += "}";
        orientations = orientations.Substring(0, orientations.Length-2);
        orientations += "}";

        string retval = "{\"index\": " + index + ", \"positions\": " + positions + ", \"orientations\": " + orientations + "}";
        //print(retval);
        return retval;
    }
}
