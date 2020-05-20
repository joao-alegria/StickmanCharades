using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.Networking;
using UnityEditor;
using System.IO;
using TMPro;
using SimpleJSON;

public class Create_Menu_AnimatorFunctions : AnimatorFunctions {

	private readonly string baseURL = "http://192.168.160.103:54880/";

	void executeOrder() {
		if(GameObject.Find("Btn Open Session").GetComponent<Animator>().GetCurrentAnimatorStateInfo(0).IsName("press")) {
			print("open Session"); ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
			
			TMP_InputField titleInput = GameObject.Find("TitleInput").GetComponent<TMP_InputField>();
			TMP_InputField durationInput = GameObject.Find("DurationInput").GetComponent<TMP_InputField>();
			if(int.TryParse(durationInput.text, out int durationInput_int)) {
				//string body = "{\"title\":\"" + titleInput.text + "\", \"duration\":" + durationInput.text + "}";
				JSONObject body = new JSONObject();
				body["title"] = titleInput.text;
				body["duration"] = durationInput_int;
				//print(body);
				StartCoroutine(PostGameSession(baseURL+"session", body, result => {
					Debug.Log(result);
				}));
			}
		} else if(GameObject.Find("Btn Start").GetComponent<Animator>().GetCurrentAnimatorStateInfo(0).IsName("press")) {
			print("Start"); //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			SessionData.KafkaTopic = "esp54_1"; //"actor0002";
			SessionData.KafkaProps = new Dictionary<string, string> {
				{ "group.id","test" },
				{ "bootstrap.servers", "192.168.160.103:9092" }, // "localhost:9092"
				{ "enable.auto.commit", "false" },
            	{ "auto.offset.reset", "latest" }
			};
			changeScene("Actor_Scene");
		} else if(GameObject.Find("Btn Invite Player").GetComponent<Animator>().GetCurrentAnimatorStateInfo(0).IsName("press")) {
			print("Invite Player"); //////////////////////////////////////////////////////////////////////////////////////////////////////////////
		} else if(GameObject.Find("Btn Go Back").GetComponent<Animator>().GetCurrentAnimatorStateInfo(0).IsName("press")) {
			print("Go Back");
			changeScene("Main_Menu_Scene");  /////////////////////////////////////////////////////////////////////////////////////////////////////
		}
	}

	IEnumerator PostGameSession(string URL, JSONObject body, Action<string> callback) {
		using (UnityWebRequest www = UnityWebRequest.Post(URL, body)) {
			www.SetRequestHeader("Content-Type", "application/json");
			yield return www.SendWebRequest();

			if (www.isNetworkError || www.isHttpError) {
				Debug.Log(www.error);
			} else {
				Debug.Log("Session opened!");
				callback?.Invoke(www.GetResponseHeader("Location"));
			}
		}
	}
	
}	
