
using System;
using System.IO;
using System.Collections;
using System.Collections.Generic;

using UnityEditor;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.Networking;
using UnityEngine.UI;

using TMPro;
using SimpleJSON;

public class Login_AnimatorFunctions : AnimatorFunctions {

	public Text warningMessage;

	private readonly string baseURL = "http://192.168.160.103:54882/";

	void executeOrder() {
		if(GameObject.Find("Btn Login").GetComponent<Animator>().GetCurrentAnimatorStateInfo(0).IsName("press")) {
			//print("Login");

			TMP_InputField usernameInput = GameObject.Find("UsernameInput").GetComponent<TMP_InputField>();
			TMP_InputField passwordInput = GameObject.Find("PasswordInput").GetComponent<TMP_InputField>();

			usernameInput.text = "filipe";
			passwordInput.text = "12345678";

			StartCoroutine(GetLogin(baseURL+"login", authenticate(usernameInput.text,passwordInput.text), token => {
				LoginData.Username = usernameInput.text;
				LoginData.Token = token;
				print(LoginData.Token);
				changeScene("Main_Menu_Scene");
			}));
		} else if(GameObject.Find("Btn Quit").GetComponent<Animator>().GetCurrentAnimatorStateInfo(0).IsName("press")) {
			print("Quit");
			//EditorApplication.isPlaying = false;
			Application.Quit();
		}
	}

	string authenticate(string username, string password) {
		string auth = username + ":" + password;
		auth = System.Convert.ToBase64String(System.Text.Encoding.GetEncoding("ISO-8859-1").GetBytes(auth));
		auth = "Basic " + auth;
		return auth;
	}

	IEnumerator GetLogin(string URL, string auth, Action<string> callback) {
		using (UnityWebRequest www = UnityWebRequest.Get(URL)) {
			UnityWebRequest.ClearCookieCache();
			www.SetRequestHeader("Content-Type", "application/json");
			www.SetRequestHeader("AUTHORIZATION", auth);
			yield return www.SendWebRequest();

			if (www.isNetworkError) {
				Debug.Log(www.error);
			} else if(www.isHttpError) {
				Debug.Log(www.error);
				warningMessage.enabled = true;
			} else {
				Debug.Log("Login successful!");
				warningMessage.enabled = false;
				string[] aux = www.GetResponseHeader("Set-Cookie").Split(';');
				callback?.Invoke(aux[0]);
			}
		}
	}
}	
