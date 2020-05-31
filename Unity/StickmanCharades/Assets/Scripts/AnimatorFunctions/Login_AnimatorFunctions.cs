
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

public class Login_AnimatorFunctions : AnimatorFunctions {

	void executeOrder() {
		if(GameObject.Find("Btn Login").GetComponent<Animator>().GetCurrentAnimatorStateInfo(0).IsName("press")) {
			print("Login");

			TMP_InputField usernameInput = GameObject.Find("UsernameInput").GetComponent<TMP_InputField>();
			TMP_InputField passwordInput = GameObject.Find("PasswordInput").GetComponent<TMP_InputField>();


			changeScene("Main_Menu_Scene");
		} else if(GameObject.Find("Btn Quit").GetComponent<Animator>().GetCurrentAnimatorStateInfo(0).IsName("press")) {
			print("Quit");
			//EditorApplication.isPlaying = false;
			Application.Quit();
		}
	}

}	
