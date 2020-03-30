using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEditor;
using System.IO;

public class AnimatorFunctions : MonoBehaviour {

	[SerializeField] public MenuButtonController menuButtonController;
	public bool disableOnce;

	void executeOrder() {

	}
	
	protected void changeScene(string sceneName) {
		SceneManager.LoadScene(sceneName);
	}

}	
