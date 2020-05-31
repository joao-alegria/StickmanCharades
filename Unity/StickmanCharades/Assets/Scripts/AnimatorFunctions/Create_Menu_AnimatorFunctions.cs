
using System;
using System.IO;
using System.Collections;
using System.Collections.Generic;

using UnityEditor;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.Networking;

using TMPro;
using SimpleJSON;

public class Create_Menu_AnimatorFunctions : AnimatorFunctions {

	private readonly string baseURL = "http://192.168.160.103:54882/";
	private readonly string commandsURL = "http://192.168.160.103:54885/";

	void executeOrder() {
		if(GameObject.Find("Btn Open Session").GetComponent<Animator>().GetCurrentAnimatorStateInfo(0).IsName("press")) {
			print("open Session"); ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
			
			TMP_InputField titleInput = GameObject.Find("TitleInput").GetComponent<TMP_InputField>();
			TMP_InputField durationInput = GameObject.Find("DurationInput").GetComponent<TMP_InputField>();
			TMP_InputField wordsInput = GameObject.Find("WordsInput").GetComponent<TMP_InputField>();

			titleInput.text = "testing_01";
			durationInput.text = "600";
			wordsInput.text = "bola, arvore";

			if(int.TryParse(durationInput.text, out int durationInput_int)) {
				JSONArray words = new JSONArray(); 
				string[] words_raw = wordsInput.text.Split(',');
				for(int i=0; i<words_raw.Length; i++) {
					words.Add(words_raw[i]);
				}

				JSONObject body = new JSONObject();
				body["title"] = titleInput.text;
				body["duration"] = durationInput_int;
				body["words"] = words;
				
				StartCoroutine(PostGameSession(baseURL+"session", body.ToString(), LoginData.Token, sessionIdStr => {
					Debug.Log(sessionIdStr);
					JSONNode sessionIdJSON = JSONObject.Parse(sessionIdStr);
					int sessionId = sessionIdJSON["id"];
					Debug.Log(sessionId);
					SessionData.SessionId = sessionId;
				}));
			}
		} else if(GameObject.Find("Btn Start").GetComponent<Animator>().GetCurrentAnimatorStateInfo(0).IsName("press")) {
			print("Start"); //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			StartCoroutine(GetGameSession(commandsURL+"session/"+SessionData.SessionId+"/action/start", authenticate(LoginData.Username,"12345678"), response => {
				SessionData.KafkaTopic = "esp54_"+SessionData.SessionId; //"esp54_1"; //"actor0002";
				SessionData.KafkaProps = new Dictionary<string, string> {
					{ "group.id","test" },
					{ "bootstrap.servers", "192.168.160.103:9092" }, // "localhost:9092"
					{ "enable.auto.commit", "false" },
					{ "auto.offset.reset", "latest" }
				};
				changeScene("Actor_Scene");
			}));
		} else if(GameObject.Find("Btn Invite Player").GetComponent<Animator>().GetCurrentAnimatorStateInfo(0).IsName("press")) {
			print("Invite Player"); //////////////////////////////////////////////////////////////////////////////////////////////////////////////
		} else if(GameObject.Find("Btn Go Back").GetComponent<Animator>().GetCurrentAnimatorStateInfo(0).IsName("press")) {
			print("Go Back");
			changeScene("Main_Menu_Scene");  /////////////////////////////////////////////////////////////////////////////////////////////////////
		}
	}

	IEnumerator PostGameSession(string URL, string body, string token, Action<string> callback) {
		byte[] jsonToSend = new System.Text.UTF8Encoding().GetBytes(body);
		using (UnityWebRequest www = UnityWebRequest.Post(URL, body)) {
			www.uploadHandler = (UploadHandler)new UploadHandlerRaw(jsonToSend);
			www.downloadHandler = (DownloadHandler)new DownloadHandlerBuffer();
			www.SetRequestHeader("Cookie", token);
			www.SetRequestHeader("Content-Type", "application/json");

			yield return www.SendWebRequest();

			if (www.isNetworkError || www.isHttpError) {
				Debug.Log(www.error);
			} else {
				callback?.Invoke(www.downloadHandler.text);
			}
		}

		/*
		var uwr = new UnityWebRequest(URL, "POST");
		byte[] jsonToSend = new System.Text.UTF8Encoding().GetBytes(body);
		uwr.uploadHandler = (UploadHandler)new UploadHandlerRaw(jsonToSend);
		uwr.downloadHandler = (DownloadHandler)new DownloadHandlerBuffer();
		uwr.SetRequestHeader("Content-Type", "application/json");

		yield return uwr.SendWebRequest();

		if (www.isNetworkError || www.isHttpError) {
				Debug.Log(www.error);
		} else {
			callback?.Invoke(uwr.downloadHandler.text);
		}
		*/
	}

	string authenticate(string username, string password) {
		string auth = username + ":" + password;
		auth = System.Convert.ToBase64String(System.Text.Encoding.GetEncoding("ISO-8859-1").GetBytes(auth));
		auth = "Basic " + auth;
		return auth;
	}

	IEnumerator GetGameSession(string URL, string auth, Action<string> callback) {
		using (UnityWebRequest www = UnityWebRequest.Get(URL)) {
			www.SetRequestHeader("Content-Type", "application/json");
			www.SetRequestHeader("AUTHORIZATION", auth);
			yield return www.SendWebRequest();

			if (www.isNetworkError) {
				Debug.Log(www.error);
			} else if(www.isHttpError) {
				Debug.Log(www.error);
			} else {
				callback?.Invoke(www.downloadHandler.text);
			}
		}
	}
	
}	
