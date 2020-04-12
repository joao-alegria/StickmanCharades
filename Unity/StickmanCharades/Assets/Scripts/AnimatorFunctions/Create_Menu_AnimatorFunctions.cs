using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEditor;
using System.IO;

public class Create_Menu_AnimatorFunctions : AnimatorFunctions {

	void executeOrder() {
		if(GameObject.Find("Btn Open Session").GetComponent<Animator>().GetCurrentAnimatorStateInfo(0).IsName("press")) {
			print("open Session");
		} else if(GameObject.Find("Btn Start").GetComponent<Animator>().GetCurrentAnimatorStateInfo(0).IsName("press")) {
			print("Start");
			SessionData.KafkaTopic = "actor0002";
			SessionData.KafkaProps = new Dictionary<string, string> {
				{ "group.id","test" },
				{ "bootstrap.servers", "localhost:9092" },
				{ "enable.auto.commit", "false" },
            	{ "auto.offset.reset", "latest" }
			};
			changeScene("Actor_Scene");
		} else if(GameObject.Find("Btn Invite Player").GetComponent<Animator>().GetCurrentAnimatorStateInfo(0).IsName("press")) {
			print("Invite Player");
		} else if(GameObject.Find("Btn Go Back").GetComponent<Animator>().GetCurrentAnimatorStateInfo(0).IsName("press")) {
			print("Go Back");
			changeScene("Main_Menu_Scene");
		}
	}
	
}	
