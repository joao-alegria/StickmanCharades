using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEditor;
using System.IO;

public class Join_Menu_AnimatorFunctions : AnimatorFunctions {

	void executeOrder() {
		if(GameObject.Find("Btn Join").GetComponent<Animator>().GetCurrentAnimatorStateInfo(0).IsName("press")) {
			print("Join");
			//changeScene("Actor_Scene");
		} else if(GameObject.Find("Btn Refresh").GetComponent<Animator>().GetCurrentAnimatorStateInfo(0).IsName("press")) {
			print("Refresh");
		} else if(GameObject.Find("Btn Go Back").GetComponent<Animator>().GetCurrentAnimatorStateInfo(0).IsName("press")) {
			print("Go Back");
			changeScene("Main_Menu_Scene");
		}
	}
	
}	
