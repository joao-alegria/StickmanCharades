using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEditor;
using System.IO;

public class Main_Menu_AnimatorFunctions : AnimatorFunctions {

	void executeOrder() {
		if(GameObject.Find("Btn Create Session").GetComponent<Animator>().GetCurrentAnimatorStateInfo(0).IsName("press")) {
			print("Create Session");
			changeScene("Create_Menu_Scene");
		} else if(GameObject.Find("Btn Join Session").GetComponent<Animator>().GetCurrentAnimatorStateInfo(0).IsName("press")) {
			print("Join Session");
			changeScene("Join_Menu_Scene");
		} else if(GameObject.Find("Btn Options").GetComponent<Animator>().GetCurrentAnimatorStateInfo(0).IsName("press")) {
			print("Options");
			changeScene("Options_Menu_Scene");
		} else if(GameObject.Find("Btn Quit").GetComponent<Animator>().GetCurrentAnimatorStateInfo(0).IsName("press")) {
			print("Quit");
			//EditorApplication.isPlaying = false;
			Application.Quit();
		}
	}

}	
