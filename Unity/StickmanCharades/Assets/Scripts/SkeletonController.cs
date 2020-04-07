using nuitrack;
using UnityEngine;
using System.Collections.Generic;
using System.IO;

public class SkeletonController : MonoBehaviour
{
    [Range(0, 6)]
    public int skeletonCount = 6;         //Max number of skeletons tracked by Nuitrack
    [SerializeField] SimpleSkeletonAvatar skeletonAvatar;

    List<SimpleSkeletonAvatar> avatars = new List<SimpleSkeletonAvatar>();

    private string path = "Assets/Resources/skeletonExample.txt";
    private StreamWriter writer;

    void OnEnable()
    {
        NuitrackManager.SkeletonTracker.OnSkeletonUpdateEvent += OnSkeletonUpdate;
    }

    void Start()
    {
        for (int i = 0; i < skeletonCount; i++)
        {
            GameObject newAvatar = Instantiate(skeletonAvatar.gameObject, transform, true);
            SimpleSkeletonAvatar simpleSkeleton = newAvatar.GetComponent<SimpleSkeletonAvatar>();
            simpleSkeleton.autoProcessing = false;
            avatars.Add(simpleSkeleton);
        }

        NuitrackManager.SkeletonTracker.SetNumActiveUsers(skeletonCount);

        writer = new StreamWriter(path, true);
    }

    void OnSkeletonUpdate(SkeletonData skeletonData)
    {
        for (int i = 0; i < avatars.Count; i++)
        {
            if (i < skeletonData.Skeletons.Length)
            {
                avatars[i].gameObject.SetActive(true);
                avatars[i].ProcessSkeleton(skeletonData.Skeletons[i]);
                //print(GetJoints(i));
                writer.WriteLine(GetJoints(i));
            }
            else
            {
                avatars[i].gameObject.SetActive(false);
            }
        }
    }

    string GetJoints(int index) {

        string data = "{";
        foreach (KeyValuePair<string, UnityEngine.Vector3> kvp in avatars[index].ExportJoints()) {
            data += string.Format("\"{0}\": [{1}], ", kvp.Key, kvp.Value[0] + "," + kvp.Value[1] + "," + kvp.Value[2] );
        }
        data = data.Substring(0, data.Length-2);
        data += "}";

        string retval = "{\"index\": " + index + " \"data\": " + data + "}"; //string.Format("{\"index\": {0}, \"data\": {1}}", index+"", data);
        return retval;
    }
}
