using nuitrack;
using UnityEngine;
using System.Collections.Generic;
using Confluent.Kafka;
using System.Text.RegularExpressions;
using System.Globalization;

public class RiggedAvatar : MonoBehaviour {
    [Header("Rigged model")]
    [SerializeField]
    ModelJoint[] modelJoints;

    /// <summary> Model bones </summary>
    Dictionary<JointType, ModelJoint> jointsRigged = new Dictionary<JointType, ModelJoint>();

    private IConsumer<int, string> kafkaConsumer;
    private const int commitPeriod = 5;

    private Skeleton avatarSkeleton;

    private Dictionary<string,JointType> jointMapping = new Dictionary<string,JointType>();

    private UnityEngine.Vector3 posModifier = new UnityEngine.Vector3(0.0f,0.0f,-7.5f);
    private UnityEngine.Vector3 posModifierPreview = new UnityEngine.Vector3(-1535.0f,-0.18f,-1287.0f);

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

        jointMapping.Add("Head",JointType.Head); jointMapping.Add("Neck",JointType.Neck); 
        jointMapping.Add("LeftCollar",JointType.LeftCollar); jointMapping.Add("Torso",JointType.Torso); 
        jointMapping.Add("Waist",JointType.Waist); jointMapping.Add("LeftShoulder",JointType.LeftShoulder); 
        jointMapping.Add("RightShoulder",JointType.RightShoulder); jointMapping.Add("LeftElbow",JointType.LeftElbow); 
        jointMapping.Add("RightElbow",JointType.RightElbow); jointMapping.Add("LeftWrist",JointType.LeftWrist); 
        jointMapping.Add("RightWrist",JointType.RightWrist); jointMapping.Add("LeftHand",JointType.LeftHand); 
        jointMapping.Add("RightHand",JointType.RightHand); jointMapping.Add("LeftHip",JointType.LeftHip); 
        jointMapping.Add("RightHip",JointType.RightHip); jointMapping.Add("LeftKnee",JointType.LeftKnee); 
        jointMapping.Add("LeftAnkle",JointType.LeftAnkle); jointMapping.Add("RightKnee",JointType.RightKnee); 
        jointMapping.Add("RightAnkle",JointType.RightAnkle);

        for (int i = 0; i < modelJoints.Length; i++) {
            modelJoints[i].baseRotOffset = modelJoints[i].bone.rotation;
            jointsRigged.Add(modelJoints[i].jointType.TryGetMirrored(), modelJoints[i]);
        }

        kafkaConsumer = new ConsumerBuilder<int, string>(SessionData.KafkaProps).Build();

        kafkaConsumer.Subscribe(new string[] { SessionData.KafkaTopic });

        //avatarSkeleton = new nuitrack.Skeleton();
        
    }

    void Update() {
        /*
        if (CurrentUserTracker.CurrentSkeleton != null) {

            //print(CurrentUserTracker.CurrentSkeleton.GetJoint(nuitrack.JointType.Head).Orient.Matrix[8]);

            ProcessSkeleton(CurrentUserTracker.CurrentSkeleton);
        }
        */
        
        try {
            var consumeResult = kafkaConsumer.Consume(1);

            if(consumeResult != null) {
                if (consumeResult.IsPartitionEOF) {
                    //print($"Reached end of topic {consumeResult.Topic}, partition {consumeResult.Partition}, offset {consumeResult.Offset}.");
                    //continue;
                    return;
                }

                //print($"Received message at {consumeResult.TopicPartitionOffset}: {consumeResult.Message.Value}");

                nuitrack.Joint[] tmp = ProcessKafkaMessage(consumeResult.Message.Value);
                //print(tmp[0].Orient.Matrix[8]);

                avatarSkeleton = new nuitrack.Skeleton(1234, tmp); // ProcessKafkaMessage(consumeResult.Message.Value));
                ProcessSkeleton(avatarSkeleton);

                if (consumeResult.Offset % commitPeriod == 0) {
                    try {
                        kafkaConsumer.Commit(consumeResult);
                    } catch (KafkaException e) {
                        print($"Commit error: {e.Error.Reason}");
                    }
                }
            }

        } catch (ConsumeException e) {
            print($"Consume error: {e.Error.Reason}");
        }
        
    }

    void ProcessSkeleton(Skeleton skeleton) {
        //Calculate the model position: take the Torso position and invert movement along the Z axis

        UnityEngine.Vector3 torsoPos = Quaternion.Euler(0f, 180f, 0f) * (-0.001f * skeleton.GetJoint(JointType.Torso).ToVector3());
        torsoPos[0] = torsoPos[0] * posModifier[0];
        for (int i = 0; i < 3; i++) {
            torsoPos[i] = torsoPos[i] + posModifier[i];
        }
        torsoPos = torsoPos + posModifierPreview;
        transform.position = torsoPos;

        foreach (var riggedJoint in jointsRigged) {
            try {
                //Get joint from the Nuitrack
                nuitrack.Joint joint = skeleton.GetJoint(riggedJoint.Key);

                ModelJoint modelJoint = riggedJoint.Value;

                //Calculate the model bone rotation: take the mirrored joint orientation, add a basic rotation of the model bone, invert movement along the Z axis
                Quaternion jointOrient = Quaternion.Inverse(CalibrationInfo.SensorOrientation) * (joint.ToQuaternion()) * modelJoint.baseRotOffset;

                modelJoint.bone.rotation = jointOrient;
            } catch {
                // Ignore IndexOutOfRangeException (it occurs because model has more available joints than those provided by orbbec)
            }
        }
    }

    nuitrack.Joint[] ProcessKafkaMessage(string value) {
        nuitrack.Joint[] skeletonJoints = new nuitrack.Joint[19];

        string[] partitions = Regex.Split(value, "\"orientations\"");

        string[] pos = Regex.Split(partitions[0], "\"");
        string[] ori = Regex.Split(partitions[1], "\"");
        string[] vecStr;

        int h = 2;
        int i;
        int j = 0;
        int k;
        for(i=5; i<pos.Length; i+=2) {

            vecStr = Regex.Split(Regex.Split(Regex.Split(pos[i+1], "\\[")[1], "\\]")[0], ",");
            nuitrack.Vector3 vec = new nuitrack.Vector3(
                float.Parse(vecStr[0], CultureInfo.InvariantCulture.NumberFormat),
                float.Parse(vecStr[1], CultureInfo.InvariantCulture.NumberFormat), 
                float.Parse(vecStr[2], CultureInfo.InvariantCulture.NumberFormat)
            );
            
            //print(ori[h]);
            string[] tmp1 = Regex.Split(ori[h], "\\[");
            string[] tmp2 = Regex.Split(tmp1[1], "\\]");
            vecStr = Regex.Split(tmp2[0], ","); h+=2;
            nuitrack.Orientation mat = new nuitrack.Orientation();
            mat.Matrix = new float[9];
            for(k=0; k<9; k++) {
                mat.Matrix[k] = float.Parse(vecStr[k], CultureInfo.InvariantCulture.NumberFormat);
                //print(mat.Matrix[k]);
            }

            nuitrack.Joint curJoint = new nuitrack.Joint();
            curJoint.Type = jointMapping[pos[i]];
            curJoint.Real = vec;
            curJoint.Orient = mat;

            skeletonJoints[j] = curJoint;
            j++; 
            //if(j==19) {print(j)};

        }
        return skeletonJoints;
    }
}