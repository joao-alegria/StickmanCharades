using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEditor;
using System.IO;

public class Options_Menu_AnimatorFunctions : AnimatorFunctions {

	void executeOrder() {
		if(GameObject.Find("Btn Save").GetComponent<Animator>().GetCurrentAnimatorStateInfo(0).IsName("press")) {
			print("Save");
		} else if(GameObject.Find("Btn Go Back").GetComponent<Animator>().GetCurrentAnimatorStateInfo(0).IsName("press")) {
			print("Go Back");
			changeScene("Main_Menu_Scene");
		}
	}
	
}	
